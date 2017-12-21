package ziphil.dictionary.slime.exporter

import groovy.transform.CompileStatic
import javax.xml.stream.XMLStreamWriter
import javax.xml.stream.XMLOutputFactory
import ziphil.dictionary.DictionarySaver
import ziphil.dictionary.ExportConfig
import ziphil.dictionary.slime.SlimeEquivalent
import ziphil.dictionary.slime.SlimeDictionary
import ziphil.dictionary.slime.SlimeWord
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class SlimePdfDictionaryExporter extends DictionarySaver<SlimeDictionary> {

  private ExportConfig $config

  public SlimePdfDictionaryExporter(ExportConfig config) {
    super()
    $config = config
  }

  protected BooleanClass save() {
    String nextPath = $path.replaceAll(/\.\w+$/, "_temp.xml")
    saveAsXml(nextPath)
  }

  protected void saveAsXml(String nextPath) {
    File file = File.new(nextPath)
    BufferedWriter bufferedWriter = file.newWriter("UTF-8")
    XMLOutputFactory factory = XMLOutputFactory.newInstance()
    XMLStreamWriter writer = factory.createXMLStreamWriter(bufferedWriter)
    try {
      writer.writeStartDocument()
      writer.writeStartElement("words")
      for (SlimeWord word : $dictionary.getRawWords()) {
        writeWord(writer, word)
      }
      writer.writeEndElement()
      writer.writeEndDocument()
    } finally {
      writer.close()
      bufferedWriter.close()
    }
  }

  private void writeWord(XMLStreamWriter writer, SlimeWord word) {
    writer.writeStartElement("word")
    writeEntry(writer, word)
    writeEquivalents(writer, word)
    writer.writeEndElement()
  }

  private void writeEntry(XMLStreamWriter writer, SlimeWord word) {
    writer.writeStartElement("name")
    writer.writeCharacters(word.getName())
    writer.writeEndElement()
    writer.writeStartElement("id")
    writer.writeCharacters(word.getId().toString())
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

}