package ziphil.dictionary.shaleia

import groovy.transform.CompileStatic
import java.util.regex.Matcher
import javafx.collections.ObservableList
import ziphil.dictionary.DictionaryLoader
import ziphilib.transform.ConvertPrimitives
import ziphilib.transform.Ziphilify
import ziphilib.type.PrimLong


@CompileStatic @Ziphilify @ConvertPrimitives
public class ShaleiaDictionaryLoader extends DictionaryLoader<ShaleiaDictionary, ShaleiaWord> {

  public ShaleiaDictionaryLoader(ShaleiaDictionary dictionary, String path) {
    super(dictionary, path)
    updateProgress(0, 1)
  }

  protected ObservableList<ShaleiaWord> call() {
    if ($path != null) {
      File file = File.new($path)
      BufferedReader reader = file.newReader("UTF-8")
      PrimLong size = file.length()
      PrimLong offset = 0L
      try {
        String currentName = null
        StringBuilder currentData = StringBuilder.new()
        for (String line ; (line = reader.readLine()) != null ;) {
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
          offset += line.getBytes("UTF-8").length + 1
          updateProgress(offset, size)
        }
        add(currentName, currentData)
      } finally {
        reader.close()
      }
    }
    for (ShaleiaWord word : $words) {
      word.updateComparisonString($dictionary.getAlphabetOrder())
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