package ziphil.dictionary.personal

import groovy.transform.CompileStatic
import java.util.regex.Matcher
import javafx.collections.ObservableList
import ziphil.dictionary.DictionaryLoader
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class PersonalDictionaryLoader extends DictionaryLoader<PersonalDictionary, PersonalWord> {

  public PersonalDictionaryLoader(PersonalDictionary dictionary, String path) {
    super(dictionary, path)
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