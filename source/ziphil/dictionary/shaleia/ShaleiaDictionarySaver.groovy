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
      StringBuilder output = StringBuilder.new()
      List<ShaleiaWord> sortedWord = $dictionary.getRawWords().toSorted((Comparator<ShaleiaWord>)$comparator)
      for (ShaleiaWord word : sortedWord) {
        output.append("* ").append(word.getUniqueName()).append("\n")
        output.append(word.getData().trim()).append("\n\n")
      }
      output.append("* META-ALPHABET-ORDER\n\n")
      output.append("- ").append($dictionary.getAlphabetOrder()).append("\n\n")
      output.append("* META-CHANGE\n\n")
      output.append($dictionary.getChangeData().trim()).append("\n\n")
      file.setText(output.toString(), "UTF-8")
    }
    return true
  }

  public void setComparator(Comparator<? super ShaleiaWord> comparator) {
    $comparator = comparator
  }

}