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

  protected Boolean save() {
    File file = File.new($path)
    BufferedWriter writer = file.newWriter("UTF-8")
    try {
      writer.write("word,trans,exp,level,memory,modify,pron,filelink")
      writer.newLine()
      for (PersonalWord word : $dictionary.getRawWords()) {
        writer.write("\"")
        writer.write(word.getName().replaceAll(/"/, "\"\""))
        writer.write("\",\"")
        writer.write(word.getTranslation().replaceAll(/"/, "\"\""))
        writer.write("\",\"")
        writer.write(word.getUsage().replaceAll(/"/, "\"\""))
        writer.write("\",")
        writer.write(word.getLevel().toString())
        writer.write(",")
        writer.write(word.getMemory().toString())
        writer.write(",")
        writer.write(word.getModification().toString())
        writer.write(",\"")
        writer.write(word.getPronunciation().replaceAll(/"/, "\"\""))
        writer.write("\"")
        writer.newLine()
      }
    } finally {
      writer.close()
    }
    return true
  }

}