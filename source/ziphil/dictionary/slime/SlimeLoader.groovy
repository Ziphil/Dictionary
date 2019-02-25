package ziphil.dictionary.slime

import com.fasterxml.jackson.core.JsonFactory
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.JsonToken
import com.fasterxml.jackson.core.TreeNode
import com.fasterxml.jackson.databind.ObjectMapper
import groovy.transform.CompileStatic
import javafx.collections.ObservableList
import ziphil.dictionary.Loader
import ziphil.dictionary.WordOrderType
import ziphilib.transform.InnerClass
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class SlimeLoader extends Loader<SlimeDictionary, SlimeWord> {

  private String $path
  private ObjectMapper $mapper

  public SlimeLoader(String path) {
    super()
    $path = path
  }

  protected BooleanClass load() {
    File file = File.new($path)
    FileInputStream stream = FileInputStream.new($path)
    JsonFactory factory = $mapper.getFactory()
    JsonParser parser = factory.createParser(stream)
    Map<IntegerClass, SlimeWord> correspondingWords = HashMap.new()
    Long size = file.length()
    try {
      parser.nextToken()
      while (parser.nextToken() == JsonToken.FIELD_NAME) {
        String topFieldName = parser.getCurrentName()
        parser.nextToken()
        if (topFieldName == "words") {
          while (parser.nextToken() == JsonToken.START_OBJECT) {
            if (isCancelled()) {
              return false
            }
            SlimeWord word = SlimeWord.new()
            parseWord(parser, word)
            $words.add(word)
            correspondingWords.put(word.getNumber(), word)
            updateProgressByParser(parser, size)
          }
        } else if (topFieldName == "zpdic") {
          while (parser.nextToken() == JsonToken.FIELD_NAME) {
            String specialFieldName = parser.getCurrentName()
            parser.nextToken()
            if (specialFieldName == "alphabetOrder") {
              parseAlphabetOrder(parser)
            } else if (specialFieldName == "wordOrderType" || specialFieldName == "alphabetOrderType") {
              parseWordOrderType(parser)
            } else if (specialFieldName == "punctuations") {
              parsePunctuations(parser)
            } else if (specialFieldName == "ignoredTranslationRegex") {
              parseIgnoredEquivalentRegex(parser)
            } else if (specialFieldName == "pronunciationTitle") {
              parsePronunciationTitle(parser)
            } else if (specialFieldName == "plainInformationTitles") {
              parsePlainInformationTitles(parser)
            } else if (specialFieldName == "informationTitleOrder") {
              parseInformationTitleOrder(parser)
            } else if (specialFieldName == "formFontFamily") {
              parseNameFontFamily(parser)
            } else if (specialFieldName == "defaultWord") {
              parseDefaultWord(parser)
            }
            updateProgressByParser(parser, size)
          }
        } else if (topFieldName == "snoj") {
          parseAkrantiainSource(parser)
        } else {
          $dictionary.getExternalData().put(topFieldName, parser.readValueAsTree())
        }
      }
    } finally {
      parser.close()
      stream.close()
    }
    for (Int i = 0 ; i < $words.size() ; i ++) {
      resolveRelations($words[i], correspondingWords)
      updateProgressByWord(i + 1, $words.size())
    }
    return true
  }

  private void parseWord(JsonParser parser, SlimeWord word) {
    while (parser.nextToken() == JsonToken.FIELD_NAME) {
      String wordFieldName = parser.getCurrentName()
      parser.nextToken()
      if (wordFieldName == "entry") {
        parseEntry(parser, word)
      } else if (wordFieldName == "translations") {
        parseEquivalents(parser, word)
      } else if (wordFieldName == "tags") {
        parseTags(parser, word)
      } else if (wordFieldName == "contents") {
        parseInformations(parser, word)
      } else if (wordFieldName == "variations") {
        parseVariations(parser, word)
      } else if (wordFieldName == "relations") {
        parseRelations(parser, word)
      }
    }
    word.setDictionary($dictionary)
  }

  private void parseEntry(JsonParser parser, SlimeWord word) {
    while (parser.nextToken() == JsonToken.FIELD_NAME) {
      String entryFieldName = parser.getCurrentName()
      parser.nextToken()
      if (entryFieldName == "id") {
        Int number = parser.getValueAsInt()
        word.setNumber(number)
      } else if (entryFieldName == "form") {
        String name = parser.getValueAsString()
        word.setName(name)
      }
    }
  }

  private void parseEquivalents(JsonParser parser, SlimeWord word) {
    while (parser.nextToken() == JsonToken.START_OBJECT) {
      SlimeEquivalent equivalent = SlimeEquivalent.new()
      while (parser.nextToken() == JsonToken.FIELD_NAME) {
        String equivalentFieldName = parser.getCurrentName()
        parser.nextToken()
        if (equivalentFieldName == "title") {
          String title = parser.getValueAsString()
          equivalent.setTitle(title)
        } else if (equivalentFieldName == "forms") {
          while (parser.nextToken() != JsonToken.END_ARRAY) {
            String name = parser.getValueAsString()
            equivalent.getNames().add(name)
            word.getEquivalents().add(name)
          }
        }
      }
      word.getRawEquivalents().add(equivalent)
    }
  }

  private void parseTags(JsonParser parser, SlimeWord word) {
    while (parser.nextToken() != JsonToken.END_ARRAY) {
      String tag = parser.getValueAsString()
      word.getTags().add(tag)
    }
  }

  private void parseInformations(JsonParser parser, SlimeWord word) {
    while (parser.nextToken() == JsonToken.START_OBJECT) {
      SlimeInformation information = SlimeInformation.new()
      while (parser.nextToken() == JsonToken.FIELD_NAME) {
        String informationFieldName = parser.getCurrentName()
        parser.nextToken()
        if (informationFieldName == "title") {
          String title = parser.getValueAsString()
          information.setTitle(title)
        } else if (informationFieldName == "text") {
          String text = parser.getValueAsString()
          information.setText(text)
        }
      }
      word.getInformations().add(information)
    }
  }

  private void parseVariations(JsonParser parser, SlimeWord word) {
    while (parser.nextToken() == JsonToken.START_OBJECT) {
      SlimeVariation variation = SlimeVariation.new()
      while (parser.nextToken() == JsonToken.FIELD_NAME) {
        String variationFieldName = parser.getCurrentName()
        parser.nextToken()
        if (variationFieldName == "title") {
          String title = parser.getValueAsString()
          variation.setTitle(title)
        } else if (variationFieldName == "form") {
          String name = parser.getValueAsString()
          variation.setName(name)
        }
      }
      word.getVariations().add(variation)
    }
  }

  private void parseRelations(JsonParser parser, SlimeWord word) {
    while (parser.nextToken() == JsonToken.START_OBJECT) {
      TemporaryRelation relation = TemporaryRelation.new()
      while (parser.nextToken() == JsonToken.FIELD_NAME) {
        String relationFieldName = parser.getCurrentName()
        parser.nextToken()
        if (relationFieldName == "title") {
          String title = parser.getValueAsString()
          relation.setTitle(title)
        } else if (relationFieldName == "entry") {
          while (parser.nextToken() == JsonToken.FIELD_NAME) {
            String relationEntryFieldName = parser.getCurrentName()
            parser.nextToken()
            if (relationEntryFieldName == "id") {
              Int number = parser.getValueAsInt()
              relation.setNumber(number)
            } else if (relationEntryFieldName == "form") {
              String name = parser.getValueAsString()
              relation.setName(name)
            }
          }
        }
      }
      word.getRelations().add(relation)
    }
  }

  private void parseAlphabetOrder(JsonParser parser) {
    String alphabetOrder = parser.getValueAsString()
    $dictionary.setAlphabetOrder(alphabetOrder) 
  }

  private void parseWordOrderType(JsonParser parser) {
    String wordOrderType = parser.getValueAsString()
    if (wordOrderType == "ID") {
      $dictionary.setWordOrderType(WordOrderType.IDENTIFIER)
    } else {
      $dictionary.setWordOrderType(WordOrderType.valueOf(wordOrderType))
    }
  }

  private void parsePunctuations(JsonParser parser) {
    $dictionary.setPunctuations(ArrayList.new())
    while (parser.nextToken() != JsonToken.END_ARRAY) {
      String punctuation = parser.getValueAsString()
      $dictionary.getPunctuations().add(punctuation)
    }
  }

  private void parseIgnoredEquivalentRegex(JsonParser parser) {
    String ignoredEquivalentRegex = parser.getValueAsString()
    $dictionary.setIgnoredEquivalentRegex(ignoredEquivalentRegex)
  }

  private void parsePronunciationTitle(JsonParser parser) {
    String pronunciationTitle = parser.getValueAsString()
    $dictionary.setPronunciationTitle(pronunciationTitle) 
  }

  private void parsePlainInformationTitles(JsonParser parser) {
    $dictionary.setPlainInformationTitles(ArrayList.new())
    while (parser.nextToken() != JsonToken.END_ARRAY) {
      String title = parser.getValueAsString()
      $dictionary.getPlainInformationTitles().add(title)
    }
  }

  private void parseInformationTitleOrder(JsonParser parser) {
    if (parser.getCurrentToken() != JsonToken.VALUE_NULL) {
      $dictionary.setInformationTitleOrder(ArrayList.new())
      while (parser.nextToken() != JsonToken.END_ARRAY) {
        String title = parser.getValueAsString()
        $dictionary.getInformationTitleOrder().add(title)
      }
    }
  }

  private void parseNameFontFamily(JsonParser parser) {
    if (parser.getCurrentToken() != JsonToken.VALUE_NULL) {
      String nameFontFamily = parser.getValueAsString()
      $dictionary.setNameFontFamily(nameFontFamily)
    }
  }

  private void parseDefaultWord(JsonParser parser) {
    SlimeWord defaultWord = SlimeWord.new()
    parseWord(parser, defaultWord)
    $dictionary.setDefaultWord(defaultWord)
  }

  private void parseAkrantiainSource(JsonParser parser) {
    if (parser.getCurrentToken() != JsonToken.VALUE_NULL) {
      String akrantiainSource = parser.getValueAsString()
      $dictionary.setAkrantiainSource(akrantiainSource)
    }
  }

  private void resolveRelations(SlimeWord word, Map<IntegerClass, SlimeWord> correspondingWords) {
    List<SlimeRelation> relations = word.getRelations()
    for (Int i = 0 ; i < relations.size() ; i ++) {
      TemporaryRelation relation = (TemporaryRelation)relations[i]
      if (correspondingWords.containsKey(relation.getNumber())) {
        SlimeWord relationWord = correspondingWords[relation.getNumber()]
        if (relationWord.getName() == relation.getName()) {
          SlimeRelation genuineRelation = SlimeRelation.new()
          genuineRelation.setTitle(relation.getTitle())
          genuineRelation.setWord(relationWord)
          relations[i] = genuineRelation
        }
      }
    }
    relations.removeAll{it instanceof TemporaryRelation}
  }

  private void updateProgressByParser(JsonParser parser, Long size) {
    if (parser != null) {
      Double progress = parser.getCurrentLocation().getByteOffset() / size
      updateProgress(progress * 0.75D, 1)
    } else {
      updateProgress(0, 1)
    }
  }

  private void updateProgressByWord(Int index, Long size) {
    if (index > 0) {
      Double progress = index / size
      updateProgress(progress * 0.25D + 0.75D, 1)
    } else {
      updateProgress(0.75D, 1)
    }
  }

  public void setMapper(ObjectMapper mapper) {
    $mapper = mapper
  }

}


@InnerClass(SlimeLoader)
@CompileStatic @Ziphilify
public static class TemporaryRelation extends SlimeRelation {

  private Int $number = -1
  private String $name = ""

  public TemporaryRelation() {
    super()
  }

  public Int getNumber() {
    return $number
  }

  public void setNumber(Int number) {
    $number = number
  }

  public String getName() {
    return $name
  }

  public void setName(String name) {
    $name = name
  }

}