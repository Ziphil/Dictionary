package ziphil.dictionary

import groovy.transform.CompileStatic
import java.util.regex.Matcher
import java.util.regex.Pattern
import java.util.regex.PatternSyntaxException
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.collections.transformation.FilteredList
import javafx.collections.transformation.SortedList
import ziphil.module.Setting
import ziphil.module.Strings


@CompileStatic @Newify
public abstract class Dictionary<W extends Word> {

  protected String $name = ""
  protected String $path = ""
  protected ObservableList<W> $words = FXCollections.observableArrayList()
  protected FilteredList<W> $filteredWords
  protected SortedList<W> $sortedWords

  public Dictionary(String name, String path) {
    $name = name
    $path = path
    setupSortedWords()
  }

  public void searchByName(String search, Boolean isStrict) {
    Setting setting = Setting.getInstance()
    Boolean ignoresAccent = setting.getIgnoresAccent()
    Boolean ignoresCase = setting.getIgnoresCase()
    Boolean prefixSearch = setting.getPrefixSearch()
    try {
      Pattern pattern = Pattern.compile(search)
      $filteredWords.setPredicate() { Word word ->
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
    } catch (PatternSyntaxException exception) {
    }
  }

  public void searchByEquivalent(String search, Boolean isStrict) {
    Setting setting = Setting.getInstance()
    Boolean prefixSearch = setting.getPrefixSearch()
    try {
      Pattern pattern = Pattern.compile(search)
      $filteredWords.setPredicate() { Word word ->
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
    } catch (PatternSyntaxException exception) {
    }
  }

  public void searchByContent(String search) {
    try {
      Pattern pattern = Pattern.compile(search)
      $filteredWords.setPredicate() { Word word ->
        Matcher matcher = pattern.matcher(word.getContent())
        return matcher.find()
      }
    } catch (PatternSyntaxException exception) {
    }
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
  }

  public abstract Boolean supportsEquivalent()

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

  public ObservableList<W> getWords() {
    return $sortedWords
  }

  public ObservableList<W> getRawWords() {
    return $words
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

}