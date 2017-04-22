package ziphil.dictionary.slime

import com.fasterxml.jackson.core.TreeNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import groovy.transform.CompileStatic
import java.util.function.Consumer
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import ziphil.dictionary.ConjugationResolver
import ziphil.dictionary.DetailDictionary
import ziphil.dictionary.Dictionary
import ziphil.dictionary.DictionaryBase
import ziphil.dictionary.DictionaryConverter
import ziphil.dictionary.DictionaryLoader
import ziphil.dictionary.DictionarySaver
import ziphil.dictionary.EditableDictionary
import ziphil.dictionary.EmptyDictionaryConverter
import ziphil.dictionary.IdentityDictionaryConverter
import ziphil.dictionary.SearchType
import ziphil.dictionary.personal.PersonalDictionary
import ziphil.module.Setting
import ziphil.module.Strings
import ziphil.module.akrantiain.Akrantiain
import ziphil.module.akrantiain.AkrantiainParseException
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class SlimeDictionary extends DictionaryBase<SlimeWord, SlimeSuggestion> implements EditableDictionary<SlimeWord, SlimeWord>, DetailDictionary<SlimeSearchParameter> {

  private static ObjectMapper $$mapper = createObjectMapper()

  private Integer $validMinId = 0
  private List<String> $registeredTags = ArrayList.new()
  private List<String> $registeredEquivalentTitles = ArrayList.new()
  private List<String> $registeredInformationTitles = ArrayList.new()
  private List<String> $registeredVariationTitles = ArrayList.new()
  private List<String> $registeredRelationTitles = ArrayList.new()
  private String $alphabetOrder = null
  private List<String> $punctuations = Arrays.asList(",", "、")
  private List<String> $plainInformationTitles = ArrayList.new()
  private List<String> $informationTitleOrder = null
  private SlimeWord $defaultWord = SlimeWord.new()
  private Akrantiain $akrantiain = null
  private String $akrantiainSource = null
  private Map<String, TreeNode> $externalData = HashMap.new()
  private Consumer<Integer> $onLinkClicked

  public SlimeDictionary(String name, String path) {
    super(name, path)
  }

  public SlimeDictionary(String name, String path, Dictionary oldDictionary) {
    super(name, path, oldDictionary)
  }

  protected void prepare() {
    setupWords()
    setupSuggestions()
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
    resetSuggestions()
    updateWordPredicate() { SlimeWord word ->   
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
        if (!nameSearchType.matches(name, searchName)) {
          predicate = false
        }
      }
      if (searchEquivalentName != null || searchEquivalentTitle != null) {
        Boolean equivalentPredicate = false
        searchEquivalentName = searchEquivalentName ?: ""
        for (SlimeEquivalent equivalent : equivalents) {
          String equivalentTitle = equivalent.getTitle()
          for (String equivalentName : equivalent.getNames()) {
            if (equivalentSearchType.matches(equivalentName, searchEquivalentName) && (searchEquivalentTitle == null || equivalentTitle == searchEquivalentTitle)) {
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
          if (informationSearchType.matches(informationText, searchInformationText) && (searchInformationTitle == null || informationTitle == searchInformationTitle)) {
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
    updateOnBackground()
  }

  public void addWord(SlimeWord word) {
    if (containsId(word.getId(), word)) {
      word.setId($validMinId)
    }
    $words.add(word)
    updateOnBackground()
  }

  public void removeWord(SlimeWord word) {
    for (SlimeWord otherWord : $words) {
      Boolean isChanged = otherWord.getRelations().removeAll{relation -> relation.getId() == word.getId()}
      if (isChanged) {
        otherWord.change()
      }
    }
    $words.remove(word)
    updateOnBackground()
  }

  public void update() {
    updateRegisteredTitles()
    updatePlainInformationTitles()
    updateInformationTitleOrder()
    updateAkrantiain()
    $isChanged = true
  }

  public void updateFirst() {
    updateRegisteredTitles()
    updatePlainInformationTitles()
    updateInformationTitleOrder()
    updateAkrantiain()
    $isChanged = true
  }

  public void updateMinimum() {
    updateComparisonStrings()
    updateAkrantiain()
    $isChanged = true
  }

  private void updateRegisteredTitles() {
    $validMinId = -1
    $registeredTags.clear()
    $registeredEquivalentTitles.clear()
    $registeredInformationTitles.clear()
    $registeredVariationTitles.clear()
    $registeredRelationTitles.clear()
    for (SlimeWord word : $words) {
      if (word.getId() >= $validMinId) {
        $validMinId = word.getId()
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
    $validMinId ++
  }

  private void updatePlainInformationTitles() {
    List<String> newPlainInformationTitles = ArrayList.new()
    for (String title : $registeredInformationTitles) {
      if ($plainInformationTitles.contains(title)) {
        newPlainInformationTitles.add(title)
      }
    }
    $plainInformationTitles = newPlainInformationTitles
  }

  private void updateInformationTitleOrder() {
    if ($informationTitleOrder != null) {
      List<String> newInformationTitleOrder = ArrayList.new()
      for (String title : $informationTitleOrder) {
        if ($registeredInformationTitles.contains(title)) {
          newInformationTitleOrder.add(title)
        }
      }
      for (String title : $registeredInformationTitles) {
        if (!newInformationTitleOrder.contains(title)) {
          newInformationTitleOrder.add(title)
        }
      }
      $informationTitleOrder = newInformationTitleOrder
    }
  }

  private void updateComparisonStrings() {
    for (SlimeWord word : $words) {
      word.updateComparisonString()
    }
  }

  private void updateAkrantiain() {
    Akrantiain akrantiain = null
    if ($akrantiainSource != null) {
      try {
        akrantiain = Akrantiain.new()
        akrantiain.load($akrantiainSource)
      } catch (AkrantiainParseException exception) {
        akrantiain = null
      }
    }
    $akrantiain = akrantiain  // snoj ファイルの読み込み中に実行されないようにフィールドは最後に更新する
  }

  public SlimeWord emptyWord(String defaultName) {
    SlimeWord word = copiedWordBase($defaultWord, false)
    word.setId($validMinId)
    word.setName(defaultName ?: "")
    word.setDictionary(this)
    word.update()
    return word
  }

  private SlimeWord copiedWordBase(SlimeWord oldWord, Boolean updates) {
    SlimeWord newWord = SlimeWord.new()
    newWord.setId(oldWord.getId())
    newWord.setName(oldWord.getName())
    newWord.setRawEquivalents(oldWord.getRawEquivalents())
    newWord.setTags(oldWord.getTags())
    newWord.setInformations(oldWord.getInformations())
    newWord.setVariations(oldWord.getVariations())
    newWord.setRelations(oldWord.getRelations())
    newWord.setDictionary(this)
    if (updates) {
      newWord.update()
    }
    return newWord
  }

  public SlimeWord copiedWord(SlimeWord oldWord) {
    return copiedWordBase(oldWord, true)
  }

  public SlimeWord inheritedWord(SlimeWord oldWord) {
    SlimeWord newWord = copiedWordBase(oldWord, false)
    newWord.setId($validMinId)
    newWord.update()
    return newWord
  }

  public Object plainWord(SlimeWord oldWord) {
    SlimePlainWord newWord = SlimePlainWord.new()
    Integer newId = oldWord.getId()
    String newName = oldWord.getName()
    List<SlimeEquivalent> newEquivalents = ArrayList.new()
    for (SlimeEquivalent equivalent : oldWord.getRawEquivalents()) {
      SlimeEquivalent newEquivalent = SlimeEquivalent.new()
      newEquivalent.setTitle(equivalent.getTitle())
      newEquivalent.setNames((List<String>)((ArrayList<String>)equivalent.getNames()).clone())
      newEquivalents.add(newEquivalent)
    }
    List<String> newTags = (List<String>)((ArrayList<String>)oldWord.getTags()).clone()
    List<SlimeInformation> newInformations = ArrayList.new()
    for (SlimeInformation information : oldWord.getInformations()) {
      SlimeInformation newInformation = SlimeInformation.new()
      newInformation.setTitle(information.getTitle())
      newInformation.setText(information.getText())
      newInformations.add(newInformation)
    }
    List<SlimeVariation> newVariations = ArrayList.new()
    for (SlimeVariation variation : oldWord.getVariations()) {
      SlimeVariation newVariation = SlimeVariation.new()
      newVariation.setTitle(variation.getTitle())
      newVariation.setName(variation.getName())
      newVariations.add(newVariation)
    }
    List<SlimeRelation> newRelations = ArrayList.new()
    for (SlimeRelation relation : oldWord.getRelations()) {
      SlimeRelation newRelation = SlimeRelation.new()
      newRelation.setTitle(relation.getTitle())
      newRelation.setId(relation.getId())
      newRelation.setName(relation.getName())
      newRelations.add(newRelation)
    }
    newWord.setId(newId)
    newWord.setName(newName)
    newWord.setEquivalents(newEquivalents)
    newWord.setTags(newTags)
    newWord.setInformations(newInformations)
    newWord.setVariations(newVariations)
    newWord.setRelations(newRelations)
    return newWord
  }

  // 同じ単語データをもつ SlimeDictionary オブジェクトを作成します。
  // この処理は浅いコピーを行うので、コピー後の SlimeDictionary オブジェクトの各単語データはコピー前のものと同一です。
  // 同じ SlimeDictionary オブジェクトに対して複数の単語リストを表示させたいときに、表示条件や表示順が同期されるのを防ぐ目的で使用されます。
  public SlimeDictionary copy() {
    SlimeDictionary dictionary = SlimeDictionary.new($name, null)
    dictionary.setPath($path)
    dictionary.getRawWords().addAll($words)
    return dictionary
  }

  public Boolean containsId(Integer id, SlimeWord excludedWord) {
    return $words.any{word -> word != excludedWord && word.getId() == id}
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

  protected ConjugationResolver createConjugationResolver() {
    SlimeConjugationResolver conjugationResolver = SlimeConjugationResolver.new($suggestions)
    return conjugationResolver
  }

  protected DictionaryLoader createLoader() {
    SlimeDictionaryLoader loader = SlimeDictionaryLoader.new(this, $path)
    loader.setMapper($$mapper)
    return loader
  }

  protected DictionaryConverter createConverter(Dictionary oldDictionary) {
    if (oldDictionary instanceof PersonalDictionary) {
      SlimePersonalDictionaryConverter converter = SlimePersonalDictionaryConverter.new(this, oldDictionary)
      return converter
    } else if (oldDictionary instanceof SlimeDictionary) {
      IdentityDictionaryConverter converter = IdentityDictionaryConverter.new(this, (SlimeDictionary)oldDictionary)
      return converter
    } else {
      EmptyDictionaryConverter converter = EmptyDictionaryConverter.new(this, oldDictionary)
      return converter
    }
  } 

  protected DictionarySaver createSaver() {
    SlimeDictionarySaver saver = SlimeDictionarySaver.new(this, $path)
    saver.setMapper($$mapper)
    return saver
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

  public List<String> getPunctuations() {
    return $punctuations
  }

  public void setPunctuations(List<String> punctuations) {
    $punctuations = punctuations
  }

  public List<String> getPlainInformationTitles() {
    return $plainInformationTitles
  }

  public void setPlainInformationTitles(List<String> plainInformationTitles) {
    $plainInformationTitles = plainInformationTitles
  }

  public List<String> getInformationTitleOrder() {
    return $informationTitleOrder
  }

  public void setInformationTitleOrder(List<String> informationTitleOrder) {
    $informationTitleOrder = informationTitleOrder
  }

  public SlimeWord getDefaultWord() {
    return $defaultWord
  }

  public void setDefaultWord(SlimeWord defaultWord) {
    $defaultWord = defaultWord
  }

  public Akrantiain getAkrantiain() {
    return $akrantiain
  }

  public String getAkrantiainSource() {
    return $akrantiainSource
  }

  public void setAkrantiainSource(String akrantiainSource) {
    $akrantiainSource = akrantiainSource
  }

  public Map<String, TreeNode> getExternalData() {
    return $externalData
  }

  public void setExternalData(Map<String, TreeNode> externalData) {
    $externalData = externalData
  }

  public Consumer<Integer> getOnLinkClicked() {
    return $onLinkClicked
  }

  public void setOnLinkClicked(Consumer<Integer> onLinkClicked) {
    $onLinkClicked = onLinkClicked
  }

}