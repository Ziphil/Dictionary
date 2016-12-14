package ziphil.dictionary.shaleia

import groovy.transform.CompileStatic
import java.util.regex.Matcher
import javafx.collections.ObservableList
import ziphil.dictionary.DictionaryLoader
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class ShaleiaDictionaryLoader extends DictionaryLoader<ShaleiaDictionary, ShaleiaWord> {

  public ShaleiaDictionaryLoader(ShaleiaDictionary dictionary, String path) {
    super(dictionary, path)
  }

  protected ObservableList<ShaleiaWord> call() {
    if ($path != null) {
      File file = File.new($path)
      BufferedReader reader = file.newReader("UTF-8")
      String currentName = null
      StringBuilder currentData = StringBuilder.new()
      String line
      while ((line = reader.readLine()) != null) {
        if (isCancelled()) {
          reader.close()
          return null
        }
        Matcher matcher = line =~ /^\*\s*(.+)\s*$/
        if (matcher.matches()) {
          add(currentName, currentData)
          currentName = matcher.group(1)
          currentData.setLength(0)
        } else {
          currentData.append(line)
          currentData.append("\n")
        }
      }
      add(currentName, currentData)
      reader.close()
    }
    for (ShaleiaWord word : $words) {
      word.createComparisonString($dictionary.getAlphabetOrder())
    }
    return $words
  }

  private void add(String currentName, StringBuilder currentData) {
    if (currentName != null) {
      if (currentName == "META-ALPHABET-ORDER") {
        addAlphabetOrder(currentData)
      } else if (currentName == "META-CHANGE") {
        addChangeData(currentData)
      } else {
        addWord(currentName, currentData)
      }
    }
  }

  private void addWord(String currentName, StringBuilder currentData) {
    ShaleiaWord word = ShaleiaWord.new()
    word.setUniqueName(currentName)
    word.setData(currentData.toString())
    word.setDictionary($dictionary)
    word.update()
    $words.add(word)
  }

  private void addAlphabetOrder(StringBuilder currentData) {
    String alphabetOrder = currentData.toString().trim().replaceAll(/^\-\s*/, "")
    $dictionary.setAlphabetOrder(alphabetOrder)
  }

  private void addChangeData(StringBuilder currentData) {
    String changeData = currentData.toString().replaceAll(/^\s*\n/, "")
    $dictionary.setChangeData(changeData)
  }

}