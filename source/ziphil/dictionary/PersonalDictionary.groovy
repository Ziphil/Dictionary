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
    Boolean ignoresAccent = Setting.getInstance().ignoresAccent()
    try {
      Pattern pattern = Pattern.compile(search)
      $filteredWords.setPredicate() { PersonalWord word ->
        String name = (ignoresAccent) ? Strings.unaccent(word.getName()) : word.getName()
        if (isStrict) {
          return name.startsWith(search)
        } else {
          Matcher matcher = pattern.matcher(name)
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
    if ($path != null) {
      File file = File.new($path)
      String input = file.getText()
      Matcher matcher = input =~ /(?s)"(.*?)","(.*?)","(.*?)",(\d*?),(\d*?),(\d*?),"(.*?)"/
      matcher.each() { List<String> matches ->
        PersonalWord word = PersonalWord.new(matches[1], matches[7], matches[2], matches[3], matches[4].toInteger(), matches[5].toInteger(), matches[6].toInteger())
        $words.add(word)
      }
    }
  }

  public void save() {
    File file = File.new($path)
    StringBuilder output = StringBuilder.new()
    output.append("word,trans,exp,level,memory,modify,pron,filelink\n")
    $words.each() { PersonalWord word ->
      output.append("\"" + word.getName() + "\",")
      output.append("\"" + word.getTranslation() + "\",")
      output.append("\"" + word.getUsage() + "\",")
      output.append(word.getLevel().toString() + ",")
      output.append(word.getMemory().toString() + ",")
      output.append(word.getModification().toString() + ",")
      output.append("\"" + word.getPronunciation() + "\"\n")
    }
    file.setText(output.toString(), "UTF-8")
  }

  private void setupWords() {
    $filteredWords = FilteredList.new($words)
    $sortedWords = SortedList.new($filteredWords)
    $sortedWords.setComparator() { PersonalWord firstWord, PersonalWord secondWord ->
      return firstWord.getName() <=> secondWord.getName()
    }
  }

  public Boolean supportsEquivalent() {
    return false
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
    return DictionaryType.PERSONAL
  }

  public ObservableList<? extends Word> getWords() {
    return $sortedWords
  }

  public ObservableList<? extends Word> getRawWords() {
    return $words
  }

}