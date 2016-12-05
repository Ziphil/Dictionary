package ziphil.dictionary.slime

import com.fasterxml.jackson.core.TreeNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import groovy.transform.CompileStatic
import java.util.function.Consumer
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.concurrent.Task
import javafx.concurrent.WorkerStateEvent
import ziphil.dictionary.Dictionary
import ziphil.dictionary.SearchType
import ziphil.module.Setting
import ziphil.module.Strings
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class SlimeDictionary extends Dictionary<SlimeWord, SlimeSuggestion> {

  private static ObjectMapper $$mapper = createObjectMapper()

  private SlimeDictionaryLoader $loader
  private Integer $validMinId
  private List<String> $registeredTags
  private List<String> $registeredEquivalentTitles
  private List<String> $registeredInformationTitles
  private List<String> $registeredVariationTitles
  private List<String> $registeredRelationTitles
  private String $alphabetOrder
  private List<String> $plainInformationTitles
  private List<String> $informationTitleOrder
  private SlimeWord $defaultWord
  private Consumer<Integer> $onLinkClicked
  private Map<String, TreeNode> $externalData

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

  protected Boolean checkSuggestion(SlimeWord word, String search, String convertedSearch) {
    Setting setting = Setting.getInstance()
    Boolean ignoresAccent = setting.getIgnoresAccent()
    Boolean ignoresCase = setting.getIgnoresCase()
    Boolean existsSuggestion = false
    for (SlimeVariation variation : word.getVariations()) {
      String variationTitle = variation.getTitle()
      String variationName = variation.getName()
      String convertedVariationName = Strings.convert(variationName, ignoresAccent, ignoresCase)
      if (convertedVariationName == convertedSearch) {
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
      if (searchEquivalentName != null || searchEquivalentTitle != null) {
        Boolean equivalentPredicate = false
        searchEquivalentName = searchEquivalentName ?: ""
        for (SlimeEquivalent equivalent : equivalents) {
          String equivalentTitle = equivalent.getTitle()
          for (String equivalentName : equivalent.getNames()) {
            if (SearchType.matches(equivalentSearchType, equivalentName, searchEquivalentName) && (searchEquivalentTitle == null || equivalentTitle == searchEquivalentTitle)) {
              equivalentPredicate = true
            }
          }
        }
        if (!equivalentPredicate) {
          predicate = false
        }
      }
      if (searchInformationText != null || searchInformationTitle != null) {
        Boolean informationPredicate = false
        searchInformationText = searchInformationText ?: ""
        for (SlimeInformation information : informations) {
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
        for (String tag : tags) {
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
    $shufflableWords.unshuffle()
  }

  public void modifyWord(SlimeWord oldWord, SlimeWord newWord) {
    if (containsId(newWord.getId(), newWord)) {
      newWord.setId($validMinId)
    }
    if (oldWord.getId() != newWord.getId() || oldWord.getName() != newWord.getName()) {
      for (SlimeWord otherWord : $words) {
        for (SlimeRelation relation : otherWord.getRelations()) {
          if (relation.getId() == oldWord.getId()) {
            relation.setId(newWord.getId())
            relation.setName(newWord.getName())
            otherWord.change()
          }
        }
      }
    }
    newWord.createComparisonString($alphabetOrder)
    newWord.createContentPane()
    addRegisteredTitles(newWord)
    $isChanged = true
  }

  public void addWord(SlimeWord word) {
    if (containsId(word.getId(), word)) {
      word.setId($validMinId)
    }
    word.setDictionary(this)
    word.createComparisonString($alphabetOrder)
    addRegisteredTitles(word)
    $words.add(word)
    $isChanged = true
  }

  public void removeWord(SlimeWord word) {
    for (SlimeWord otherWord : $words) {
      Boolean isChanged = otherWord.getRelations().removeAll{relation -> relation.getId() == word.getId()}
      if (isChanged) {
        otherWord.change()
      }
    }
    $words.remove(word)
    $isChanged = true
  }

  private void addRegisteredTitles(SlimeWord word) {
    if (word.getId() >= $validMinId) {
      $validMinId = word.getId() + 1
    }
    for (String tag : word.getTags()) {
      if (!$registeredTags.contains(tag)) {
        $registeredTags.addAll(tag)
      }
    }
    for (SlimeEquivalent equivalent : word.getRawEquivalents()) {
      String title = equivalent.getTitle()
      if (!$registeredEquivalentTitles.contains(title)) {
        $registeredEquivalentTitles.add(title)
      }
    }
    for (SlimeInformation information : word.getInformations()) {
      String title = information.getTitle()
      if (!$registeredInformationTitles.contains(title)) {
        $registeredInformationTitles.add(title)
      }
    }
    for (SlimeVariation variation : word.getVariations()) {
      String title = variation.getTitle()
      if (!$registeredVariationTitles.contains(title)) {
        $registeredVariationTitles.add(title)
      }
    }
    for (SlimeRelation relation : word.getRelations()) {
      String title = relation.getTitle()
      if (!$registeredRelationTitles.contains(title)) {
        $registeredRelationTitles.add(title)
      }
    }
  }

  public SlimeWord emptyWord(String defaultName) {
    SlimeWord word = copiedWord($defaultWord)
    word.setId($validMinId)
    if (defaultName != null) {
      word.setName(defaultName)
    }
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

  public SlimeDictionary copy() {
    ObservableList<SlimeWord> copiedWords = FXCollections.observableArrayList($words)
    SlimeDictionary dictionary = SlimeDictionary.new($name, $path, copiedWords)
    return dictionary
  }

  public Boolean containsId(Integer id, SlimeWord excludedWord) {
    return $words.any{word -> word != excludedWord && word.getId() == id}
  }

  private void load() {
    $loader = SlimeDictionaryLoader.new($path, $$mapper, this)
    $loader.addEventFilter(WorkerStateEvent.WORKER_STATE_SUCCEEDED) { WorkerStateEvent event ->
      $validMinId = $loader.getValidMinId()
      $registeredTags = $loader.getRegisteredTags()
      $registeredEquivalentTitles = $loader.getRegisteredEquivalentTitles()
      $registeredInformationTitles = $loader.getRegisteredInformationTitles()
      $registeredVariationTitles = $loader.getRegisteredVariationTitles()
      $registeredRelationTitles = $loader.getRegisteredRelationTitles()
      $alphabetOrder = $loader.getAlphabetOrder()
      $plainInformationTitles = $loader.getPlainInformationTitles()
      $informationTitleOrder = $loader.getInformationTitleOrder()
      $defaultWord = $loader.getDefaultWord()
      $externalData = $loader.getExternalData()
      $words.addAll($loader.getValue())
    }
    Thread thread = Thread.new(loader)
    thread.setDaemon(true)
    thread.start()
  }

  public void save() {
    Runnable saver = SlimeDictionarySaver.new($path, $$mapper, $words, this)
    saver.run()
    $isChanged = false
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
    $isChanged = true
  }

  public List<String> getPlainInformationTitles() {
    return $plainInformationTitles
  }

  public void setPlainInformationTitles(List<String> plainInformationTitles) {
    $plainInformationTitles = plainInformationTitles
    $isChanged = true
  }

  public List<String> getInformationTitleOrder() {
    return $informationTitleOrder
  }

  public void setInformationTitleOrder(List<String> informationTitleOrder) {
    $informationTitleOrder = informationTitleOrder
    $isChanged = true
  }

  public SlimeWord getDefaultWord() {
    return $defaultWord
  }

  public void setDefaultWord(SlimeWord defaultWord) {
    $defaultWord = defaultWord
    $isChanged = true
  }

  public Map<String, TreeNode> getExternalData() {
    return $externalData
  }

  public Consumer<Integer> getOnLinkClicked() {
    return $onLinkClicked
  }

  public void setOnLinkClicked(Consumer<Integer> onLinkClicked) {
    $onLinkClicked = onLinkClicked
  }

  public Task<?> getLoader() {
    return $loader
  }

}