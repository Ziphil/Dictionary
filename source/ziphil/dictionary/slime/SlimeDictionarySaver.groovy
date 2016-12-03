package ziphil.dictionary.slime

import com.fasterxml.jackson.core.JsonFactory
import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.TreeNode
import com.fasterxml.jackson.databind.ObjectMapper
import java.util.Map.Entry
import groovy.transform.CompileStatic
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class SlimeDictionarySaver implements Runnable {

  private String $path
  private ObjectMapper $mapper
  private List<SlimeWord> $words
  private SlimeDictionary $dictionary

  public SlimeDictionarySaver(String path, ObjectMapper mapper, List<SlimeWord> words, SlimeDictionary dictionary) {
    $path = path
    $mapper = mapper
    $words = words
    $dictionary = dictionary
  }

  public void run() {
    if ($path != null) {
      FileOutputStream stream = FileOutputStream.new($path)
      JsonFactory factory = $mapper.getFactory()
      JsonGenerator generator = factory.createGenerator(stream)
      generator.useDefaultPrettyPrinter()
      generator.writeStartObject()
      generator.writeFieldName("words")
      generator.writeStartArray()
      for (SlimeWord word : $words) {
        writeWord(generator, word)
      }
      generator.writeEndArray()
      generator.writeFieldName("zpdic")
      generator.writeStartObject()
      generator.writeFieldName("alphabetOrder")
      writeAlphabetOrder(generator)
      generator.writeFieldName("plainInformationTitles")
      writePlainInformationTitles(generator)
      generator.writeEndObject()
      for (Entry<String, TreeNode> entry : $dictionary.getExternalData()) {
        String fieldName = entry.getKey()
        TreeNode node = entry.getValue()
        generator.writeFieldName(fieldName)
        generator.writeTree(node)
      }
      generator.writeEndObject()
      generator.close()
      stream.close()
    }
  }

  private void writeWord(JsonGenerator generator, SlimeWord word) {
    generator.writeStartObject()
    generator.writeFieldName("entry")
    writeEntry(generator, word)
    generator.writeFieldName("translations")
    writeEquivalents(generator, word)
    generator.writeFieldName("tags")
    writeTags(generator, word)
    generator.writeFieldName("contents")
    writeInformations(generator, word)
    generator.writeFieldName("variations")
    writeVariations(generator, word)
    generator.writeFieldName("relations")
    writeRelations(generator, word)
    generator.writeEndObject()
  }

  private void writeEntry(JsonGenerator generator, SlimeWord word) {
    generator.writeStartObject()
    generator.writeNumberField("id", word.getId())
    generator.writeStringField("form", word.getName())
    generator.writeEndObject()
  }

  private void writeEquivalents(JsonGenerator generator, SlimeWord word) {
    generator.writeStartArray()
    for (SlimeEquivalent equivalent : word.getRawEquivalents()) {
      generator.writeStartObject()
      generator.writeStringField("title", equivalent.getTitle())
      generator.writeFieldName("forms")
      generator.writeStartArray()
      for (String name : equivalent.getNames()) {
        generator.writeString(name)
      }
      generator.writeEndArray()
      generator.writeEndObject()
    }
    generator.writeEndArray()
  }

  private void writeTags(JsonGenerator generator, SlimeWord word) {
    generator.writeStartArray()
    for (String tag : word.getTags()) {
      generator.writeString(tag)
    }
    generator.writeEndArray()
  }

  private void writeInformations(JsonGenerator generator, SlimeWord word) {
    generator.writeStartArray()
    for (SlimeInformation information : word.getInformations()) {
      generator.writeStartObject()
      generator.writeStringField("title", information.getTitle())
      generator.writeStringField("text", information.getText())
      generator.writeEndObject()
    }
    generator.writeEndArray()
  }

  private void writeVariations(JsonGenerator generator, SlimeWord word) {
    generator.writeStartArray()
    for (SlimeVariation variation : word.getVariations()) {
      generator.writeStartObject()
      generator.writeStringField("title", variation.getTitle())
      generator.writeStringField("form", variation.getName())
      generator.writeEndObject()
    }
    generator.writeEndArray()
  }

  private void writeRelations(JsonGenerator generator, SlimeWord word) {
    generator.writeStartArray()
    for (SlimeRelation relation : word.getRelations()) {
      generator.writeStartObject()
      generator.writeStringField("title", relation.getTitle())
      generator.writeFieldName("entry")
      generator.writeStartObject()
      generator.writeNumberField("id", relation.getId())
      generator.writeStringField("form", relation.getName())
      generator.writeEndObject()
      generator.writeEndObject()
    }
    generator.writeEndArray()
  }

  private void writeAlphabetOrder(JsonGenerator generator) {
    generator.writeString($dictionary.getAlphabetOrder())
  }

  private void writePlainInformationTitles(JsonGenerator generator) {
    generator.writeStartArray()
    for (String title : $dictionary.getPlainInformationTitles()) {
      generator.writeString(title)
    }
    generator.writeEndArray()
  }

}