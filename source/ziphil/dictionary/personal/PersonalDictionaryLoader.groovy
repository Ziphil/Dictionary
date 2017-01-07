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
      String input = file.getText().replaceAll(/\r/, "")
      Matcher matcher = input =~ /(?s)"(.*?)","(.*?)","(.*?)",(\d*?),(\d*?),(\d*?),"(.*?)"/
      while (matcher.find()) {
        if (isCancelled()) {
          return null
        }
        PersonalWord word = PersonalWord.new()
        word.setName(matcher.group(1))
        word.setPronunciation(matcher.group(7))
        word.setTranslation(matcher.group(2))
        word.setUsage(matcher.group(3))
        word.setLevel(matcher.group(4).toInteger())
        word.setMemory(matcher.group(5).toInteger())
        word.setModification(matcher.group(6).toInteger())
        word.update()
        $words.add(word)
      }
    }
    return $words
  }

}