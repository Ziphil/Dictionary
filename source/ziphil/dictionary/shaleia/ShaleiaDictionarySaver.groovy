package ziphil.dictionary.shaleia

import groovy.transform.CompileStatic
import java.util.regex.Matcher
import javafx.collections.ObservableList
import ziphil.dictionary.DictionarySaver
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class ShaleiaDictionarySaver extends DictionarySaver<ShaleiaDictionary> {

  private Comparator<? super ShaleiaWord> $comparator

  public ShaleiaDictionarySaver(ShaleiaDictionary dictionary, String path) {
    super(dictionary, path)
  }

  protected BooleanClass save() {
    File file = File.new($path)
    BufferedWriter writer = file.newWriter("UTF-8")
    try {
      List<ShaleiaWord> sortedWord = $dictionary.getRawWords().toSorted((Comparator<ShaleiaWord>)$comparator)
      for (ShaleiaWord word : sortedWord) {
        writer.write("* ")
        writer.write(word.getUniqueName())
        writer.newLine()
        writer.write(word.getDescription().trim())
        writer.newLine()
        writer.newLine()
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

  public void setComparator(Comparator<? super ShaleiaWord> comparator) {
    $comparator = comparator
  }

}