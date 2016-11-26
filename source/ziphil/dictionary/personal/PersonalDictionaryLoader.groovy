package ziphil.dictionary.personal

import groovy.transform.CompileStatic
import java.util.regex.Matcher
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.concurrent.Task
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class PersonalDictionaryLoader extends Task<ObservableList<PersonalWord>> {

  private ObservableList<PersonalWord> $words = FXCollections.observableArrayList()
  private String $path

  public PersonalDictionaryLoader(String path) {
    $path = path
  }

  protected ObservableList<PersonalWord> call() {
    if ($path != null) {
      File file = File.new($path)
      String input = file.getText()
      Matcher matcher = input =~ /(?s)"(.*?)","(.*?)","(.*?)",(\d*?),(\d*?),(\d*?),"(.*?)"/
      while (matcher.find()) {
        if (isCancelled()) {
          return null
        }
        String name = matcher.group(1)
        String pronunciation = matcher.group(7)
        String translation = matcher.group(2)
        String usage = matcher.group(3)
        Integer level = matcher.group(4).toInteger()
        Integer memory = matcher.group(5).toInteger()
        Integer modification = matcher.group(6).toInteger()
        PersonalWord word = PersonalWord.new(name, pronunciation, translation, usage, level, memory, modification)
        $words.add(word)
      }
    }
    return $words
  }

}