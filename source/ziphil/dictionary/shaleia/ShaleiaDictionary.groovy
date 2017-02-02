package ziphil.dictionary.shaleia

import groovy.transform.CompileStatic
import java.time.Duration
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.util.function.Consumer
import java.util.regex.Matcher
import java.util.regex.Pattern
import java.util.regex.PatternSyntaxException
import javafx.concurrent.Task
import ziphil.dictionary.DetailDictionary
import ziphil.dictionary.DictionaryBase
import ziphil.dictionary.EditableDictionary
import ziphil.dictionary.SearchType
import ziphil.module.Setting
import ziphil.module.Strings
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class ShaleiaDictionary extends DictionaryBase<ShaleiaWord, ShaleiaSuggestion> implements EditableDictionary<ShaleiaWord, ShaleiaWord>, DetailDictionary<ShaleiaSearchParameter> {

  private String $alphabetOrder = ""
  private String $changeData = ""
  private Map<String, List<String>> $changes = HashMap.new()
  private Integer $systemWordSize = 0
  private Consumer<String> $onLinkClicked

  public ShaleiaDictionary(String name, String path) {
    super(name, path)
    load()
    setupWords()
    setupSuggestions()
  }

  public void searchDetail(ShaleiaSearchParameter parameter) {
    String searchName = parameter.getName()
    SearchType nameSearchType = parameter.getNameSearchType()
    String searchEquivalent = parameter.getEquivalent()
    SearchType equivalentSearchType = parameter.getEquivalentSearchType()
    String searchData = parameter.getData()
    SearchType dataSearchType = parameter.getDataSearchType()
    resetSuggestions()
    updateWordPredicate() { ShaleiaWord word ->
      Boolean predicate = true
      String name = word.getName()
      List<String> equivalents = word.getEquivalents()
      String data = word.getData()
      if (searchName != null) {
        if (!SearchType.matches(nameSearchType, name, searchName)) {
          predicate = false
        }
      }
      if (searchEquivalent != null) {
        Boolean equivalentPredicate = false
        for (String equivalent : equivalents) {
          if (SearchType.matches(equivalentSearchType, equivalent, searchEquivalent)) {
            equivalentPredicate = true
          }
        }
        if (!equivalentPredicate) {
          predicate = false
        }
      }
      if (searchData != null) {
        if (!SearchType.matches(dataSearchType, data, searchData)) {
          predicate = false
        }
      }
      return predicate
    }
  }

  protected void checkWholeSuggestion(String search, String convertedSearch) {
    Setting setting = Setting.getInstance()
    Boolean ignoresAccent = setting.getIgnoresAccent()
    Boolean ignoresCase = setting.getIgnoresCase()
    if ($changes.containsKey(convertedSearch)) {
      for (String newName : $changes[convertedSearch]) {
        ShaleiaPossibility possibility = ShaleiaPossibility.new(newName, "変更前")
        $suggestions[0].getPossibilities().add(possibility)
        $suggestions[0].setDisplayed(true)
        $suggestions[0].update()
      }
    }
  }

  public void modifyWord(ShaleiaWord oldWord, ShaleiaWord newWord) {
    newWord.updateComparisonString($alphabetOrder)
    newWord.updateContentPane()
    $isChanged = true
  }

  public void addWord(ShaleiaWord word) {
    word.setDictionary(this)
    word.updateComparisonString($alphabetOrder)
    $words.add(word)
    $isChanged = true
  }

  public void removeWord(ShaleiaWord word) {
    $words.remove(word)
    $isChanged = true
  }

  public void update() {
    parseChanges()
    calculateSystemWordSize()
    $isChanged = true
  }

  public void updateMinimum() {
    parseChanges()
    $isChanged = true
  }

  private void parseChanges() {
    Setting setting = Setting.getInstance()
    Boolean ignoresAccent = setting.getIgnoresAccent()
    Boolean ignoresCase = setting.getIgnoresCase()
    BufferedReader reader = BufferedReader.new(StringReader.new($changeData))
    try {
      $changes.clear()
      for (String line ; (line = reader.readLine()) != null ;) {
        Matcher matcher = line =~ /^\-\s*(\d+)\s*:\s*\{(.+)\}\s*→\s*\{(.+)\}/
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
    $systemWordSize = (Integer)$words.count{word -> word.getUniqueName().startsWith("\$")}
  }

  public ShaleiaWord emptyWord(String defaultName) {
    Long hairiaNumber = LocalDateTime.of(2012, 1, 23, 6, 0).until(LocalDateTime.now(), ChronoUnit.DAYS) + 1
    ShaleiaWord word = ShaleiaWord.new()
    word.setUniqueName(defaultName ?: "")
    word.setData("+ ${hairiaNumber} 〈不〉\n\n=〈〉")
    word.update()
    return word
  }

  private ShaleiaWord copiedWordBase(ShaleiaWord oldWord, Boolean updates) {
    ShaleiaWord newWord = ShaleiaWord.new()
    newWord.setUniqueName(oldWord.getUniqueName())
    newWord.setData(oldWord.getData())
    if (updates) {
      newWord.update()
    }
    return newWord
  }

  public ShaleiaWord copiedWord(ShaleiaWord oldWord) {
    return copiedWordBase(oldWord, true)
  }

  public ShaleiaWord inheritedWord(ShaleiaWord oldWord) {
    Long hairiaNumber = LocalDateTime.of(2012, 1, 23, 6, 0).until(LocalDateTime.now(), ChronoUnit.DAYS) + 1
    ShaleiaWord newWord = copiedWordBase(oldWord, false)
    newWord.setData(oldWord.getData().replaceAll(/^\+\s*(\d+)/, "+ ${hairiaNumber}"))
    newWord.update()
    return newWord
  }

  private void setupWords() {
    $sortedWords.setComparator() { ShaleiaWord firstWord, ShaleiaWord secondWord ->
      String firstString = firstWord.getComparisonString()
      String secondString = secondWord.getComparisonString()
      return firstString <=> secondString
    }
  }

  private void setupSuggestions() {
    ShaleiaSuggestion suggestion = ShaleiaSuggestion.new()
    suggestion.setDictionary(this)
    $suggestions.add(suggestion)
  }

  public Integer totalSize() {
    return $words.size() - $systemWordSize
  }

  protected Task<?> createLoader() {
    ShaleiaDictionaryLoader loader = ShaleiaDictionaryLoader.new(this, $path)
    return loader
  }

  protected Task<?> createSaver() {
    ShaleiaDictionarySaver saver = ShaleiaDictionarySaver.new(this, $path)
    saver.setComparator($sortedWords.getComparator())
    return saver
  }

  public String getExtension() {
    return "xdc"
  }

  public String getAlphabetOrder() {
    return $alphabetOrder
  }

  public void setAlphabetOrder(String alphabetOrder) {
    $alphabetOrder = alphabetOrder
  }

  public String getChangeData() {
    return $changeData
  }

  public void setChangeData(String changeData) {
    $changeData = changeData
  }

  public Consumer<String> getOnLinkClicked() {
    return $onLinkClicked
  }

  public void setOnLinkClicked(Consumer<String> onLinkClicked) {
    $onLinkClicked = onLinkClicked
  }

}