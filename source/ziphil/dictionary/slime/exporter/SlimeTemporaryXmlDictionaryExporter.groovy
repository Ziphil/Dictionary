package ziphil.dictionary.slime.exporter

import groovy.transform.CompileStatic
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method
import java.lang.reflect.Proxy
import javax.xml.stream.XMLStreamWriter
import javax.xml.stream.XMLOutputFactory
import ziphil.dictionary.DictionarySaver
import ziphil.dictionary.slime.SlimeEquivalent
import ziphil.dictionary.slime.SlimeDictionary
import ziphil.dictionary.slime.SlimeInformation
import ziphil.dictionary.slime.SlimeRelation
import ziphil.dictionary.slime.SlimeVariation
import ziphil.dictionary.slime.SlimeWord
import ziphilib.transform.InnerClass
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public abstract class SlimeTemporaryXmlDictionaryExporter extends DictionarySaver<SlimeDictionary> {

  protected String $temporaryPath

  public SlimeTemporaryXmlDictionaryExporter() {
    super()
  }

  protected BooleanClass save() {
    $temporaryPath = $path.replaceAll(/\.\w+$/, "_temp.xml")
    BooleanClass temporaryResult = saveTemporary()
    BooleanClass mainResult = saveMain()
    return temporaryResult && mainResult
  }

  protected abstract BooleanClass saveMain()

  protected BooleanClass saveTemporary() {
    File file = File.new($temporaryPath)
    BufferedWriter bufferedWriter = file.newWriter("UTF-8")
    XMLOutputFactory factory = XMLOutputFactory.newInstance()
    XMLStreamWriter rawWriter = factory.createXMLStreamWriter(bufferedWriter)
    PrettyPrintHandler handler = PrettyPrintHandler.new(rawWriter)
    XMLStreamWriter writer = (XMLStreamWriter)Proxy.newProxyInstance(XMLStreamWriter.getClassLoader(), (Class[])[XMLStreamWriter].toArray(), handler)
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
    return true
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
      writer.writeStartElement("ids")
      for (SlimeRelation relation : relationGroup) {
        writer.writeStartElement("id")
        writer.writeCharacters(relation.getId().toString())
        writer.writeEndElement()
      }
      writer.writeEndElement()
      writer.writeStartElement("names")
      for (SlimeRelation relation : relationGroup) {
        writer.writeStartElement("name")
        writer.writeCharacters(relation.getName())
        writer.writeEndElement()
      }
      writer.writeEndElement()
      writer.writeEndElement()
    }
    writer.writeEndElement()
  }

}


@InnerClass(SlimeTemporaryXmlDictionaryExporter)
@CompileStatic @Ziphilify
private static class PrettyPrintHandler implements InvocationHandler {

  private XMLStreamWriter $target
  private Int $depth = 0
  private Map<IntegerClass, BooleanClass> $childFlags = HashMap.new()

  public PrettyPrintHandler(XMLStreamWriter target) {
    $target = target
  }

  public Object invoke(Object proxy, Method method, Object[] args) {
    String name = method.getName()
    if (name == "writeStartElement") {
      if ($depth > 0) {
        $childFlags[$depth - 1] = true
      }
      $childFlags[$depth] = false
      $target.writeCharacters("\n")
      $target.writeCharacters(repeat($depth, "  "))
      $depth ++
    } else if (name == "writeEndElement") {
      $depth --
      if ($childFlags[$depth]) {
        $target.writeCharacters("\n")
        $target.writeCharacters(repeat($depth, "  "))
      }
    } else if (name == "writeEmptyElement") {
      if ($depth > 0) {
        $childFlags[$depth - 1] = true
      }
      $target.writeCharacters("\n")
      $target.writeCharacters(repeat($depth, "  "))
    }
    method.invoke($target, args)
    return null
  }

  private String repeat(Int depth, String string) {
    String result = ""
    while (depth -- > 0) {
      result += string
    }
    return result
  }

}