package ziphil.dictionary.slime

import com.fasterxml.jackson.core.JsonFactory
import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.TreeNode
import com.fasterxml.jackson.databind.ObjectMapper
import groovy.transform.CompileStatic


@CompileStatic @Newify
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
      $words.each() { SlimeWord word ->
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
      generator.writeEndArray()
      generator.writeFieldName("zpdic")
      generator.writeStartObject()
      generator.writeStringField("alphabetOrder", $dictionary.getAlphabetOrder())
      generator.writeEndObject()
      $dictionary.getExternalData().each() { String fieldName, TreeNode node ->
        generator.writeFieldName(fieldName)
        generator.writeTree(node)
      }
      generator.writeEndObject()
      generator.close()
      stream.close()
    }
  }

  private void writeEntry(JsonGenerator generator, SlimeWord word) {
    generator.writeStartObject()
    generator.writeNumberField("id", word.getId())
    generator.writeStringField("form", word.getName())
    generator.writeEndObject()
  }

  private void writeEquivalents(JsonGenerator generator, SlimeWord word) {
    generator.writeStartArray()
    word.getRawEquivalents().each() { SlimeEquivalent equivalent ->
      generator.writeStartObject()
      generator.writeStringField("title", equivalent.getTitle())
      generator.writeFieldName("forms")
      generator.writeStartArray()
      equivalent.getNames().each() { String name ->
        generator.writeString(name)
      }
      generator.writeEndArray()
      generator.writeEndObject()
    }
    generator.writeEndArray()
  }

  private void writeTags(JsonGenerator generator, SlimeWord word) {
    generator.writeStartArray()
    word.getTags().each() { String tag ->
      generator.writeString(tag)
    }
    generator.writeEndArray()
  }

  private void writeInformations(JsonGenerator generator, SlimeWord word) {
    generator.writeStartArray()
    word.getInformations().each() { SlimeInformation information ->
      generator.writeStartObject()
      generator.writeStringField("title", information.getTitle())
      generator.writeStringField("text", information.getText())
      generator.writeEndObject()
    }
    generator.writeEndArray()
  }

  private void writeVariations(JsonGenerator generator, SlimeWord word) {
    generator.writeStartArray()
    word.getVariations().each() { SlimeVariation variation ->
      generator.writeStartObject()
      generator.writeStringField("title", variation.getTitle())
      generator.writeStringField("form", variation.getName())
      generator.writeEndObject()
    }
    generator.writeEndArray()
  }

  private void writeRelations(JsonGenerator generator, SlimeWord word) {
    generator.writeStartArray()
    word.getRelations().each() { SlimeRelation relation ->
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

}