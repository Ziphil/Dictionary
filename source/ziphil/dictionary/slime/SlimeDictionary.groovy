package ziphil.dictionary.slime

import com.fasterxml.jackson.core.TreeNode
import groovy.transform.CompileStatic
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import ziphil.dictionary.ConjugationResolver
import ziphil.dictionary.Dictionary
import ziphil.dictionary.EditableDictionaryBase
import ziphil.dictionary.ExportConfig
import ziphil.dictionary.IndividualSetting
import ziphil.dictionary.Loader
import ziphil.dictionary.NormalSearchParameter
import ziphil.dictionary.PseudoWord
import ziphil.dictionary.Saver
import ziphil.dictionary.SearchType
import ziphil.dictionary.WordOrderType
import ziphil.module.Setting
import ziphil.module.Strings
import ziphil.module.akrantiain.Akrantiain
import ziphil.module.akrantiain.AkrantiainParseException
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class SlimeDictionary extends EditableDictionaryBase<SlimeWord, SlimeSuggestion, SlimeDictionaryFactory> {

  private Int $validMinNumber = 1
  private List<String> $registeredTags = ArrayList.new()
  private List<String> $registeredEquivalentTitles = ArrayList.new()
  private List<String> $registeredInformationTitles = ArrayList.new()
  private List<String> $registeredVariationTitles = ArrayList.new()
  private List<String> $registeredRelationTitles = ArrayList.new()
  private String $alphabetOrder = ""
  private WordOrderType $wordOrderType = WordOrderType.UNICODE
  private List<String> $punctuations = Arrays.asList(",", "、")
  private String $ignoredEquivalentRegex = ""
  private String $pronunciationTitle = null
  private List<String> $plainInformationTitles = ArrayList.new()
  private List<String> $informationTitleOrder = null
  private String $nameFontFamily = null
  private SlimeWord $defaultWord = SlimeWord.new()
  private Akrantiain $akrantiain = null
  private String $akrantiainSource = null
  private List<SlimeRelationRequest> $relationRequests = ArrayList.new()
  private Map<String, TreeNode> $externalData = HashMap.new()

  public SlimeDictionary(String name, String path) {
    super(name, path)
  }

  public SlimeDictionary(String name, String path, Loader loader) {
    super(name, path, loader)
  }

  protected void prepare() {
    setupSuggestions()
  }

  // これから追加もしくは変更される単語データである word の ID を、追加もしくは変更の後に矛盾が生じないように修正します。
  // また、この修正に応じて変更が必要となる内部データの修正も同時に行います。
  private void correctNumber(SlimeWord word) {
    if (containsNumber(word.getNumber(), word)) {
      word.setNumber($validMinNumber)
    }
  }

  private void correctNumber(List<? extends SlimeWord> words) {
    for (SlimeWord word : words) {
      if (containsNumber(word.getNumber(), word)) {
        word.setNumber($validMinNumber)
        $validMinNumber ++
      }
    }
  }

  // この辞書に登録されている word が変更されることによって、関連語参照などに矛盾が生じないように修正します。
  // 具体的には、word を関連語として参照している単語データを更新します。
  private void correctRelationsByModify(SlimeWord word) {
    for (SlimeWord otherWord : $words) {
      if (otherWord.getRelations().any{it.getWord() == word}) {
        otherWord.update()
      }
    }
  }

  // これから追加もしくは変更される単語データである word の関連語データを、追加もしくは変更の後に矛盾が生じないように修正します。
  // 具体的には、存在しない単語を参照している関連語データを word から削除します。
  private void correctRelationsByAdd(SlimeWord word) {
    List<SlimeRelation> removedRelations = ArrayList.new()
    for (SlimeRelation relation : word.getRelations()) {
      SlimeWord relationWord = relation.getWord()
      if (!$words.contains(relationWord) && relationWord != word) {
        removedRelations.add(relation)
      }
    }
    word.getRelations().removeAll(removedRelations)
  }

  private void correctRelationsByAdd(List<? extends SlimeWord> words) {
    for (SlimeWord word : words) {
      List<SlimeRelation> removedRelations = ArrayList.new()
      for (SlimeRelation relation : word.getRelations()) {
        SlimeWord relationWord = relation.getWord()
        if (!$words.contains(relationWord) && !words.contains(relationWord)) {
          removedRelations.add(relation)
        }
      }
      word.getRelations().removeAll(removedRelations)
    }
  }

  // この辞書から word が削除されることによって、関連語参照などに矛盾が生じないように修正します。
  private void correctRelationsByRemove(SlimeWord word) {
    for (SlimeWord otherWord : $words) {
      Boolean changed = otherWord.getRelations().removeAll{it.getWord() == word}
      if (changed) {
        otherWord.change()
      }
    }
  }

  public synchronized void modifyWord(SlimeWord oldWord, SlimeWord newWord) {
    correctNumber(newWord)
    correctRelationsByAdd(newWord)
    correctRelationsByModify(newWord)
    complyRelationRequests()
    update()
  }

  public synchronized void addWord(SlimeWord word) {
    correctNumber(word)
    correctRelationsByAdd(word)
    $words.add(word)
    complyRelationRequests()
    update()
  }

  public synchronized void addWords(List<? extends SlimeWord> words) {
    correctNumber(words)
    correctRelationsByAdd(words)
    for (SlimeWord word : words) {
      $words.add(word)
    }
    complyRelationRequests()
    update()
  }

  public synchronized void removeWord(SlimeWord word) {
    correctRelationsByRemove(word)
    $words.remove(word)
    update()
  }

  public synchronized void removeWords(List<? extends SlimeWord> words) {
    for (SlimeWord word : words) {
      correctRelationsByRemove(word)
      $words.remove(word)
    }
    update()
  }

  public synchronized void mergeWord(SlimeWord mergedWord, SlimeWord removedWord) {
    correctRelationsByRemove(removedWord)
    correctRelationsByAdd(mergedWord)
    correctRelationsByModify(mergedWord)
    $words.remove(removedWord)
    update()
  }

  public void requestRelation(SlimeRelationRequest request) {
    $relationRequests.add(request)
  }

  private void complyRelationRequests() {
    for (SlimeRelationRequest request : $relationRequests) {
      SlimeWord word = request.getWord()
      word.getRelations().add(request.getRelation())
      word.update()
    }
    $relationRequests.clear()
  }

  private void update() {
    updateValidMinNumber()
    updateRegisteredTitles()
    updatePlainInformationTitles()
    updateInformationTitleOrder()
    $changed = true
  }

  public void updateFirst() {
    validate()
    updateValidMinNumber()
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
    validateNumbers()
    validateRelations()
  }

  private void validateNumbers() {
    Set<IntegerClass> numbers = HashSet.new()
    for (SlimeWord word : $words) {
      if (!numbers.contains(word.getNumber())) {
        numbers.add(word.getNumber()) 
      } else {
        throw SlimeValidationException.new("ID ${word.getNumber()} is duplicate")
      }
    }
  }

  private void validateRelations() {
    Map<IntegerClass, String> wordNames = HashMap.new()
    Map<IntegerClass, String> relationNames = HashMap.new()
    for (SlimeWord word : $words) {
      for (SlimeRelation relation : word.getRelations()) {
        SlimeWord relationWord = relation.getWord()
        if (relationWord == null) {
          throw SlimeValidationException.new("Unresolved relation in [${word.getNumber()}: ${word.getName()}]")
        } else if (!$words.contains(relationWord)) {
          throw SlimeValidationException.new("No [${relationWord.getNumber()}: ${relationWord.getName()}] specified as a relation in [${word.getNumber()}: ${word.getName()}]")
        }
      }
    }
  }

  private void updateValidMinNumber() {
    Int validMinNumber = 0
    for (SlimeWord word : $words) {
      if (word.getNumber() >= validMinNumber) {
        validMinNumber = word.getNumber()
      }
    }
    $validMinNumber = validMinNumber + 1
  }

  private void updateRegisteredTitles() {
    $registeredTags.clear()
    $registeredEquivalentTitles.clear()
    $registeredInformationTitles.clear()
    $registeredVariationTitles.clear()
    $registeredRelationTitles.clear()
    for (SlimeWord word : $words) {
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
    word.setNumber($validMinNumber)
    word.setName(defaultName ?: "")
    word.getRelations().clear()
    word.setDictionary(this)
    word.update()
    return word
  }

  private SlimeWord prepareCopyWord(SlimeWord oldWord, Boolean updates) {
    SlimeWord newWord = SlimeWord.new()
    newWord.setNumber(oldWord.getNumber())
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

  public List<? extends SlimeWord> copyWords(List<? extends SlimeWord> oldWords) {
    List<SlimeWord> newWords = ArrayList.new()
    Map<SlimeWord, SlimeWord> correspondingWords = HashMap.new()
    for (SlimeWord oldWord : oldWords) {
      SlimeWord newWord = prepareCopyWord(oldWord, false)
      newWords.add(newWord)
      correspondingWords[oldWord] = newWord
    }
    for (SlimeWord newWord : newWords) {
      List<SlimeRelation> nextRelations = ArrayList.new()
      for (SlimeRelation oldRelation : newWord.getRelations()) {
        SlimeWord oldRelationWord = oldRelation.getWord()
        if (correspondingWords.containsKey(oldRelationWord)) {
          SlimeRelation nextRelation = SlimeRelation.new()
          nextRelation.setTitle(oldRelation.getTitle())
          nextRelation.setWord(correspondingWords[oldRelationWord])
          nextRelations.add(nextRelation)
        } else {
          nextRelations.add(oldRelation)
        }
      }
      newWord.setRelations(nextRelations)
    }
    return newWords
  }

  public SlimeWord inheritWord(SlimeWord oldWord) {
    SlimeWord newWord = prepareCopyWord(oldWord, false)
    newWord.setNumber($validMinNumber)
    newWord.update()
    return newWord
  }

  public SlimeWord determineWord(String name, PseudoWord pseudoWord) {
    SlimeWord word = SlimeWord.new()
    List<String> pseudoEquivalents = pseudoWord.getEquivalents()
    String pseudoContent = pseudoWord.getContent()
    word.setNumber($validMinNumber)
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
    Int newNumber = oldWord.getNumber()
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
    List<SlimePlainRelation> newRelations = ArrayList.new()
    for (SlimeRelation relation : oldWord.getRelations()) {
      SlimePlainRelation newRelation = SlimePlainRelation.new()
      newRelation.setTitle(relation.getTitle())
      newRelation.setWord(createPlainWord(relation.getWord()))
      newRelations.add(newRelation)
    }
    newWord.setNumber(newNumber)
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

  public Boolean containsNumber(Int number, SlimeWord excludedWord) {
    return $words.any{it != excludedWord && it.getNumber() == number}
  }

  public SlimeWord findName(String name, SlimeWord excludedWord) {
    for (SlimeWord word : $words) {
      if (word != excludedWord && word.getName() == name) {
        return word
      }
    }
    return null
  }

  public String firstPunctuation() {
    String punctuation = $punctuations[0] ?: ""
    if (punctuation == ",") {
      punctuation = punctuation + " "
    }
    return punctuation
  }

  private void setupSuggestions() {
    SlimeSuggestion suggestion = SlimeSuggestion.new()
    suggestion.setDictionary(this)
    $suggestions.add(suggestion)
  }

  protected Comparator<? super SlimeWord> createCustomWordComparator() {
    Comparator<SlimeWord> comparator = { SlimeWord firstWord, SlimeWord secondWord ->
      Int firstNumber = firstWord.getNumber()
      Int secondNumber = secondWord.getNumber()
      String firstString = firstWord.getComparisonString()
      String secondString = secondWord.getComparisonString()
      Int result = firstString <=> secondString
      if (result == 0) {
        return firstNumber <=> secondNumber
      } else {
        return result
      }
    }
    return comparator
  }

  protected ConjugationResolver createConjugationResolver() {
    SlimeConjugationResolver conjugationResolver = SlimeConjugationResolver.new($suggestions)
    return conjugationResolver
  }

  protected IndividualSetting createIndividualSetting() {
    SlimeIndividualSetting individualSetting = SlimeIndividualSetting.create(this)
    return individualSetting
  }

  public Int getValidMinNumber() {
    return $validMinNumber
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

  public WordOrderType getWordOrderType() {
    return $wordOrderType
  }

  public void setWordOrderType(WordOrderType wordOrderType) {
    $wordOrderType = wordOrderType
  }

  public List<String> getPunctuations() {
    return $punctuations
  }

  public void setPunctuations(List<String> punctuations) {
    $punctuations = punctuations
  }

  public String getIgnoredEquivalentRegex() {
    return $ignoredEquivalentRegex
  }

  public void setIgnoredEquivalentRegex(String ignoredEquivalentRegex) {
    $ignoredEquivalentRegex = ignoredEquivalentRegex
  }

  public String getPronunciationTitle() {
    return $pronunciationTitle
  }

  public void setPronunciationTitle(String pronunciationTitle) {
    $pronunciationTitle = pronunciationTitle
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

  public String getNameFontFamily() {
    return $nameFontFamily
  }

  public void setNameFontFamily(String nameFontFamily) {
    $nameFontFamily = nameFontFamily
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