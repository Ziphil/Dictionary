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
import ziphil.dictionary.Dictionary
import ziphil.dictionary.SearchType
import ziphil.dictionary.Suggestion
import ziphil.module.Setting
import ziphil.module.Strings
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class ShaleiaDictionary extends Dictionary<ShaleiaWord, ShaleiaSuggestion> {

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

  public void searchByName(String search, Boolean isStrict) {
    Setting setting = Setting.getInstance()
    Boolean ignoresAccent = setting.getIgnoresAccent()
    Boolean ignoresCase = setting.getIgnoresCase()
    Boolean searchesPrefix = setting.getSearchesPrefix()
    Boolean existsSuggestion = false
    try {
      Pattern pattern = (isStrict) ? null : Pattern.compile(search)
      String convertedSearch = Strings.convert(search, ignoresAccent, ignoresCase)
      for (ShaleiaSuggestion suggestion : $suggestions) {
        suggestion.getPossibilities().clear()
      }
      if (checkWholeSuggestion(search, convertedSearch)) {
        existsSuggestion = true
      }
      $filteredWords.setPredicate() { ShaleiaWord word ->
        if (isStrict) {
          if (!word.getUniqueName().startsWith("\$")) {
            String name = word.getName()
            String convertedName = Strings.convert(name, ignoresAccent, ignoresCase)
            if (checkSuggestion(word, search, convertedSearch)) {
              existsSuggestion = true
            }
            if (search != "") {
              if (searchesPrefix) {
                return convertedName.startsWith(convertedSearch)
              } else {
                return convertedName == convertedSearch
              }
            } else {
              return true
            }
          } else {
            return search == "\$"
          }
        } else {
          if (!word.getUniqueName().startsWith("\$")) {
            Matcher matcher = pattern.matcher(word.getName())
            return matcher.find()
          } else {
            return false
          }
        }
      }
      $filteredSuggestions.setPredicate() { ShaleiaSuggestion suggestion ->
        return existsSuggestion
      }
    } catch (PatternSyntaxException exception) {
    }
    $shufflableWords.unshuffle()
  }

  public void searchByEquivalent(String search, Boolean isStrict) {
    Setting setting = Setting.getInstance()
    Boolean searchesPrefix = setting.getSearchesPrefix()
    try {
      Pattern pattern = Pattern.compile(search)
      $filteredWords.setPredicate() { ShaleiaWord word ->
        if (isStrict) {
          if (!word.getUniqueName().startsWith("\$")) {
            if (search != "") {
              return word.getEquivalents().any() { String equivalent ->
                if (searchesPrefix) {
                  return equivalent.startsWith(search)
                } else {
                  return equivalent == search
                }
              }
            } else {
              return true
            }
          } else {
            return false
          }
        } else {
          if (!word.getUniqueName().startsWith("\$")) {
            return word.getEquivalents().any() { String equivalent ->
              Matcher matcher = pattern.matcher(equivalent)
              return matcher.find()
            }
          } else {
            return false
          }
        }
      }
      $filteredSuggestions.setPredicate() { ShaleiaSuggestion suggestion ->
        return false
      }
    } catch (PatternSyntaxException exception) {
    }
    $shufflableWords.unshuffle()
  }

  public void searchByContent(String search) {
    try {
      Pattern pattern = Pattern.compile(search)
      $filteredWords.setPredicate() { ShaleiaWord word ->
        if (!word.getUniqueName().startsWith("\$")) {     
          Matcher matcher = pattern.matcher(word.getContent())
          return matcher.find()
        } else {
          return false
        }
      }
      $filteredSuggestions.setPredicate() { ShaleiaSuggestion suggestion ->
        return false
      }
    } catch (PatternSyntaxException exception) {
    }
    $shufflableWords.unshuffle()
  }

  protected Boolean checkWholeSuggestion(String search, String convertedSearch) {
    Setting setting = Setting.getInstance()
    Boolean ignoresAccent = setting.getIgnoresAccent()
    Boolean ignoresCase = setting.getIgnoresCase()
    if ($changes.containsKey(convertedSearch)) {
      for (String newName : $changes[convertedSearch]) {
        ShaleiaPossibility possibility = ShaleiaPossibility.new(newName, "変更前")
        $suggestions[0].getPossibilities().add(possibility)
        $suggestions[0].update()
      }
      return true
    }
    return false
  }

  public void searchDetail(ShaleiaSearchParameter parameter) {
    String searchName = parameter.getName()
    SearchType nameSearchType = parameter.getNameSearchType()
    String searchEquivalent = parameter.getEquivalent()
    SearchType equivalentSearchType = parameter.getEquivalentSearchType()
    String searchData = parameter.getData()
    SearchType dataSearchType = parameter.getDataSearchType()
    $filteredWords.setPredicate() { ShaleiaWord word ->
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
    $filteredSuggestions.setPredicate() { Suggestion suggestion ->
      return false
    }
    $shufflableWords.unshuffle()
  }

  public void modifyWord(ShaleiaWord oldWord, ShaleiaWord newWord) {
    newWord.createComparisonString($alphabetOrder)
    newWord.createContentPane()
    $isChanged = true
  }

  public void addWord(ShaleiaWord word) {
    word.setDictionary(this)
    word.createComparisonString($alphabetOrder)
    $words.add(word)
    $isChanged = true
  }

  public void removeWord(ShaleiaWord word) {
    $words.remove(word)
    $isChanged = true
  }

  public void updateOthers() {
    createChanges()
    calculateSystemWordSize()
  }

  private void createChanges() {
    Setting setting = Setting.getInstance()
    Boolean ignoresAccent = setting.getIgnoresAccent()
    Boolean ignoresCase = setting.getIgnoresCase()
    BufferedReader reader = BufferedReader.new(StringReader.new($changeData))
    String line
    $changes.clear()
    while ((line = reader.readLine()) != null) {
      Matcher matcher = line =~ /^\-\s*(\d+)\s*:\s*\{(.+)\}\s*→\s*\{(.+)\}/
      if (matcher.matches()) {
        String oldName = Strings.convert(matcher.group(2), ignoresAccent, ignoresCase)
        if (!$changes.containsKey(oldName)) {
          $changes[oldName] = ArrayList.new()
        }
        $changes[oldName].add(matcher.group(3))
      }
    }
    reader.close()
  }

  private void calculateSystemWordSize() {
    $systemWordSize = (Integer)$words.count{word -> word.getUniqueName().startsWith("\$")}
  }

  public ShaleiaWord emptyWord(String defaultName) {
    Long hairiaNumber = LocalDateTime.of(2012, 1, 23, 6, 0).until(LocalDateTime.now(), ChronoUnit.DAYS) + 1
    String data = "+ ${hairiaNumber} 〈不〉\n\n=〈〉"
    if (defaultName != null) {
      return ShaleiaWord.new(defaultName, data)
    } else {
      return ShaleiaWord.new("", data)
    }
  }

  public ShaleiaWord copiedWord(ShaleiaWord oldWord) {
    String name = oldWord.getName()
    String data = oldWord.getData()
    ShaleiaWord newWord = ShaleiaWord.new(name, data)
    return newWord
  }

  public ShaleiaWord inheritedWord(ShaleiaWord oldWord) {
    Long hairiaNumber = LocalDateTime.of(2012, 1, 23, 6, 0).until(LocalDateTime.now(), ChronoUnit.DAYS) + 1
    String name = oldWord.getName()
    String data = oldWord.getData().replaceAll(/^\+\s*(\d+)/, "+ ${hairiaNumber}")
    ShaleiaWord newWord = ShaleiaWord.new(name, data)
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

  public String getAlphabetOrder() {
    return $alphabetOrder
  }

  public void setAlphabetOrder(String alphabetOrder) {
    $alphabetOrder = alphabetOrder
    $isChanged = true
  }

  public String getChangeData() {
    return $changeData
  }

  public void setChangeData(String changeData) {
    $changeData = changeData
    $isChanged = true
    createChanges()
  }

  public Consumer<String> getOnLinkClicked() {
    return $onLinkClicked
  }

  public void setOnLinkClicked(Consumer<String> onLinkClicked) {
    $onLinkClicked = onLinkClicked
  }

}