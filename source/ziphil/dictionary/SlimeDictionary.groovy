package ziphil.dictionary

import com.fasterxml.jackson.core.JsonFactory
import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParseException
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.JsonToken
import com.fasterxml.jackson.core.TreeNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import groovy.transform.CompileStatic
import java.util.function.Consumer
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.concurrent.Task
import javafx.concurrent.WorkerStateEvent
import ziphil.custom.SimpleTask
import ziphil.module.Setting
import ziphil.module.Strings


@CompileStatic @Newify
public class SlimeDictionary extends Dictionary<SlimeWord, SlimeSuggestion> {

  private static ObjectMapper $$mapper = createObjectMapper()

  private Integer $validMinId = 0
  private List<String> $registeredTags = ArrayList.new()
  private List<String> $registeredEquivalentTitles = ArrayList.new()
  private List<String> $registeredInformationTitles = ArrayList.new()
  private List<String> $registeredVariationTitles = ArrayList.new()
  private List<String> $registeredRelationTitles = ArrayList.new()
  private String $alphabetOrder = "abcdefghijklmnopqrstuvwxyz"
  private Consumer<Integer> $onLinkClicked
  private Map<String, TreeNode> $externalData = HashMap.new()

  public SlimeDictionary(String name, String path) {
    super(name, path)
    load()
    setupWords()
    setupSuggestions()
  }

  public SlimeDictionary(String name, String path, ObservableList<SlimeWord> words) {
    super(name, path)
    $words.addAll(words)
    setupWords()
    setupSuggestions()
  }

  protected Boolean checkSuggestion(SlimeWord word, String search) {
    Setting setting = Setting.getInstance()
    Boolean ignoresAccent = setting.getIgnoresAccent()
    Boolean ignoresCase = setting.getIgnoresCase()
    Boolean existsSuggestion = false
    word.getVariations().each() { SlimeVariation variation ->
      String variationTitle = variation.getTitle()
      String variationName = variation.getName()
      String newVariationName = variationName
      String newSearch = search
      if (ignoresAccent) {
        newVariationName = Strings.unaccent(newVariationName)
        newSearch = Strings.unaccent(newSearch)
      }
      if (ignoresCase) {
        newVariationName = Strings.toLowerCase(newVariationName)
        newSearch = Strings.toLowerCase(newSearch)
      }
      if (newVariationName == newSearch) {
        SlimePossibility possibility = SlimePossibility.new(word, variationTitle)
        $suggestions[0].getPossibilities().add(possibility)
        $suggestions[0].update()
        existsSuggestion = true
      }
    }
    return existsSuggestion
  }

  public void searchDetail(SlimeSearchParameter parameter) {
    Integer searchId = parameter.getId()
    String searchName = parameter.getName()
    SearchType nameSearchType = parameter.getNameSearchType()
    String searchEquivalentName = parameter.getEquivalentName()
    String searchEquivalentTitle = parameter.getEquivalentTitle()
    SearchType equivalentSearchType = parameter.getEquivalentSearchType()
    String searchInformationText = parameter.getInformationText()
    String searchInformationTitle = parameter.getInformationTitle()
    SearchType informationSearchType = parameter.getInformationSearchType()
    String searchTag = parameter.getTag()
    $filteredWords.setPredicate() { SlimeWord word ->
      Boolean predicate = true
      Integer id = word.getId()
      String name = word.getName()
      List<SlimeEquivalent> equivalents = word.getRawEquivalents()
      List<SlimeInformation> informations = word.getInformations()
      List<String> tags = word.getTags()
      if (searchId != null) {
        if (id != searchId) {
          predicate = false
        }
      }
      if (searchName != null) {
        if (!SearchType.matches(nameSearchType, name, searchName)) {
          predicate = false
        }
      }
      if (searchEquivalentName != null) {
        Boolean equivalentPredicate = false
        equivalents.each() { SlimeEquivalent equivalent ->
          String equivalentTitle = equivalent.getTitle()
          equivalent.getNames().each() { String equivalentName ->
            if (SearchType.matches(equivalentSearchType, equivalentName, searchEquivalentName) && (searchEquivalentTitle == null || equivalentTitle == searchEquivalentTitle)) {
              equivalentPredicate = true
            }
          }
        }
        if (!equivalentPredicate) {
          predicate = false
        }
      }
      if (searchInformationText != null) {
        Boolean informationPredicate = false
        informations.each() { SlimeInformation information ->
          String informationText = information.getText()
          String informationTitle = information.getTitle()
          if (SearchType.matches(informationSearchType, informationText, searchInformationText) && (searchInformationTitle == null || informationTitle == searchInformationTitle)) {
            informationPredicate = true
          }
        }
        if (!informationPredicate) {
          predicate = false
        }
      }
      if (searchTag != null) {
        Boolean tagPredicate = false
        tags.each() { String tag ->
          if (tag == searchTag) {
            tagPredicate = true
          }
        }
        if (!tagPredicate) {
          predicate = false
        }
      }
      return predicate
    }
    $filteredSuggestions.setPredicate() { SlimeSuggestion suggestion ->
      return false
    }
  }

