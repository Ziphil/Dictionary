package ziphil.dictionary

import groovy.transform.CompileStatic
import groovy.lang.Binding as GroovyBinding
import groovy.lang.GroovyShell
import groovy.lang.Script
import java.util.concurrent.Callable
import java.util.regex.Matcher
import java.util.regex.Pattern
import java.util.regex.PatternSyntaxException
import javafx.collections.FXCollections
import javafx.collections.ListChangeListener
import javafx.collections.ListChangeListener.Change
import javafx.collections.ObservableList
import javafx.collections.transformation.FilteredList
import javafx.collections.transformation.SortedList
import javafx.concurrent.Task
import javafx.concurrent.WorkerStateEvent
import ziphil.custom.ShufflableList
import ziphil.module.Setting
import ziphil.module.Strings
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public abstract class DictionaryBase<W extends Word, S extends Suggestion, P extends DetailSearchParameter> implements Dictionary<W> {

  protected String $name = ""
  protected String $path = ""
  protected ObservableList<W> $words = FXCollections.observableArrayList()
  protected FilteredList<W> $filteredWords
  protected SortedList<W> $sortedWords
  protected ShufflableList<W> $shufflableWords
  protected ObservableList<S> $suggestions = FXCollections.observableArrayList()
  protected FilteredList<S> $filteredSuggestions
  protected SortedList<S> $sortedSuggestions
  private ObservableList<Element> $wholeWords = FXCollections.observableArrayList()
  private Task<?> $loader
  private Task<?> $saver
  protected Boolean $isChanged = false
  protected Boolean $isFirstEmpty = false

  public DictionaryBase(String name, String path) {
    $name = name
    $path = path
    $isChanged = (path == null) ? true : false
    $isFirstEmpty = path == null
    setupSortedWords()
    setupWholeWords()
  }

  public void searchByName(String search, Boolean isStrict) {
    Setting setting = Setting.getInstance()
    Boolean ignoresAccent = setting.getIgnoresAccent()
    Boolean ignoresCase = setting.getIgnoresCase()
    try {
      Pattern pattern = (isStrict) ? null : Pattern.compile(search)
      String convertedSearch = Strings.convert(search, ignoresAccent, ignoresCase)
      resetSuggestions()
      checkWholeSuggestion(search, convertedSearch)
      $filteredWords.setPredicate() { W word ->
        if (isStrict) {
          checkSuggestion(word, search, convertedSearch)
        }
        return word.isDisplayed() && doSearchByName(word, search, convertedSearch, pattern, isStrict)
      }
      $filteredSuggestions.setPredicate() { S suggestion ->
        return suggestion.isDisplayed()
      }
    } catch (PatternSyntaxException exception) {
    }
    $shufflableWords.unshuffle()
  }

  public void searchByEquivalent(String search, Boolean isStrict) {
    try {
      Pattern pattern = Pattern.compile(search)
      $filteredWords.setPredicate() { W word ->
        return word.isDisplayed() && doSearchByEquivalent(word, search, pattern, isStrict)
      }
      $filteredSuggestions.setPredicate() { S suggestion ->
        return false
      }
    } catch (PatternSyntaxException exception) {
    }
    $shufflableWords.unshuffle()
  }

  public void searchByContent(String search) {
    try {
      Pattern pattern = Pattern.compile(search)
      $filteredWords.setPredicate() { W word ->
        return word.isDisplayed() && doSearchByContent(word, search, pattern)
      }
      $filteredSuggestions.setPredicate() { S suggestion ->
        return false
      }
    } catch (PatternSyntaxException exception) {
    }
    $shufflableWords.unshuffle()
  }

  public void searchScript(String script) {
    GroovyShell shell = GroovyShell.new()
    Script parsedScript = shell.parse(script)
    $filteredWords.setPredicate() { W word ->
      return word.isDisplayed() && doSearchScript(word, parsedScript)
    }
    $filteredSuggestions.setPredicate() { S suggestion ->
      return false
    }
    $shufflableWords.unshuffle()
  }

  public void searchDetail(P parameter) {
    $filteredWords.setPredicate() { W word ->
      return word.isDisplayed() && doSearchDetail(word, parameter)
    }
    $filteredSuggestions.setPredicate() { S suggestion ->
      return false
    }
    $shufflableWords.unshuffle()
  }

  protected Boolean doSearchByName(W word, String search, String convertedSearch, Pattern pattern, Boolean isStrict) {
    Setting setting = Setting.getInstance()
    Boolean ignoresAccent = setting.getIgnoresAccent()
    Boolean ignoresCase = setting.getIgnoresCase()
    Boolean searchesPrefix = setting.getSearchesPrefix()
    if (isStrict) {
      String name = word.getName()
      String convertedName = Strings.convert(name, ignoresAccent, ignoresCase)
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
      Matcher matcher = pattern.matcher(word.getName())
      return matcher.find()
    }
  }

  protected Boolean doSearchByEquivalent(W word, String search, Pattern pattern, Boolean isStrict) {
    Setting setting = Setting.getInstance()
    Boolean ignoresAccent = setting.getIgnoresAccent()
    Boolean ignoresCase = setting.getIgnoresCase()
    Boolean searchesPrefix = setting.getSearchesPrefix()
    if (isStrict) {
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
      return word.getEquivalents().any() { String equivalent ->
        Matcher matcher = pattern.matcher(equivalent)
        return matcher.find()
      }
    }
  }

  protected Boolean doSearchByContent(W word, String search, Pattern pattern) {
    Matcher matcher = pattern.matcher(word.getContent())
    return matcher.find()
  }

  protected Boolean doSearchScript(W word, Script parsedScript) {
    try {
      GroovyBinding binding = GroovyBinding.new()
      binding.setVariable("word", word)
      parsedScript.setBinding(binding)
      Object result = parsedScript.run()
      if (result) {
        return true
      } else {
        return false
      }
    } catch (Exception exception) {
      return false
    }
  }

  protected Boolean doSearchDetail(W word, P parameter) {
    return false
  }

  private void resetSuggestions() {
    for (S suggestion : $suggestions) {
      suggestion.getPossibilities().clear()
      suggestion.setDisplayed(false)
    }
  }

  protected void checkWholeSuggestion(String search, String convertedSearch) {
  }

  protected void checkSuggestion(W word, String search, String convertedSearch) {
  }

  public void shuffleWords() {
    $shufflableWords.shuffle()
  }

  public void modifyWord(W oldWord, W newWord) {
    $isChanged = true
  }

  public void addWord(W word) {
    $words.add(word)
    $isChanged = true
  }

  public void removeWord(W word) {
    $words.remove(word)
    $isChanged = true
  }

  public abstract void update()

  public abstract void updateMinimum()

  public abstract W emptyWord(String defaultName)

  public abstract W copiedWord(W oldWord)

  public abstract W inheritedWord(W oldWord)

  protected void load() {
    $loader = createLoader()
    $loader.addEventFilter(WorkerStateEvent.WORKER_STATE_SUCCEEDED) { WorkerStateEvent event ->
      if (!$isFirstEmpty) {
        $isChanged = false
      }
    }
    Thread thread = Thread.new($loader)
    thread.setDaemon(true)
    thread.start()
  }

  public void save() {
    $saver = createSaver()
    $saver.run()
    if ($path != null) {
      $isChanged = false
    }
  }

  private void setupSortedWords() {
    $filteredWords = FilteredList.new($words)
    $sortedWords = SortedList.new($filteredWords)
    $shufflableWords = ShufflableList.new($sortedWords)
    $filteredSuggestions = FilteredList.new($suggestions){suggestion -> false}
    $sortedSuggestions = SortedList.new($filteredSuggestions)
  }

  private void setupWholeWords() {
    ListChangeListener<?> listener = (ListChangeListener){ Change<?> change ->
      $wholeWords.clear()
      $wholeWords.addAll($sortedSuggestions)
      $wholeWords.addAll($shufflableWords)
    }
    $filteredWords.addListener(listener)
    $filteredSuggestions.addListener(listener)
    $shufflableWords.addListener(listener)
  }

  public Integer hitSize() {
    return $shufflableWords.size()
  }

  public Integer totalSize() {
    return $words.size()
  }

  protected abstract Task<?> createLoader()

  protected abstract Task<?> createSaver()

  public String getName() {
    return $name
  }

  public void setName(String name) {
    $name = name
  }

  public String getPath() {
    return $path
  }

  public void setPath(String path) {
    $path = path
  }

  public ObservableList<Element> getWholeWords() {
    return $wholeWords
  }

  public ObservableList<W> getWords() {
    return $shufflableWords
  }

  public ObservableList<W> getRawWords() {
    return $words
  }

  public Task<?> getLoader() {
    return $loader
  }

  public Task<?> getSaver() {
    return $saver
  }

  public abstract String getExtension()

  public Boolean isChanged() {
    return $isChanged
  }

}