package ziphil.dictionary.slime

import com.fasterxml.jackson.core.TreeNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import groovy.transform.CompileStatic
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
import ziphil.dictionary.NormalSearchParameter
import ziphil.dictionary.PseudoWord
import ziphil.dictionary.SearchType
import ziphil.dictionary.personal.PersonalDictionary
import ziphil.dictionary.shaleia.ShaleiaDictionary
import ziphil.module.Setting
import ziphil.module.Strings
import ziphil.module.akrantiain.Akrantiain
import ziphil.module.akrantiain.AkrantiainParseException
import ziphilib.transform.InnerClass
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class SlimeDictionary extends DictionaryBase<SlimeWord, SlimeSuggestion> implements EditableDictionary<SlimeWord, SlimeWord>, DetailDictionary<SlimeSearchParameter> {

  private static ObjectMapper $$mapper = createObjectMapper()

  private Int $validMinId = 1
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
  private List<RelationRequest> $relationRequests = ArrayList.new()
  private Map<String, TreeNode> $externalData = HashMap.new()

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
    Int searchId = parameter.getId()
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
      Int id = word.getId()
      String name = word.getName()
      List<SlimeEquivalent> equivalents = word.getRawEquivalents()
      List<SlimeInformation> informations = word.getInformations()
      List<String> tags = word.getTags()
      if (parameter.hasId()) {
        if (id != searchId) {
          predicate = false
        }
      }
      if (parameter.hasName()) {
        if (!nameSearchType.matches(name, searchName)) {
          predicate = false
        }
      }
      if (parameter.hasEquivalent()) {
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
      if (parameter.hasInformation()) {
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
      if (parameter.hasTag()) {
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
      for (RelationRequest request : $relationRequests) {
        SlimeRelation requestRelation = request.getRelation()
        if (requestRelation.getId() == newWord.getId()) {
          requestRelation.setId($validMinId)
        }
      }
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
    complyRelationRequests()
    updateOnBackground()
  }

  private void addWordWithoutUpdate(SlimeWord word) {
    if (containsId(word.getId(), word)) {
      for (RelationRequest request : $relationRequests) {
        SlimeRelation requestRelation = request.getRelation()
        if (requestRelation.getId() == word.getId()) {
          requestRelation.setId($validMinId)
        }
      }
      word.setId($validMinId)
    }
    $words.add(word)
  }

  public void addWord(SlimeWord word) {
    addWordWithoutUpdate(word)
    complyRelationRequests()
    updateOnBackground()
  }

  public void addWords(List<? extends SlimeWord> words) {
    for (SlimeWord word : words) {
      addWordWithoutUpdate(word)
      incrementValidMinId(word)
    }
    complyRelationRequests()
    updateOnBackground()
  }

  public void removeWord(SlimeWord word) {
    for (SlimeWord otherWord : $words) {
      Boolean changed = otherWord.getRelations().removeAll{it.getId() == word.getId()}
      if (changed) {
        otherWord.change()
      }
    }
    $words.remove(word)
    updateOnBackground()
  }

  public void requestRelation(SlimeWord word, SlimeRelation relation) {
    RelationRequest request = RelationRequest.new(word, relation)
    $relationRequests.add(request)
  }

  private void complyRelationRequests() {
    for (RelationRequest request : $relationRequests) {
      SlimeWord word = request.getWord()
      word.getRelations().add(request.getRelation())
      word.update()
    }
    $relationRequests.clear()
  }

  public void update() {
    updateRegisteredTitles()
    updatePlainInformationTitles()
    updateInformationTitleOrder()
    updateAkrantiain()
    $changed = true
  }

  public void updateFirst() {
    validate()
    updateRegisteredTitles()
    updatePlainInformationTitles()
    updateInformationTitleOrder()
    updateAkrantiain()
    $changed = true
  }

  public void updateMinimum() {
    updateComparisonStrings()
    updateAkrantiain()
    $changed = true
  }

  public void validate() {
    validateIds()
    validateRelations()
  }

  private void validateIds() {
    Set<IntegerClass> ids = HashSet.new()
    for (SlimeWord word : $words) {
      if (!ids.contains(word.getId())) {
        ids.add(word.getId()) 
      } else {
        throw SlimeValidationException.new("Duplicate id")
      }
    }
  }

  private void validateRelations() {
    Map<IntegerClass, String> wordNames = HashMap.new()
    Map<IntegerClass, String> relationNames = HashMap.new()
    for (SlimeWord word : $words) {
      Int wordId = word.getId()
      wordNames[wordId] = word.getName()
      for (SlimeRelation relation : word.getRelations()) {
        Int relationId = relation.getId()
        String previousRelationName = relationNames[relationId]
        if (previousRelationName == null) {
          relationNames[relation.getId()] = relation.getName()
        } else {
          if (relation.getName() != previousRelationName) {
            throw SlimeValidationException.new("Invalid relation")
          }
        }
      }
    }
    for (Map.Entry<IntegerClass, String> entry : relationNames) {
      if (wordNames[entry.getKey()] != entry.getValue()) {
        throw SlimeValidationException.new("Invalid relation")
      }
    }
  }

  private void incrementValidMinId(SlimeWord word) {
    if (word.getId() >= $validMinId) {
      $validMinId = word.getId() + 1
    }
  }

  private void updateRegisteredTitles() {
    $validMinId = 0
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
    $akrantiain = akrantiain
  }

  public SlimeWord createWord(String defaultName) {
    SlimeWord word = prepareCopyWord($defaultWord, false)
    word.setId($validMinId)
    word.setName(defaultName ?: "")
    word.setDictionary(this)
    word.update()
    return word
  }

  private SlimeWord prepareCopyWord(SlimeWord oldWord, Boolean updates) {
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

  public SlimeWord copyWord(SlimeWord oldWord) {
    return prepareCopyWord(oldWord, true)
  }

  public SlimeWord inheritWord(SlimeWord oldWord) {
    SlimeWord newWord = prepareCopyWord(oldWord, false)
    newWord.setId($validMinId)
    newWord.update()
    return newWord
  }

  public SlimeWord determineWord(String name, PseudoWord pseudoWord) {
    SlimeWord word = SlimeWord.new()
    List<String> pseudoEquivalents = pseudoWord.getEquivalents()
    String pseudoContent = pseudoWord.getContent()
    word.setId($validMinId)
    word.setName(name)
    word.getRawEquivalents().add(SlimeEquivalent.new("", pseudoEquivalents))
    if (pseudoContent != null) {
      word.getInformations().add(SlimeInformation.new("", pseudoContent))
    }
    word.setDictionary(this)
    word.update()
    return word
  }

  public Object createPlainWord(SlimeWord oldWord) {
    SlimePlainWord newWord = SlimePlainWord.new()
    Int newId = oldWord.getId()
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

  public SlimeDictionary copy() {
    SlimeDictionary dictionary = SlimeDictionary.new($name, null)
    dictionary.setPath($path)
    dictionary.getRawWords().addAll($words)
    return dictionary
  }

  public Boolean containsId(Int id, SlimeWord excludedWord) {
    return $words.any{it != excludedWord && it.getId() == id}
  }

  private void setupWords() {
    $sortedWords.setComparator() { SlimeWord firstWord, SlimeWord secondWord ->
      Int firstId = firstWord.getId()
      Int secondId = secondWord.getId()
      String firstString = firstWord.getComparisonString()
      String secondString = secondWord.getComparisonString()
      Int result = firstString <=> secondString
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

  protected ConjugationResolver createConjugationResolver(NormalSearchParameter parameter) {
    SlimeConjugationResolver conjugationResolver = SlimeConjugationResolver.new($suggestions)
    return conjugationResolver
  }

  protected DictionaryLoader createLoader() {
    SlimeDictionaryLoader loader = SlimeDictionaryLoader.new(this, $path)
    loader.setMapper($$mapper)
    return loader
  }

  protected DictionaryConverter createConverter(Dictionary oldDictionary) {
    if (oldDictionary instanceof ShaleiaDictionary) {
      SlimeShaleiaDictionaryConverter converter = SlimeShaleiaDictionaryConverter.new(this, oldDictionary)
      return converter
    } else if (oldDictionary instanceof PersonalDictionary) {
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

  public Int getValidMinId() {
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

}


@InnerClass(SlimeDictionary)
@Ziphilify
private static class RelationRequest {

  private SlimeWord $word
  private SlimeRelation $relation

  public RelationRequest(SlimeWord word, SlimeRelation relation) {
    $word = word
    $relation = relation
  }

  public SlimeWord getWord() {
    return $word
  }

  public SlimeRelation getRelation() {
    return $relation
  }

}