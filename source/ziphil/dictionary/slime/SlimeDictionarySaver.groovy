package ziphil.dictionary.slime

import com.fasterxml.jackson.core.JsonFactory
import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.TreeNode
import com.fasterxml.jackson.databind.ObjectMapper
import groovy.transform.CompileStatic
import ziphil.dictionary.DictionarySaver
import ziphil.module.akrantiain.Akrantiain
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class SlimeDictionarySaver extends DictionarySaver<SlimeDictionary> {

  private ObjectMapper $mapper

  public SlimeDictionarySaver(SlimeDictionary dictionary, String path) {
    super(dictionary, path)
  }

  protected BooleanClass save() {
    FileOutputStream stream = FileOutputStream.new($path)
    JsonFactory factory = $mapper.getFactory()
    JsonGenerator generator = factory.createGenerator(stream)
    try {
      generator.useDefaultPrettyPrinter()
      generator.writeStartObject()
      generator.writeFieldName("words")
      generator.writeStartArray()
      for (SlimeWord word : $dictionary.getRawWords()) {
        writeWord(generator, word)
      }
      generator.writeEndArray()
      generator.writeFieldName("zpdic")
      generator.writeStartObject()
      generator.writeFieldName("alphabetOrder")
      writeAlphabetOrder(generator)
      generator.writeFieldName("alphabetOrderType")
      writeAlphabetOrderType(generator)
      generator.writeFieldName("punctuations")
      writePunctuations(generator)
      generator.writeFieldName("pronunciationTitle")
      writePronunciationTitle(generator)
      generator.writeFieldName("plainInformationTitles")
      writePlainInformationTitles(generator)
      generator.writeFieldName("informationTitleOrder")
      writeInformationTitleOrder(generator)
      generator.writeFieldName("defaultWord")
      writeDefaultWord(generator)
      generator.writeEndObject()
      generator.writeFieldName("snoj")
      writeAkrantiainSource(generator)
      for (Map.Entry<String, TreeNode> entry : $dictionary.getExternalData()) {
        String fieldName = entry.getKey()
        TreeNode node = entry.getValue()
        generator.writeFieldName(fieldName)
        generator.writeTree(node)
      }
      generator.writeEndObject()
    } finally {
      generator.close()
      stream.close()
    }
    return true
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

  private void writeAlphabetOrderType(JsonGenerator generator) {
    generator.writeString($dictionary.getAlphabetOrderType().name())
  }

  private void writePunctuations(JsonGenerator generator) {
    generator.writeStartArray()
    for (String punctuation : $dictionary.getPunctuations()) {
      generator.writeString(punctuation)
    }
    generator.writeEndArray()
  }

  private void writePronunciationTitle(JsonGenerator generator) {
    generator.writeString($dictionary.getPronunciationTitle())
  }

  private void writePlainInformationTitles(JsonGenerator generator) {
    generator.writeStartArray()
    for (String title : $dictionary.getPlainInformationTitles()) {
      generator.writeString(title)
    }
    generator.writeEndArray()
  }

  private void writeInformationTitleOrder(JsonGenerator generator) {
    if ($dictionary.getInformationTitleOrder() != null) {
      generator.writeStartArray()
      for (String title : $dictionary.getInformationTitleOrder()) {
        generator.writeString(title)
      }
      generator.writeEndArray()
    } else {
      generator.writeNull()
    }
  }

  private void writeDefaultWord(JsonGenerator generator) {
    writeWord(generator, $dictionary.getDefaultWord())
  }

  private void writeAkrantiainSource(JsonGenerator generator) {
    if ($dictionary.getAkrantiainSource() != null) {
      generator.writeString($dictionary.getAkrantiainSource())
    } else {
      generator.writeNull()
    }
  }

  public void setMapper(ObjectMapper mapper) {
    $mapper = mapper
  }

}