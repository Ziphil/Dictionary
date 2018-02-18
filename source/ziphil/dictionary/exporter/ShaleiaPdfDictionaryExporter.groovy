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
public class ShaleiaPdfDictionaryExporter extends PdfDictionaryExporter<ShaleiaDictionary, PdfExportConfig> {

  private static final String XSLT_PATH = "resource/xsl/shaleia_pdf.xsl"

  public ShaleiaPdfDictionaryExporter(PdfExportConfig config) {
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
    List<String> synonyms = ArrayList.new()
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
          String synonym = reader.lookupSynonym()
          synonyms.add(synonym)
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

  private void writeSynonyms(XMLStreamWriter writer, List<String> synonyms) {
    writer.writeStartElement("synonyms")
    for (String synonym : synonyms) {
      writer.writeStartElement("synonym")
      writer.writeCharacters(synonym)
      writer.writeEndElement()
    }
    writer.writeEndElement()
  }

  protected void setupTransformer(Transformer transformer) {
    Setting setting = Setting.getInstance()
    transformer.setParameter("punctuation", ", ")
    transformer.setParameter("relation-marker", setting.getRelationMarker())
  }

  protected String getXsltPath() {
    return XSLT_PATH
  }

}