  public void modifyWord(SlimeWord oldWord, SlimeWord newWord) {
    if (containsId(newWord.getId(), newWord)) {
      newWord.setId($validMinId)
      $validMinId += 1
    } else {
      if (newWord.getId() >= $validMinId) {
        $validMinId = newWord.getId() + 1
      }
    }
    if (oldWord.getId() != newWord.getId() || oldWord.getName() != newWord.getName()) {
      $words.each() { SlimeWord otherWord ->
        otherWord.getRelations().each() { SlimeRelation relation ->
          if (relation.getId() == oldWord.getId()) {
            relation.setId(newWord.getId())
            relation.setName(newWord.getName())
          }
        }
      }
    }
    newWord.createComparisonString($alphabetOrder)
    newWord.createContentPane()
    addRegisteredTitles(newWord)
  }

  public void addWord(SlimeWord word) {
    if (containsId(word.getId(), word)) {
      word.setId($validMinId)
      $validMinId += 1
    } else {
      if (word.getId() >= $validMinId) {
        $validMinId = word.getId() + 1
      }
    }
    word.setDictionary(this)
    word.createComparisonString($alphabetOrder)
    addRegisteredTitles(word)
    $words.add(word)
  }

  public void removeWord(SlimeWord word) {
    $words.each() { SlimeWord otherWord ->
      otherWord.getRelations().removeAll{relation -> relation.getId() == word.getId()}
    }
    $words.remove(word)
  }

  private void addRegisteredTitles(SlimeWord word) {
    word.getTags().each() { String tag ->
      if (!$registeredTags.contains(tag)) {
        $registeredTags.addAll(tag)
      }
    }
    word.getRawEquivalents().each() { SlimeEquivalent equivalent ->
      String title = equivalent.getTitle()
      if (!$registeredEquivalentTitles.contains(title)) {
        $registeredEquivalentTitles.add(title)
      }
    }
    word.getInformations().each() { SlimeInformation information ->
      String title = information.getTitle()
      if (!$registeredInformationTitles.contains(title)) {
        $registeredInformationTitles.add(title)
      }
    }
    word.getVariations().each() { SlimeVariation variation ->
      String title = variation.getTitle()
      if (!$registeredVariationTitles.contains(title)) {
        $registeredVariationTitles.add(title)
      }
    }
    word.getRelations().each() { SlimeRelation relation ->
      String title = relation.getTitle()
      if (!$registeredRelationTitles.contains(title)) {
        $registeredRelationTitles.add(title)
      }
    }
  }

  public SlimeWord emptyWord() {
    SlimeWord word = SlimeWord.new()
    word.setId($validMinId)
    return word
  }

  public SlimeWord copiedWord(SlimeWord oldWord) {
    Integer id = oldWord.getId()
    String name = oldWord.getName()
    List<SlimeEquivalent> rawEquivalents = oldWord.getRawEquivalents()
    List<String> tags = oldWord.getTags()
    List<SlimeInformation> informations = oldWord.getInformations()
    List<SlimeVariation> variations = oldWord.getVariations()
    List<SlimeRelation> relations = oldWord.getRelations()
    SlimeWord newWord = SlimeWord.new(id, name, rawEquivalents, tags, informations, variations, relations)
    return newWord
  }

