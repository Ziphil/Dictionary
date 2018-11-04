package ziphil.dictionary.exporter

import groovy.transform.CompileStatic
import javax.xml.stream.XMLStreamWriter
import javax.xml.transform.Transformer
import ziphil.dictionary.WordOrderType
import ziphil.dictionary.slime.SlimeEquivalent
import ziphil.dictionary.slime.SlimeDictionary
import ziphil.dictionary.slime.SlimeInformation
import ziphil.dictionary.slime.SlimeRelation
import ziphil.dictionary.slime.SlimeVariation
import ziphil.dictionary.slime.SlimeWord
import ziphil.module.Setting
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class SlimePdfExporter extends PdfExporter<SlimeDictionary, SlimePdfExportConfig> {

  private static final String XSLT_PATH = "resource/xsl/slime_pdf.xsl"

  public SlimePdfExporter(SlimePdfExportConfig config) {
    super(config)
  }

  protected void writeTemporary(XMLStreamWriter writer) {
    writer.writeStartDocument()
    writer.writeStartElement("words")
    String beforeInitialLetter = ""
    for (SlimeWord word : $dictionary.getRawSortedWords()) {
      String initialLetter = calculateInitialLetter(word.getName())
      if (initialLetter != beforeInitialLetter) {
        beforeInitialLetter = initialLetter
        writeCaption(writer, initialLetter)
      }
      writeWord(writer, word)
    }
    writer.writeEndElement()
    writer.writeEndDocument()
  }

  private String calculateInitialLetter(String name) {
    WordOrderType wordOrderType = $dictionary.getWordOrderType()
    if (wordOrderType == WordOrderType.CUSTOM) {
      for (String character : name) {
        if ($dictionary.getAlphabetOrder().indexOf(character) >= 0) {
          return character
        }
      }
      return ""
    } else if (wordOrderType == WordOrderType.UNICODE) {
      if (!name.isEmpty()) {
        return name[0]
      } else {
        return ""
      }
    } else {
      return ""
    }
  }

  private void writeCaption(XMLStreamWriter writer, String initialLetter) {
    writer.writeStartElement("caption")
    writer.writeCharacters(initialLetter)
    writer.writeEndElement()
  }

  private void writeWord(XMLStreamWriter writer, SlimeWord word) {
    writer.writeStartElement("word")
    writeEntry(writer, word)
    writeEquivalents(writer, word)
    writeTags(writer, word)
    writeInformations(writer, word)
    writeVariations(writer, word)
    writeRelations(writer, word)
    writer.writeEndElement()
  }

  private void writeEntry(XMLStreamWriter writer, SlimeWord word) {
    writer.writeStartElement("name")
    writer.writeCharacters(word.getName())
    writer.writeEndElement()
    writer.writeStartElement("number")
    writer.writeCharacters(word.getNumber().toString())
    writer.writeEndElement()
  }

  private void writeEquivalents(XMLStreamWriter writer, SlimeWord word) {
    writer.writeStartElement("equivalents")
    for (SlimeEquivalent equivalent : word.getRawEquivalents()) {
      writer.writeStartElement("equivalent")
      writer.writeStartElement("title")
      writer.writeCharacters(equivalent.getTitle())
      writer.writeEndElement()
      writer.writeStartElement("names")
      for (String name : equivalent.getNames()) {
        writer.writeStartElement("name")
        writer.writeCharacters(name)
        writer.writeEndElement()
      }
      writer.writeEndElement()
      writer.writeEndElement()
    }
    writer.writeEndElement()
  }

  private void writeTags(XMLStreamWriter writer, SlimeWord word) {
    writer.writeStartElement("tags")
    for (String tag : word.getTags()) {
      writer.writeStartElement("tag")
      writer.writeCharacters(tag)
      writer.writeEndElement()
    }
    writer.writeEndElement()
  }

  private void writeInformations(XMLStreamWriter writer, SlimeWord word) {
    writer.writeStartElement("informations")
    for (SlimeInformation information : word.sortedInformations()) {
      writer.writeStartElement("information")
      writer.writeStartElement("title")
      writer.writeCharacters(information.getTitle())
      writer.writeEndElement()
      writer.writeStartElement("text")
      writer.writeCharacters(information.getText())
      writer.writeEndElement()
      writer.writeEndElement()
    }
    writer.writeEndElement()
  }

  private void writeVariations(XMLStreamWriter writer, SlimeWord word) {
    writer.writeStartElement("variations")
    for (Map.Entry<String, List<SlimeVariation>> entry : word.groupedVariations()) {
      String title = entry.getKey()
      List<SlimeVariation> variationGroup = entry.getValue()
      writer.writeStartElement("variation")
      writer.writeStartElement("title")
      writer.writeCharacters(title)
      writer.writeEndElement()
      writer.writeStartElement("names")
      for (SlimeVariation variation : variationGroup) {
        writer.writeStartElement("name")
        writer.writeCharacters(variation.getName())
        writer.writeEndElement()
      }
      writer.writeEndElement()
      writer.writeEndElement()
    }
    writer.writeEndElement()
  }

  private void writeRelations(XMLStreamWriter writer, SlimeWord word) {
    writer.writeStartElement("relations")
    for (Map.Entry<String, List<SlimeRelation>> entry : word.groupedRelations()) {
      String title = entry.getKey()
      List<SlimeRelation> relationGroup = entry.getValue()
      writer.writeStartElement("relation")
      writer.writeStartElement("title")
      writer.writeCharacters(title)
      writer.writeEndElement()
      writer.writeStartElement("entries")
      for (SlimeRelation relation : relationGroup) {
        writer.writeStartElement("entry")
        writer.writeStartElement("number")
        writer.writeCharacters(relation.getNumber().toString())
        writer.writeEndElement()
        writer.writeStartElement("name")
        writer.writeCharacters(relation.getName())
        writer.writeEndElement()
        writer.writeEndElement()
      }
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
    transformer.setParameter("main-font-family", "${$config.getFirstMainFontFamily()}, ${$config.getSecondMainFontFamily()}")
    transformer.setParameter("main-font-size", "${$config.getMainFontSize()}pt")
    transformer.setParameter("title-font-size", "${$config.getMainFontSize() * 0.75}pt")
    transformer.setParameter("punctuation", $dictionary.firstPunctuation())
    transformer.setParameter("variation-marker", $config.getVariationMarker() ?: setting.getVariationMarker())
    transformer.setParameter("relation-marker", $config.getRelationMarker() ?: setting.getRelationMarker())
    transformer.setParameter("modifies", $config.getModifies())
  }

  protected String getXsltPath() {
    return XSLT_PATH
  }

}