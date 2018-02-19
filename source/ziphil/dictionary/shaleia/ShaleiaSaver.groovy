package ziphil.dictionary.shaleia

import groovy.transform.CompileStatic
import java.util.regex.Matcher
import javafx.collections.ObservableList
import ziphil.dictionary.Saver
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class ShaleiaSaver extends Saver<ShaleiaDictionary> {

  public ShaleiaSaver() {
    super()
  }

  protected BooleanClass save() {
    File file = File.new($path)
    BufferedWriter writer = file.newWriter("UTF-8")
    List<ShaleiaWord> sortedWord = $dictionary.getRawSortedWords()
    Int size = sortedWord.size()
    try {
      for (Int i = 0 ; i < size ; i ++) {
        ShaleiaWord word = sortedWord[i]
        writer.write("* ")
        writer.write(word.getUniqueName())
        writer.newLine()
        writer.write(word.getDescription().trim())
        writer.newLine()
        writer.newLine()
        updateProgress(i + 1, size)
      }
      writer.write("* META-ALPHABET-ORDER")
      writer.newLine()
      writer.newLine()
      writer.write("- ")
      writer.write($dictionary.getAlphabetOrder())
      writer.newLine()
      writer.newLine()
      writer.write("* META-VERSION")
      writer.newLine()
      writer.newLine()
      writer.write("- ")
      writer.write($dictionary.getVersion())
      writer.newLine()
      writer.newLine()
      writer.write("* META-CHANGE")
      writer.newLine()
      writer.newLine()
      writer.write($dictionary.getChangeDescription().trim())
      writer.newLine()
      writer.newLine()
      if ($dictionary.getAkrantiainSource() != null) {
        writer.write("* META-SNOJ")
        writer.newLine()
        writer.newLine()
        writer.write($dictionary.getAkrantiainSource().trim())
        writer.newLine()
        writer.newLine()
      }
    } finally {
      writer.close()
    }
    return true
  }

}