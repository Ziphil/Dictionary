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
public class ShaleiaDictionary extends Dictionary {

  private String $name = ""
  private String $path = ""
  private ObservableList<ShaleiaWord> $words = FXCollections.observableArrayList()
  private FilteredList<ShaleiaWord> $filteredWords
  private SortedList<ShaleiaWord> $sortedWords

  public ShaleiaDictionary(String name, String path) {
    $name = name
    $path = path
    load()
    setupWords()
  }

  public void searchByName(String search, Boolean isStrict) {
    Boolean ignoresAccent = Setting.getInstance().ignoresAccent()
    try {
      Pattern pattern = Pattern.compile(search)
      $filteredWords.setPredicate() { ShaleiaWord word ->
        if (isStrict) {
          String name = (ignoresAccent) ? Strings.unaccent(word.getName()) : word.getName()
          return name.startsWith(search)
        } else {
          Matcher matcher = pattern.matcher(word.getName())
          return matcher.find()
        }
      }
    } catch (PatternSyntaxException exception) {
    }
  }

  public void searchByEquivalent(String search, Boolean isStrict) {
    try {
      Pattern pattern = Pattern.compile(search)
      $filteredWords.setPredicate() { ShaleiaWord word ->
        if (isStrict) {
          return word.getEquivalents().any() { String equivalent ->
            return equivalent.startsWith(search)
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
      $filteredWords.setPredicate() { ShaleiaWord word ->
        Matcher matcher = pattern.matcher(word.getContent())
        return matcher.find()
      }
    } catch (PatternSyntaxException exception) {
    }
  }

  private void load() {
    if ($path != null) {
      File file = File.new($path)
      String currentName = null
      StringBuilder currentData = StringBuilder.new()
      file.eachLine() { String line ->
        Matcher matcher = line =~ /^\*\s*(.+)\s*$/
        if (matcher.matches()) {
          if (currentName != null) {
            ShaleiaWord word = ShaleiaWord.new(currentName, currentData.toString())
            $words.add(word)
          }
          currentName = matcher.group(1)
          currentData.setLength(0)
        } else {
          currentData.append(line)
          currentData.append("\n")
        }
      }
      if (currentName != null) {
        ShaleiaWord word = ShaleiaWord.new(currentName, currentData.toString())
        $words.add(word)
      }
    }
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
  }

  private void setupWords() {
    $filteredWords = FilteredList.new($words)
    $sortedWords = SortedList.new($filteredWords)
    $sortedWords.setComparator() { ShaleiaWord firstWord, ShaleiaWord secondWord ->
      List<Integer> firstList = firstWord.listForComparison()
      List<Integer> secondList = secondWord.listForComparison()
      Integer result = null
      (0 ..< firstList.size()).each() { Integer i ->
        Integer firstData = firstList[i]
        Integer secondData = secondList[i]
        if (result == null && firstData <=> secondData != 0) {
          result = firstData <=> secondData
        }
      }
      return (result == null) ? -1 : result
    }
  }

  public Boolean supportsEquivalent() {
    return true
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

  public DictionaryType getType() {
    return DictionaryType.SHALEIA
  }

  public ObservableList<? extends Word> getWords() {
    return $sortedWords
  }

  public ObservableList<? extends Word> getRawWords() {
    return $words
  }

}