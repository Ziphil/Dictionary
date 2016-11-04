package ziphil.dictionary.slime

import com.fasterxml.jackson.core.JsonFactory
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.JsonToken
import com.fasterxml.jackson.core.TreeNode
import com.fasterxml.jackson.databind.ObjectMapper
import groovy.transform.CompileStatic
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.concurrent.Task


@CompileStatic @Newify
public class SlimeDictionaryLoader extends Task<ObservableList<SlimeWord>> {

  private ObservableList<SlimeWord> $words = FXCollections.observableArrayList()
  private String $path
  private ObjectMapper $mapper
  private SlimeDictionary $dictionary
  private Integer $validMinId = -1
  private List<String> $registeredTags = ArrayList.new()
  private List<String> $registeredEquivalentTitles = ArrayList.new()
  private List<String> $registeredInformationTitles = ArrayList.new()
  private List<String> $registeredVariationTitles = ArrayList.new()
  private List<String> $registeredRelationTitles = ArrayList.new()
  private String $alphabetOrder = "abcdefghijklmnopqrstuvwxyz"
  private Map<String, TreeNode> $externalData = HashMap.new()

  public SlimeDictionaryLoader(String path, ObjectMapper mapper, SlimeDictionary dictionary) {
    $path = path
    $mapper = mapper
    $dictionary = dictionary
    updateProgress(null, null)
  }

  protected ObservableList<SlimeWord> call() {
    if ($path != null) {
      FileInputStream stream = FileInputStream.new($path)
      JsonFactory factory = $mapper.getFactory()
      JsonParser parser = factory.createParser(stream)
      Integer size = stream.available()
      parser.nextToken()
      while (parser.nextToken() == JsonToken.FIELD_NAME) {
        String topFieldName = parser.getCurrentName()
        parser.nextToken()
        if (isCancelled()) {
          return null
        }
        if (topFieldName == "words") {
          while (parser.nextToken() == JsonToken.START_OBJECT) {
            SlimeWord word = SlimeWord.new()
            while (parser.nextToken() == JsonToken.FIELD_NAME) {
              String wordFieldName = parser.getCurrentName()
              parser.nextToken()
              if (isCancelled()) {
                return null
              }
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
            $words.add(word)
            updateProgress(parser, size)
          }
        } else if (topFieldName == "zpdic") {
          while (parser.nextToken() == JsonToken.FIELD_NAME) {
            String specialFieldName = parser.getCurrentName()
            parser.nextToken()
            if (specialFieldName == "alphabetOrder") {
              $alphabetOrder = parser.getValueAsString()
            }
            updateProgress(parser, size)
          }
        } else {
          $externalData.put(topFieldName, parser.readValueAsTree())
        }
      }
      $validMinId ++
      parser.close()
      stream.close()
    }
    $words.each() { SlimeWord word ->
      word.createComparisonString($alphabetOrder)
    }
    return $words
  }

  private void parseEntry(JsonParser parser, SlimeWord word) {
    while (parser.nextToken() == JsonToken.FIELD_NAME) {
      String entryFieldName = parser.getCurrentName()
      parser.nextToken()
      if (entryFieldName == "id") {
        Integer id = parser.getValueAsInt()
        word.setId(id)
        if ($validMinId < id) {
          $validMinId = id
        }
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
          if (!$registeredEquivalentTitles.contains(title)) {
            $registeredEquivalentTitles.add(title)
          } 
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
      if (!$registeredTags.contains(tag)) {
        $registeredTags.addAll(tag)
      }
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
          if (!$registeredInformationTitles.contains(title)) {
            $registeredInformationTitles.add(title)
          }
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
          if (!$registeredVariationTitles.contains(title)) {
            $registeredVariationTitles.add(title)
          }
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
          if (!$registeredRelationTitles.contains(title)) {
            $registeredRelationTitles.add(title)
          }
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

  private void updateProgress(JsonParser parser, Integer size) {
    if (parser != null) {
      updateProgress(parser.getCurrentLocation().getByteOffset(), size)
    } else {
      updateProgress(0, 1)
    }
  }

  public Integer getValidMinId() {
    return $validMinId
  }

  public List<String> getRegisteredTags() {
    return $registeredTags
  }

  public List<String> getRegisteredEquivalentTitles() {
    return $registeredEquivalentTitles
  }

  public List<String> getRegisteredInformationTitles() {
    return $registeredInformationTitles
  }

  public List<String> getRegisteredVariationTitles() {
    return $registeredVariationTitles
  }

  public List<String> getRegisteredRelationTitles() {
    return $registeredRelationTitles
  }

  public String getAlphabetOrder() {
    return $alphabetOrder
  }

  public Map<String, TreeNode> getExternalData() {
    return $externalData
  }

}