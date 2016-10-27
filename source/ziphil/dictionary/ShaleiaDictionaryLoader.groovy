package ziphil.dictionary

import groovy.transform.CompileStatic
import java.util.regex.Matcher
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.concurrent.Task


@CompileStatic @Newify
public class ShaleiaDictionaryLoader extends Task<ObservableList<ShaleiaWord>> {

  private String $path
  private ShaleiaDictionary $dictionary
  private String $alphabetOrder = "sztdkgfvpbcqxjrlmnhyaâáàeêéèiîíìoôòuûù"

  public ShaleiaDictionaryLoader(String path, ShaleiaDictionary dictionary) {
    $path = path
    $dictionary = dictionary
  }

  protected ObservableList<ShaleiaWord> call() {
    ObservableList<ShaleiaWord> words = FXCollections.observableArrayList()
    if ($path != null) {
      File file = File.new($path)
      String currentName = null
      StringBuilder currentData = StringBuilder.new()
      file.eachLine() { String line ->
        Matcher matcher = line =~ /^\*\s*(.+)\s*$/
        if (matcher.matches()) {
          addWord(words, currentName, currentData)
          currentName = matcher.group(1)
          currentData.setLength(0)
        } else {
          currentData.append(line)
          currentData.append("\n")
        }
      }
      addWord(words, currentName, currentData)
    }
    return words
  }

  private void addWord(List<ShaleiaWord> words, String currentName, StringBuilder currentData) {
    if (currentName != null) {
      ShaleiaWord word = ShaleiaWord.new(currentName, currentData.toString())
      word.setDictionary($dictionary)
      word.createComparisonString($alphabetOrder)
      words.add(word)
    }
  }

  public String getAlphabetOrder() {
    return $alphabetOrder
  }

}