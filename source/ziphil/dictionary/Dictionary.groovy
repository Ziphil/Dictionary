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
import ziphil.dictionary.personal.PersonalDictionary
import ziphil.dictionary.shaleia.ShaleiaDictionary
import ziphil.dictionary.slime.SlimeDictionary
import ziphil.module.Setting
import ziphil.module.Strings
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public abstract class Dictionary<W extends Word, S extends Suggestion> {

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

  public Dictionary(String name, String path) {
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
    Boolean searchesPrefix = setting.getSearchesPrefix()
    Boolean existsSuggestion = false
    try {
      Pattern pattern = (isStrict) ? null : Pattern.compile(search)
      String convertedSearch = Strings.convert(search, ignoresAccent, ignoresCase)
      for (S suggestion : $suggestions) {
        suggestion.getPossibilities().clear()
      }
      if (checkWholeSuggestion(search, convertedSearch)) {
        existsSuggestion = true
      }
      $filteredWords.setPredicate() { W word ->
        if (isStrict) {
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
          Matcher matcher = pattern.matcher(word.getName())
          return matcher.find()
        }
      }
      $filteredSuggestions.setPredicate() { S suggestion ->
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
      $filteredWords.setPredicate() { W word ->
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
        Matcher matcher = pattern.matcher(word.getContent())
        return matcher.find()
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
    $filteredSuggestions.setPredicate() { S suggestion ->
      return false
    }
    $shufflableWords.unshuffle()
  }

  public void shuffleWords() {
    $shufflableWords.shuffle()
  }

  protected Boolean checkWholeSuggestion(String search, String convertedSearch) {
    return false
  }

  protected Boolean checkSuggestion(W word, String search, String convertedSearch) {
    return false
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

  public static Dictionary loadDictionary(File file) {
    if (file.exists() && file.isFile()) {
      Dictionary dictionary
      String fileName = file.getName()
      String filePath = file.getPath()
      if (filePath.endsWith(".xdc")) {
        dictionary = ShaleiaDictionary.new(fileName, filePath)
      } else if (filePath.endsWith(".csv")) {
        dictionary = PersonalDictionary.new(fileName, filePath)
      } else if (filePath.endsWith(".json")) {
        dictionary = SlimeDictionary.new(fileName, filePath)
      }
      return dictionary
    } else {
      return null
    }
  }

  public static Dictionary loadEmptyDictionary(File file) {
    Dictionary dictionary
    String fileName = file.getName()
    String filePath = file.getPath()
    if (filePath.endsWith(".xdc")) {
      dictionary = ShaleiaDictionary.new(fileName, null)
      dictionary.setPath(filePath)
    } else if (filePath.endsWith(".csv")) {
      dictionary = PersonalDictionary.new(fileName, null)
      dictionary.setPath(filePath)
    } else if (filePath.endsWith(".json")) {
      dictionary = SlimeDictionary.new(fileName, null)
      dictionary.setPath(filePath)
    }
    return dictionary
  }

  public static String extensionOf(Dictionary dictionary) {
    if (dictionary instanceof ShaleiaDictionary) {
      return "xdc"
    } else if (dictionary instanceof PersonalDictionary) {
      return "csv"
    } else if (dictionary instanceof SlimeDictionary) {
      return "json"
    } else {
      return null
    }
  }

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

  public Boolean isChanged() {
    return $isChanged
  }

}