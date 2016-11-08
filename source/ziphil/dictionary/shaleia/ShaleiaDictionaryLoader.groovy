package ziphil.dictionary.shaleia

import groovy.transform.CompileStatic
import java.util.regex.Matcher
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.concurrent.Task
import ziphil.module.ThrowMarker


@CompileStatic @Newify
public class ShaleiaDictionaryLoader extends Task<ObservableList<ShaleiaWord>> {

  private ObservableList<ShaleiaWord> $words = FXCollections.observableArrayList()
  private String $path
  private ShaleiaDictionary $dictionary
  private String $changeData = ""
  private String $alphabetOrder = "sztdkgfvpbcqxjrlmnhyaâáàeêéèiîíìoôòuûù"

  public ShaleiaDictionaryLoader(String path, ShaleiaDictionary dictionary) {
    $path = path
    $dictionary = dictionary
  }

  protected ObservableList<ShaleiaWord> call() {
    if ($path != null) {
      BufferedReader reader = File.new($path).newReader("UTF-8")
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
    return $words
  }

  private void add(String currentName, StringBuilder currentData) {
    if (currentName != null) {
      if (currentName == "META-CHANGE") {
        addChangeData(currentData)
      } else {
        addWord(currentName, currentData)
      }
    }
  }

  private void addWord(String currentName, StringBuilder currentData) {
    ShaleiaWord word = ShaleiaWord.new(currentName, currentData.toString())
    word.setDictionary($dictionary)
    word.createComparisonString($alphabetOrder)
    $words.add(word)
  }

  private void addChangeData(StringBuilder currentData) {
    $changeData = currentData.toString()
  }

  private String getChangeData() {
    return $changeData
  }

  public String getAlphabetOrder() {
    return $alphabetOrder
  }

}