package ziphil.dictionary

import groovy.transform.CompileStatic
import java.util.regex.Matcher
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.concurrent.Task


@CompileStatic @Newify
public class PersonalDictionaryLoader extends Task<ObservableList<PersonalWord>> {

  private String $path

  public PersonalDictionaryLoader(String path) {
    $path = path
  }

  protected ObservableList<PersonalWord> call() {
    ObservableList<PersonalWord> words = FXCollections.observableArrayList()
    if ($path != null) {
      File file = File.new($path)
      String input = file.getText()
      Matcher matcher = input =~ /(?s)"(.*?)","(.*?)","(.*?)",(\d*?),(\d*?),(\d*?),"(.*?)"/
      matcher.each() { List<String> matches ->
        PersonalWord word = PersonalWord.new(matches[1], matches[7], matches[2], matches[3], matches[4].toInteger(), matches[5].toInteger(), matches[6].toInteger())
        words.add(word)
      }
    }
    return words
  }

}