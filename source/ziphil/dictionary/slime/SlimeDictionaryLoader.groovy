package ziphil.dictionary.slime

import com.fasterxml.jackson.core.JsonFactory
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.JsonToken
import com.fasterxml.jackson.core.TreeNode
import com.fasterxml.jackson.databind.ObjectMapper
import groovy.transform.CompileStatic
import javafx.collections.ObservableList
import ziphil.dictionary.DictionaryLoader
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class SlimeDictionaryLoader extends DictionaryLoader<SlimeDictionary, SlimeWord> {

  private ObjectMapper $mapper

  public SlimeDictionaryLoader(SlimeDictionary dictionary, String path) {
    super(dictionary, path)
    updateProgress(0, 1)
  }

  protected ObservableList<SlimeWord> call() {
    if ($path != null) {
      FileInputStream stream = FileInputStream.new($path)
      JsonFactory factory = $mapper.getFactory()
      JsonParser parser = factory.createParser(stream)
      Integer size = stream.available()
      try {
        parser.nextToken()
        while (parser.nextToken() == JsonToken.FIELD_NAME) {
          String topFieldName = parser.getCurrentName()
          parser.nextToken()
          if (topFieldName == "words") {
            while (parser.nextToken() == JsonToken.START_OBJECT) {
              SlimeWord word = SlimeWord.new()
              if (isCancelled()) {
                return null
              }
              parseWord(parser, word)
              word.setDictionary($dictionary)
              $words.add(word)
              updateProgressByParser(parser, size)
            }
          } else if (topFieldName == "zpdic") {
            while (parser.nextToken() == JsonToken.FIELD_NAME) {
              String specialFieldName = parser.getCurrentName()
              parser.nextToken()
              if (specialFieldName == "alphabetOrder") {
                parseAlphabetOrder(parser)
              } else if (specialFieldName == "plainInformationTitles") {
                parsePlainInformationTitles(parser)
              } else if (specialFieldName == "informationTitleOrder") {
                parseInformationTitleOrder(parser)
              } else if (specialFieldName == "defaultWord") {
                parseDefaultWord(parser)
              }
              updateProgressByParser(parser, size)
            }
          } else {
            $dictionary.getExternalData().put(topFieldName, parser.readValueAsTree())
          }
        }
      } finally {
        parser.close()
        stream.close()
      }
    }
    for (SlimeWord word : $words) {
      word.updateComparisonString($dictionary.getAlphabetOrder())
      word.update()
    }
    return $words
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
  }

  private void parseEntry(JsonParser parser, SlimeWord word) {
    while (parser.nextToken() == JsonToken.FIELD_NAME) {
      String entryFieldName = parser.getCurrentName()
      parser.nextToken()
      if (entryFieldName == "id") {
        Integer id = parser.getValueAsInt()
        word.setId(id)
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
      SlimeRelation relation = SlimeRelation.new()
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
              Integer id = parser.getValueAsInt()
              relation.setId(id)
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

  private void parsePlainInformationTitles(JsonParser parser) {
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

  private void parseDefaultWord(JsonParser parser) {
    parseWord(parser, $dictionary.getDefaultWord())
  }

  private void updateProgressByParser(JsonParser parser, Integer size) {
    if (parser != null) {
      updateProgress(parser.getCurrentLocation().getByteOffset(), size)
    } else {
      updateProgress(0, 1)
    }
  }

  public void setMapper(ObjectMapper mapper) {
    $mapper = mapper
  }

}