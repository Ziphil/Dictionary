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

  protected Boolean call() {
    if ($path != null) {
      File file = File.new($path)
      BufferedWriter writer = file.newWriter("UTF-8")
      try {
        List<ShaleiaWord> sortedWord = $dictionary.getRawWords().toSorted((Comparator<ShaleiaWord>)$comparator)
        for (ShaleiaWord word : sortedWord) {
          writer.write("* ")
          writer.write(word.getUniqueName())
          writer.write("\n")
          writer.write(word.getData().trim())
          writer.write("\n\n")
        }
        writer.write("* META-ALPHABET-ORDER\n\n")
        writer.write("- ")
        writer.write($dictionary.getAlphabetOrder())
        writer.write("\n\n")
        writer.write("* META-CHANGE\n\n")
        writer.write($dictionary.getChangeData().trim())
        writer.write("\n\n")
      } finally {
        println("closed")
      }
    }
    return true
  }

  public void setComparator(Comparator<? super ShaleiaWord> comparator) {
    $comparator = comparator
  }

}