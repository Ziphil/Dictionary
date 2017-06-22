package ziphil.dictionary.shaleia

import groovy.transform.CompileStatic
import java.time.Duration
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.util.regex.Matcher
import java.util.regex.Pattern
import java.util.regex.PatternSyntaxException
import javafx.concurrent.Task
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
import ziphil.module.Setting
import ziphil.module.Strings
import ziphil.module.akrantiain.Akrantiain
import ziphil.module.akrantiain.AkrantiainParseException
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class ShaleiaDictionary extends DictionaryBase<ShaleiaWord, ShaleiaSuggestion> implements EditableDictionary<ShaleiaWord, ShaleiaWord>, DetailDictionary<ShaleiaSearchParameter> {

  private String $alphabetOrder = ""
  private String $changeDescription = ""
  private Map<String, List<String>> $changes = HashMap.new()
  private Akrantiain $akrantiain = null
  private String $akrantiainSource = null
  private String $version = ""
  private Int $systemWordSize = 0

  public ShaleiaDictionary(String name, String path) {
    super(name, path)
  }

  public ShaleiaDictionary(String name, String path, Dictionary oldDictionary) {
    super(name, path, oldDictionary)
  }

  protected void prepare() {
    setupWords()
    setupSuggestions()
  }

  public void searchDetail(ShaleiaSearchParameter parameter) {
    String searchName = parameter.getName()
    SearchType nameSearchType = parameter.getNameSearchType()
    String searchEquivalent = parameter.getEquivalent()
    SearchType equivalentSearchType = parameter.getEquivalentSearchType()
    String searchDescription = parameter.getDescription()
    SearchType descriptionSearchType = parameter.getDescriptionSearchType()
    resetSuggestions()
    updateWordPredicate() { ShaleiaWord word ->
      Boolean predicate = true
      String name = word.getName()
      List<String> equivalents = word.getEquivalents()
      String description = word.getDescription()
      if (parameter.hasName()) {
        if (!nameSearchType.matches(name, searchName)) {
          predicate = false
        }
      }
      if (parameter.hasEquivalent()) {
        Boolean equivalentPredicate = false
        for (String equivalent : equivalents) {
          if (equivalentSearchType.matches(equivalent, searchEquivalent)) {
            equivalentPredicate = true
          }
        }
        if (!equivalentPredicate) {
          predicate = false
        }
      }
      if (parameter.hasDescription()) {
        if (!descriptionSearchType.matches(description, searchDescription)) {
          predicate = false
        }
      }
      return predicate
    }
  }

  public void modifyWord(ShaleiaWord oldWord, ShaleiaWord newWord) {
    $changed = true
  }

  public void addWord(ShaleiaWord word) {
    $words.add(word)
    $changed = true
  }

  public void addWords(List<? extends ShaleiaWord> words) {
    $words.addAll(words)
    $changed = true
  }

  public void removeWord(ShaleiaWord word) {
    $words.remove(word)
    $changed = true
  }

  public void update() {
    calculateSystemWordSize()
    $changed = true
  }

  public void updateFirst() {
    parseChanges()
    calculateSystemWordSize()
    updateAkrantiain()
    $changed = true
  }

  public void updateMinimum() {
    parseChanges()
    updateAkrantiain()
    $changed = true
  }

  private void parseChanges() {
    Setting setting = Setting.getInstance()
    Boolean ignoresAccent = setting.getIgnoresAccent()
    Boolean ignoresCase = setting.getIgnoresCase()
    BufferedReader reader = BufferedReader.new(StringReader.new($changeDescription))
    try {
      $changes.clear()
      for (String line ; (line = reader.readLine()) != null ;) {
        Matcher matcher = line =~ /^\-\s*(.+)\s*:\s*\{(.+)\}\s*→\s*\{(.+)\}/
        if (matcher.matches()) {
          String oldName = Strings.convert(matcher.group(2), ignoresAccent, ignoresCase)
          if (!$changes.containsKey(oldName)) {
            $changes[oldName] = ArrayList.new()
          }
          $changes[oldName].add(matcher.group(3))
        }
      }
    } finally {
      reader.close()
    }
  }

  private void calculateSystemWordSize() {
    $systemWordSize = (Int)$words.count{it.getUniqueName().startsWith("\$")}
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

  public ShaleiaWord createWord(String defaultName) {
    Long hairiaNumber = LocalDateTime.of(2012, 1, 23, 6, 0).until(LocalDateTime.now(), ChronoUnit.DAYS) + 1
    ShaleiaWord word = ShaleiaWord.new()
    word.setUniqueName(defaultName ?: "")
    word.setDescription("+ ${hairiaNumber} 〈不〉\n\n=〈〉")
    word.setDictionary(this)
    word.update()
    return word
  }

  private ShaleiaWord prepareCopyWord(ShaleiaWord oldWord, Boolean updates) {
    ShaleiaWord newWord = ShaleiaWord.new()
    newWord.setUniqueName(oldWord.getUniqueName())
    newWord.setDescription(oldWord.getDescription())
    newWord.setDictionary(this)
    if (updates) {
      newWord.update()
    }
    return newWord
  }

  public ShaleiaWord copyWord(ShaleiaWord oldWord) {
    return prepareCopyWord(oldWord, true)
  }

  public ShaleiaWord inheritWord(ShaleiaWord oldWord) {
    Long hairiaNumber = LocalDateTime.of(2012, 1, 23, 6, 0).until(LocalDateTime.now(), ChronoUnit.DAYS) + 1
    ShaleiaWord newWord = prepareCopyWord(oldWord, false)
    newWord.setDescription(oldWord.getDescription().replaceAll(/^\+\s*(\d+)/, "+ ${hairiaNumber}"))
    newWord.update()
    return newWord
  }

  public ShaleiaWord determineWord(String name, PseudoWord pseudoWord) {
    Long hairiaNumber = LocalDateTime.of(2012, 1, 23, 6, 0).until(LocalDateTime.now(), ChronoUnit.DAYS) + 1
    ShaleiaWord word = ShaleiaWord.new()
    List<String> pseudoEquivalents = pseudoWord.getEquivalents()
    String pseudoContent = pseudoWord.getContent()
    word.setUniqueName(name)
    word.setDescription("+ ${hairiaNumber} 〈不〉\n\n=〈〉 ${pseudoEquivalents.join(", ")}")
    word.setDictionary(this)
    word.update()
    return word
  }

  public Object createPlainWord(ShaleiaWord oldWord) {
    ShaleiaPlainWord newWord = ShaleiaPlainWord.new()
    newWord.setName(oldWord.getName())
    newWord.setUniqueName(oldWord.getUniqueName())
    newWord.setData(oldWord.getDescription())
    return newWord
  }

  public ShaleiaDictionary copy() {
    ShaleiaDictionary dictionary = ShaleiaDictionary.new($name, null)
    dictionary.setPath($path)
    dictionary.setVersion($version)
    dictionary.getRawWords().addAll($words)
    return dictionary
  }

  private void setupWords() {
    $sortedWords.setComparator() { ShaleiaWord firstWord, ShaleiaWord secondWord ->
      String firstString = firstWord.getComparisonString()
      String secondString = secondWord.getComparisonString()
      return firstString <=> secondString
    }
  }

  private void setupSuggestions() {
    ShaleiaSuggestion conjugationSuggestion = ShaleiaSuggestion.new()
    ShaleiaSuggestion changeSuggestion = ShaleiaSuggestion.new()
    conjugationSuggestion.setDictionary(this)
    changeSuggestion.setDictionary(this)
    $suggestions.addAll(conjugationSuggestion, changeSuggestion)
  }

  public Int totalWordSize() {
    return $words.size() - $systemWordSize
  }

  protected ConjugationResolver createConjugationResolver(NormalSearchParameter parameter) {
    ShaleiaConjugationResolver conjugationResolver = ShaleiaConjugationResolver.new($suggestions, parameter, $changes, $version)
    return conjugationResolver
  }

  protected DictionaryLoader createLoader() {
    ShaleiaDictionaryLoader loader = ShaleiaDictionaryLoader.new(this, $path)
    return loader
  }

  protected DictionaryConverter createConverter(Dictionary oldDictionary) {
    if (oldDictionary instanceof ShaleiaDictionary) {
      IdentityDictionaryConverter converter = IdentityDictionaryConverter.new(this, (ShaleiaDictionary)oldDictionary)
      return converter
    } else {
      EmptyDictionaryConverter converter = EmptyDictionaryConverter.new(this, oldDictionary)
      return converter
    }
  } 

  protected DictionarySaver createSaver() {
    ShaleiaDictionarySaver saver = ShaleiaDictionarySaver.new(this, $path)
    saver.setComparator($sortedWords.getComparator())
    return saver
  }

  public String getAlphabetOrder() {
    return $alphabetOrder
  }

  public void setAlphabetOrder(String alphabetOrder) {
    $alphabetOrder = alphabetOrder
  }

  public String getVersion() {
    return $version
  }

  public void setVersion(String version) {
    $version = version
  }

  public String getChangeDescription() {
    return $changeDescription
  }

  public void setChangeDescription(String changeDescription) {
    $changeDescription = changeDescription
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

}