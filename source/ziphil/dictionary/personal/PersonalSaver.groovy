package ziphil.dictionary.personal

import groovy.transform.CompileStatic
import java.util.regex.Matcher
import javafx.collections.ObservableList
import ziphil.dictionary.Saver
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class PersonalSaver extends Saver<PersonalDictionary> {

  public PersonalSaver() {
    super()
  }

  protected BooleanClass save() {
    File file = File.new($path)
    BufferedWriter writer = file.newWriter("UTF-8")
    Int size = $dictionary.getRawWords().size()
    try {
      writer.write("word,trans,exp,level,memory,modify,pron,filelink")
      writer.newLine()
      for (Int i = 0 ; i < size ; i ++) {
        PersonalWord word = $dictionary.getRawWords()[i]
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
        updateProgress(i + 1, size)
      }
    } finally {
      writer.close()
    }
    return true
  }

}