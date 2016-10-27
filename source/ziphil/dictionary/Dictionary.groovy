package ziphil.dictionary

import groovy.transform.CompileStatic
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
import ziphil.module.Setting
import ziphil.module.Strings


@CompileStatic @Newify
public abstract class Dictionary<W extends Word, S extends Suggestion> {

  protected String $name = ""
  protected String $path = ""
  protected ObservableList<W> $words = FXCollections.observableArrayList()
  protected FilteredList<W> $filteredWords
  protected SortedList<W> $sortedWords
  protected ObservableList<S> $suggestions = FXCollections.observableArrayList()
  protected FilteredList<S> $filteredSuggestions
  protected SortedList<S> $sortedSuggestions
  private ObservableList<? extends Word> $wholeWords = FXCollections.observableArrayList()

  public Dictionary(String name, String path) {
    $name = name
    $path = path
    setupSortedWords()
    setupWholeWords()
  }

  public void searchByName(String search, Boolean isStrict) {
    Setting setting = Setting.getInstance()
    Boolean ignoresAccent = setting.getIgnoresAccent()
    Boolean ignoresCase = setting.getIgnoresCase()
    Boolean prefixSearch = setting.getPrefixSearch()
    Boolean existsSuggestion = false
    try {
      Pattern pattern = Pattern.compile(search)
      $suggestions.each() { S suggestion ->
        suggestion.getPossibilities().clear()
      }
      $filteredWords.setPredicate() { W word ->
        if (isStrict) {
          String newName = word.getName()
          String newSearch = search
          if (ignoresAccent) {
            newName = Strings.unaccent(newName)
            newSearch = Strings.unaccent(newSearch)
          }
          if (ignoresCase) {
            newName = Strings.toLowerCase(newName)
            newSearch = Strings.toLowerCase(newSearch)
          }
          if (checkSuggestion(word, search)) {
            existsSuggestion = true
          }
          if (search != "") {
            if (prefixSearch) {
              return newName.startsWith(newSearch)
            } else {
              return newName == newSearch
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
  }

  public void searchByEquivalent(String search, Boolean isStrict) {
    Setting setting = Setting.getInstance()
    Boolean prefixSearch = setting.getPrefixSearch()
    try {
      Pattern pattern = Pattern.compile(search)
      $filteredWords.setPredicate() { W word ->
        if (isStrict) {
          if (search != "") {
            return word.getEquivalents().any() { String equivalent ->
              if (prefixSearch) {
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
  }

  protected Boolean checkSuggestion(W word, String search) {
    return false
  }

  public abstract void modifyWord(W oldWord, W newWord)

  public abstract void addWord(W word)

  public abstract void removeWord(W word)

  public abstract W emptyWord()

  public abstract W copiedWord(W oldWord)

  public abstract W inheritedWord(W oldWord)

  public abstract void save()

  private void setupSortedWords() {
    $filteredWords = FilteredList.new($words)
    $sortedWords = SortedList.new($filteredWords)
    $filteredSuggestions = FilteredList.new($suggestions){suggestion -> false}
    $sortedSuggestions = SortedList.new($filteredSuggestions)
  }

  private void setupWholeWords() {
    ListChangeListener<?> listener = (ListChangeListener){ Change<?> change ->
      $wholeWords.clear()
      $wholeWords.addAll($sortedSuggestions)
      $wholeWords.addAll($sortedWords)
    }
    $filteredWords.addListener(listener)
    $filteredSuggestions.addListener(listener)
  }

  public static Dictionary loadDictionary(File file) {
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

  public ObservableList<? extends Word> getWholeWords() {
    return $wholeWords
  }

  public ObservableList<W> getWords() {
    return $sortedWords
  }

  public ObservableList<W> getRawWords() {
    return $words
  }

  public abstract Task<?> getLoader()

}