package ziphil.dictionary

import com.fasterxml.jackson.core.JsonFactory
import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParseException
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.JsonToken
import com.fasterxml.jackson.databind.JavaType
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import groovy.transform.CompileStatic
import java.util.function.Consumer
import javafx.collections.FXCollections
import javafx.collections.ObservableList
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
  private Map<String, Object> $externalData = HashMap.new()

  public SlimeDictionary(String name, String path) {
    super(name, path)
    load()
    setupRegisteredTitles()
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
    addRegisteredTitles(newWord)
    newWord.createContentPane()
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
    if ($path != null) {
      try {
        FileInputStream stream = FileInputStream.new($path)
        JsonFactory factory = $$mapper.getFactory()
        JsonParser parser = factory.createParser(stream)
        JavaType mapType = $$mapper.getTypeFactory().constructMapType(Map, String, Object)
        while (parser.nextToken() != null) {
          if (parser.getCurrentToken() == JsonToken.FIELD_NAME) {
            String fieldName = parser.getCurrentName()
            parser.nextToken()
            if (fieldName == "words") {
              while (parser.nextToken() == JsonToken.START_OBJECT) {
                SlimeWord word = $$mapper.readValue(parser, SlimeWord)
                word.setDictionary(this)
                $words.add(word)
              }
            } else if (fieldName == "zpdic") {
              Map<String, Object> setting = (Map)$$mapper.readValue(parser, mapType)
              $alphabetOrder = setting["alphabetOrder"]
            } else {
              $externalData.put(fieldName, parser.readValueAs(Object))
            }
          }
        }
        parser.close()
        stream.close()
      } catch (JsonParseException exception) {
      }
    }
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
      $$mapper.writeValue(generator, word.getEntry())
      generator.writeFieldName("translations")
      $$mapper.writeValue(generator, word.getRawEquivalents())
      generator.writeFieldName("tags")
      $$mapper.writeValue(generator, word.getTags())
      generator.writeFieldName("contents")
      $$mapper.writeValue(generator, word.getInformations())
      generator.writeFieldName("variations")
      $$mapper.writeValue(generator, word.getVariations())
      generator.writeFieldName("relations")
      $$mapper.writeValue(generator, word.getRelations())
      generator.writeEndObject()
    }
    generator.writeEndArray()
    generator.writeFieldName("zpdic")
    generator.writeStartObject()
    generator.writeStringField("alphabetOrder", $alphabetOrder)
    generator.writeEndObject()
    $externalData.each() { String fieldName, Object object ->
      generator.writeFieldName(fieldName)
      $$mapper.writeValue(generator, object)
    }
    generator.writeEndObject()
    generator.close()
    stream.close()
  }

  private void setupRegisteredTitles() {
    $words.each() { SlimeWord word ->
      if ($validMinId < word.getId()) {
        $validMinId = word.getId()
      }
      addRegisteredTitles(word)
    }
    $validMinId += 1
  }

  private void setupWords() {
    $sortedWords.setComparator() { SlimeWord firstWord, SlimeWord secondWord ->
      Integer firstId = firstWord.getId()
      Integer secondId = secondWord.getId()
      List<Integer> firstList = firstWord.listForComparison($alphabetOrder)
      List<Integer> secondList = secondWord.listForComparison($alphabetOrder)
      Integer result = null
      (0 ..< firstList.size()).each() { Integer i ->
        Integer firstData = firstList[i]
        Integer secondData = secondList[i]
        if (result == null) {
          if (secondData == null) {
            result = 1
          } else if (firstData != secondData) {
            result = firstData <=> secondData
          }
        }
      }
      if (result == null) {
        if (firstList.size() != secondList.size()) {
          return -1
        } else {
          return firstId <=> secondId
        }
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