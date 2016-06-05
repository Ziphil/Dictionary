package ziphil.dictionary

import groovy.transform.CompileStatic
import java.util.regex.Matcher
import java.util.regex.Pattern
import java.util.regex.PatternSyntaxException
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.collections.transformation.FilteredList
import javafx.collections.transformation.SortedList


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
    try {
      Pattern pattern = Pattern.compile(search)
      $filteredWords.setPredicate() { ShaleiaWord word ->
        if (isStrict) {
          return word.getName().startsWith(search)
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
    File file = File.new($path)
    StringBuilder currentContent = StringBuilder.new()
    file.eachLine() { String line ->
      Matcher matcher = line =~ /^\*\s*(.+)\s*$/
      if (matcher.matches()) {
        if (currentContent.length() > 0) {
          ShaleiaWord word = ShaleiaWord.new(currentContent.toString())
          $words.add(word)
        }
        currentContent.setLength(0)
      }
      currentContent.append(line)
      currentContent.append("\n")
    }
    if (currentContent.length() > 0) {
      ShaleiaWord word = ShaleiaWord.new(currentContent.toString())
      $words.add(word)
    }
  }

  public void save() {
    File file = File.new($path)
    StringBuilder wholeData = StringBuilder.new()
    $words.each() { ShaleiaWord word ->
      String data = word.getData().trim() + "\n\n"
      wholeData.append(data)
    }
    file.setText(wholeData.toString(), "UTF-8")
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

  public String getName() {
    return $name
  }

  public ObservableList<? extends Word> getWords() {
    return $sortedWords
  }

  public ObservableList<? extends Word> getRawWords() {
    return $words
  }

}