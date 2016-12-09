package ziphil.dictionary.personal

import groovy.transform.CompileStatic
import java.util.regex.Matcher
import javafx.collections.ObservableList
import ziphil.dictionary.DictionarySaver
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class PersonalDictionarySaver extends DictionarySaver<PersonalDictionary> {

  public PersonalDictionarySaver(PersonalDictionary dictionary, String path) {
    super(dictionary, path)
  }

  protected Boolean call() {
    if ($path != null) {
      File file = File.new($path)
      StringBuilder output = StringBuilder.new()
      output.append("word,trans,exp,level,memory,modify,pron,filelink\n")
      for (PersonalWord word : $dictionary.getRawWords()) {
        output.append("\"").append(word.getName()).append("\",")
        output.append("\"").append(word.getTranslation()).append("\",")
        output.append("\"").append(word.getUsage()).append("\",")
        output.append(word.getLevel().toString()).append(",")
        output.append(word.getMemory().toString()).append(",")
        output.append(word.getModification().toString()).append(",")
        output.append("\"").append(word.getPronunciation()).append("\"\n")
      }
      file.setText(output.toString(), "UTF-8")
    }
    return true
  }

}