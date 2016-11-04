package ziphil.dictionary.shaleia

import groovy.transform.CompileStatic
import java.time.Duration
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.util.function.Consumer
import java.util.regex.Matcher
import javafx.concurrent.Task
import javafx.concurrent.WorkerStateEvent
import ziphil.dictionary.Dictionary
import ziphil.dictionary.SearchType
import ziphil.dictionary.Suggestion


@CompileStatic @Newify
public class ShaleiaDictionary extends Dictionary<ShaleiaWord, Suggestion> {

  private ShaleiaDictionaryLoader $loader
  private String $alphabetOrder
  private Consumer<String> $onLinkClicked

  public ShaleiaDictionary(String name, String path) {
    super(name, path)
    load()
    setupWords()
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
        equivalents.each() { String equivalent ->
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

  public ShaleiaWord emptyWord() {
    Long hairiaNumber = LocalDateTime.of(2012, 1, 23, 6, 0).until(LocalDateTime.now(), ChronoUnit.DAYS) + 1
    String data = "+ ${hairiaNumber} 〈不〉\n\n=〈〉"
    return ShaleiaWord.new("", data)
  }

  public ShaleiaWord copiedWord(ShaleiaWord oldWord) {
    String name = oldWord.getName()
    String data = oldWord.getData()
    ShaleiaWord newWord = ShaleiaWord.new(name, data)
    return newWord
  }

  public ShaleiaWord inheritedWord(ShaleiaWord oldWord) {
    return copiedWord(oldWord)
  }

  private void load() {
    $loader = ShaleiaDictionaryLoader.new($path, this)
    $loader.addEventFilter(WorkerStateEvent.WORKER_STATE_SUCCEEDED) { WorkerStateEvent event ->
      $alphabetOrder = $loader.getAlphabetOrder()
      $words.addAll($loader.getValue())
    }
    Thread thread = Thread.new(loader)
    thread.setDaemon(true)
    thread.start()
  }

  public void save() {
    File file = File.new($path)
    StringBuilder output = StringBuilder.new()
    $words.each() { ShaleiaWord word ->
      output.append("* " + word.getUniqueName())
      output.append("\n")
      output.append(word.getData().trim())
      output.append("\n\n")
    }
    file.setText(output.toString(), "UTF-8")
    $isChanged = false
  }

  private void setupWords() {
    $sortedWords.setComparator() { ShaleiaWord firstWord, ShaleiaWord secondWord ->
      String firstString = firstWord.getComparisonString()
      String secondString = secondWord.getComparisonString()
      return firstString <=> secondString
    }
  }

  public Consumer<String> getOnLinkClicked() {
    return $onLinkClicked
  }

  public void setOnLinkClicked(Consumer<String> onLinkClicked) {
    $onLinkClicked = onLinkClicked
  }

  public Task<?> getLoader() {
    return $loader
  }

}