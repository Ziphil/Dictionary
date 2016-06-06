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
public class PersonalDictionary extends Dictionary {

  private String $name = ""
  private String $path = ""
  private ObservableList<PersonalWord> $words = FXCollections.observableArrayList()
  private FilteredList<PersonalWord> $filteredWords
  private SortedList<PersonalWord> $sortedWords

  public PersonalDictionary(String name, String path) {
    $name = name
    $path = path
    load()
    setupWords()
  }

  public void searchByName(String search, Boolean isStrict) {
    try {
      Pattern pattern = Pattern.compile(search)
      $filteredWords.setPredicate() { PersonalWord word ->
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
  }

  public void searchByContent(String search) {
    try {
      Pattern pattern = Pattern.compile(search)
      $filteredWords.setPredicate() { PersonalWord word ->
        Matcher matcher = pattern.matcher(word.getContent())
        return matcher.find()
      }
    } catch (PatternSyntaxException exception) {
    }
  }

  private void load() {
    File file = File.new($path)
    String data = file.getText()
    Matcher matcher = data =~ /(?s)"(.*?)","(.*?)","(.*?)",(.*?),(.*?),(.*?),"(.*?)"/
    matcher.each() { List<String> matches ->
      PersonalWord word = PersonalWord.new(matches[1], matches[7], matches[2], matches[3], matches[4].toInteger(), matches[5].toInteger(), matches[6].toInteger())
      $words.add(word)
    }
  }

  public void save() {
  }

  private void setupWords() {
    $filteredWords = FilteredList.new($words)
    $sortedWords = SortedList.new($filteredWords)
    $sortedWords.setComparator() { PersonalWord firstWord, PersonalWord secondWord ->
      return firstWord.getName() <=> secondWord.getName()
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