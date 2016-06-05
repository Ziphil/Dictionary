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

  private static final String DATA_PATH = "C:/Apache Software Foundation/Apache2.2/htdocs/xk/page/nagili/raw_words.csv"

  private String $name = "凪霧"
  private ObservableList<PersonalWord> $words = FXCollections.observableArrayList()
  private FilteredList<PersonalWord> $filteredWords
  private SortedList<PersonalWord> $sortedWords

  public PersonalDictionary() {
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
    File file = File.new(DATA_PATH)
    String data = file.getText()
    Matcher matcher = data =~ /(?s)"(.*?)","(.*?)","(.*?)",(.*?),(.*?),(.*?),"(.*?)"/
    matcher.each() { List<String> matches ->
      PersonalWord word = PersonalWord.new(matches[1], matches[2], matches[3], matches[4].toInteger(), matches[5].toInteger(), matches[6].toInteger(), matches[7])
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