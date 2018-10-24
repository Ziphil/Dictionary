package ziphil.dictionary.exporter

import groovy.transform.CompileStatic
import javax.xml.stream.XMLStreamWriter
import javax.xml.transform.Transformer
import ziphil.dictionary.shaleia.ShaleiaDictionary
import ziphil.dictionary.shaleia.ShaleiaDescriptionReader
import ziphil.dictionary.shaleia.ShaleiaWord
import ziphil.module.Setting
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class ShaleiaPdfExporter extends PdfExporter<ShaleiaDictionary, ShaleiaPdfExportConfig> {

  private static final String XSLT_PATH = "resource/xsl/shaleia_pdf.xsl"

  public ShaleiaPdfExporter(ShaleiaPdfExportConfig config) {
    super(config)
  }

  protected void writeTemporary(XMLStreamWriter writer) {
    writer.writeStartDocument()
    writer.writeStartElement("words")
    String beforeInitialLetter = ""
    for (ShaleiaWord word : $dictionary.getRawSortedWords()) {
      if (!word.getName().startsWith("\$")) {
        String initialLetter = calculateInitialLetter(word.getName())
        if (initialLetter != beforeInitialLetter) {
          beforeInitialLetter = initialLetter
          writeCaption(writer, initialLetter)
        }
        writeWord(writer, word)
      }
    }
    writer.writeEndElement()
    writer.writeEndDocument()
  }

  private String calculateInitialLetter(String name) {
    for (String character : name) {
      if ($dictionary.getAlphabetOrder().indexOf(character) >= 0) {
        return character
      }
    }
    return ""
  }

  private void writeCaption(XMLStreamWriter writer, String initialLetter) {
    writer.writeStartElement("caption")
    writer.writeCharacters(initialLetter)
    writer.writeEndElement()
  }

  private void writeWord(XMLStreamWriter writer, ShaleiaWord word) {
    writer.writeStartElement("word")
    writeName(writer, word.getName(), word.getUniqueName())
    ShaleiaDescriptionReader reader = ShaleiaDescriptionReader.new(word.getDescription())
    List<List<String>> equivalents = ArrayList.new()
    List<List<String>> contents = ArrayList.new()
    List<List<String>> synonyms = ArrayList.new()
    try {
      while (reader.readLine() != null) {
        if (reader.findCreationDate()) {
          String totalPart = reader.lookupTotalPart()
          writeTotalPart(writer, totalPart)
        }
        if (reader.findEquivalent()) {
          String part = reader.lookupPart()
          String equivalent = reader.lookupEquivalent()
          equivalents.add([part, equivalent])
        }
        if (reader.findContent()) {
          String title = reader.title()
          String content = reader.lookupContent()
          contents.add([title, content])
        }
        if (reader.findSynonym()) {
          String synonymType = reader.lookupSynonymType()
          String synonym = reader.lookupSynonym()
          synonyms.add([synonymType, synonym])
        }
      }
    } finally {
      reader.close()
    }
    writeEquivalents(writer, equivalents)
    writeContents(writer, contents)
    writeSynonyms(writer, synonyms)
    writer.writeEndElement()
  }

  private void writeName(XMLStreamWriter writer, String name, String uniqueName) {
    writer.writeStartElement("name")
    writer.writeCharacters(name)
    writer.writeEndElement()
    writer.writeStartElement("unique-name")
    writer.writeCharacters(uniqueName)
    writer.writeEndElement()
  }

  private void writeTotalPart(XMLStreamWriter writer, String totalPart) {
    writer.writeStartElement("total-part")
    writer.writeCharacters(totalPart)
    writer.writeEndElement()
  }

  private void writeEquivalents(XMLStreamWriter writer, List<List<String>> equivalents) {
    writer.writeStartElement("equivalents")
    for (List<String> equivalent : equivalents) {
      writer.writeStartElement("equivalent")
      writer.writeStartElement("part")
      writer.writeCharacters(equivalent[0])
      writer.writeEndElement()
      writer.writeStartElement("equivalent")
      writer.writeCharacters(equivalent[1])
      writer.writeEndElement()
      writer.writeEndElement()
    }
    writer.writeEndElement()
  }

  private void writeContents(XMLStreamWriter writer, List<List<String>> contents) {
    writer.writeStartElement("contents")
    for (List<String> content : contents) {
      writer.writeStartElement("content")
      writer.writeStartElement("title")
      writer.writeCharacters(content[0])
      writer.writeEndElement()
      writer.writeStartElement("content")
      writer.writeCharacters(content[1])
      writer.writeEndElement()
      writer.writeEndElement()
    }
    writer.writeEndElement()
  }

  private void writeSynonyms(XMLStreamWriter writer, List<List<String>> synonyms) {
    writer.writeStartElement("synonyms")
    for (List<String> synonym : synonyms) {
      writer.writeStartElement("synonym")
      writer.writeStartElement("synonym-type")
      writer.writeCharacters(synonym[0])
      writer.writeEndElement()
      writer.writeStartElement("synonym")
      writer.writeCharacters(synonym[1])
      writer.writeEndElement()
      writer.writeEndElement()
    }
    writer.writeEndElement()
  }

  protected void setupTransformer(Transformer transformer) {
    Setting setting = Setting.getInstance()
    transformer.setParameter("caption-font-family", "${$config.getFirstCaptionFontFamily()}, ${$config.getSecondCaptionFontFamily()}")
    transformer.setParameter("caption-font-size", "${$config.getCaptionFontSize()}pt")
    transformer.setParameter("head-font-family", "${$config.getFirstHeadFontFamily()}, ${$config.getSecondHeadFontFamily()}")
    transformer.setParameter("head-font-size", "${$config.getHeadFontSize()}pt")
    transformer.setParameter("shaleia-font-family", "${$config.getFirstShaleiaFontFamily()}, ${$config.getSecondShaleiaFontFamily()}")
    transformer.setParameter("shaleia-font-size", "${$config.getShaleiaFontSize()}pt")
    transformer.setParameter("main-font-family", "${$config.getFirstMainFontFamily()}, ${$config.getSecondMainFontFamily()}")
    transformer.setParameter("main-font-size", "${$config.getMainFontSize()}pt")
    transformer.setParameter("title-font-size", "${$config.getMainFontSize() * 0.75}pt")
    transformer.setParameter("relation-marker", $config.getRelationMarker() ?: setting.getRelationMarker())
    transformer.setParameter("modifies", $config.getModifies())
  }

  protected String getXsltPath() {
    return XSLT_PATH
  }

}