  public SlimeWord inheritedWord(SlimeWord oldWord) {
    Integer id = $validMinId
    String name = oldWord.getName()
    List<SlimeEquivalent> rawEquivalents = oldWord.getRawEquivalents()
    List<String> tags = oldWord.getTags()
    List<SlimeInformation> informations = oldWord.getInformations()
    List<SlimeVariation> variations = oldWord.getVariations()
    List<SlimeRelation> relations = oldWord.getRelations()
    SlimeWord newWord = SlimeWord.new(id, name, rawEquivalents, tags, informations, variations, relations)
    return newWord
  }

  public Boolean containsId(Integer id, SlimeWord excludedWord) {
    return $words.any{word -> word != excludedWord && word.getId() == id}
  }

  public SlimeDictionary copy() {
    ObservableList<SlimeWord> copiedWords = FXCollections.observableArrayList($words)
    SlimeDictionary dictionary = SlimeDictionary.new($name, $path, copiedWords)
    return dictionary
  }

  private void load() {
    SlimeDictionary dictionary = this
    Task<ObservableList<SlimeWord>> task = SimpleTask.new() {
      ObservableList<SlimeWord> currentWords = FXCollections.observableArrayList()
      if ($path != null) {
        try {
          FileInputStream stream = FileInputStream.new($path)
          JsonFactory factory = $$mapper.getFactory()
          JsonParser parser = factory.createParser(stream)
          parser.nextToken()
          while (parser.nextToken() == JsonToken.FIELD_NAME) {
            String topFieldName = parser.getCurrentName()
            parser.nextToken()
            if (topFieldName == "words") {
              while (parser.nextToken() == JsonToken.START_OBJECT) {
                SlimeWord word = SlimeWord.new()
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
                word.setDictionary(dictionary)
                currentWords.add(word)
              }
            } else if (topFieldName == "zpdic") {
              while (parser.nextToken() == JsonToken.FIELD_NAME) {
                String specialFieldName = parser.getCurrentName()
                parser.nextToken()
                if (specialFieldName == "alphabetOrder") {
                  $alphabetOrder = parser.getValueAsString()
                }
              }
            } else {
              $externalData.put(topFieldName, parser.readValueAsTree())
            }
          }
          $validMinId ++
          parser.close()
          stream.close()
        } catch (JsonParseException exception) {
          exception.printStackTrace()
        }
      }
      currentWords.each() { SlimeWord word ->
        word.createComparisonString($alphabetOrder)
      }
      return currentWords
    }
    task.setOnSucceeded() { WorkerStateEvent event ->
      $words.addAll(task.getValue())
    }
    Thread thread = Thread.new(task)
    thread.setDaemon(true)
    thread.start()
  }

  public void save() {
    FileOutputStream stream = FileOutputStream.new($path)
    JsonFactory factory = $$mapper.getFactory()
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
    generator.writeStringField("alphabetOrder", $alphabetOrder)
    generator.writeEndObject()
    $externalData.each() { String fieldName, TreeNode node ->
      generator.writeFieldName(fieldName)
      generator.writeTree(node)
    }
    generator.writeEndObject()
    generator.close()
    stream.close()
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

  private void setupWords() {
    $sortedWords.setComparator() { SlimeWord firstWord, SlimeWord secondWord ->
      Integer firstId = firstWord.getId()
      Integer secondId = secondWord.getId()
      String firstString = firstWord.getComparisonString()
      String secondString = secondWord.getComparisonString()
      Integer result = firstString <=> secondString
      if (result == 0) {
        return firstId <=> secondId
      } else {
        return result
      }
    }
  }

  private void setupSuggestions() {
    SlimeSuggestion suggestion = SlimeSuggestion.new()
    suggestion.setDictionary(this)
    $suggestions.add(suggestion)
  }

  private static ObjectMapper createObjectMapper() {
    ObjectMapper mapper = ObjectMapper.new()
    mapper.enable(SerializationFeature.INDENT_OUTPUT)
    return mapper
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

  public void setAlphabetOrder(String alphabetOrder) {
    $alphabetOrder = alphabetOrder
  }

  public Consumer<Integer> getOnLinkClicked() {
    return $onLinkClicked
  }

  public void setOnLinkClicked(Consumer<Integer> onLinkClicked) {
    $onLinkClicked = onLinkClicked
  }

}