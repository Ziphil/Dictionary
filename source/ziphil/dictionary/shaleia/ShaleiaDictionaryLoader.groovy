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

  protected BooleanClass load() {
    File file = File.new($path)
    BufferedReader reader = file.newReader("UTF-8")
    Long size = file.length()
    Long offset = 0L
    try {
      String currentName = null
      StringBuilder currentDescription = StringBuilder.new()
      for (String line ; (line = reader.readLine()) != null ;) {
        if (isCancelled()) {
          reader.close()
          return false
        }
        Matcher matcher = line =~ /^\*\s*(.+)\s*$/
        if (matcher.matches()) {
          add(currentName, currentDescription)
          currentName = matcher.group(1)
          currentDescription.setLength(0)
        } else {
          currentDescription.append(line)
          currentDescription.append("\n")
        }
        offset += line.getBytes("UTF-8").length + 1
        updateProgress(offset, size)
      }
      add(currentName, currentDescription)
    } finally {
      reader.close()
    }
    return true
  }

  private void add(String currentName, StringBuilder currentDescription) {
    if (currentName != null) {
      if (currentName.startsWith("META-")) {
        if (currentName == "META-ALPHABET-ORDER") {
          addAlphabetOrder(currentDescription)
        } else if (currentName == "META-VERSION") {
          addVersion(currentDescription)
        } else if (currentName == "META-CHANGE") {
          addChangeDescription(currentDescription)
        } else if (currentName == "META-SNOJ") {
          addAkrantiainSource(currentDescription)
        }
      } else {
        addWord(currentName, currentDescription)
      }
    }
  }

  private void addWord(String currentName, StringBuilder currentDescription) {
    ShaleiaWord word = ShaleiaWord.new()
    word.setUniqueName(currentName)
    word.setDescription(currentDescription.toString())
    word.setDictionary($dictionary)
    $words.add(word)
  }

  private void addAlphabetOrder(StringBuilder currentDescription) {
    String alphabetOrder = currentDescription.toString().trim().replaceAll(/^\-\s*/, "")
    $dictionary.setAlphabetOrder(alphabetOrder)
  }

  private void addVersion(StringBuilder currentDescription) {
    String version = currentDescription.toString().trim().replaceAll(/^\-\s*/, "")
    $dictionary.setVersion(version)
  }

  private void addChangeDescription(StringBuilder currentDescription) {
    String changeDescription = currentDescription.toString().replaceAll(/^\s*\n/, "")
    $dictionary.setChangeDescription(changeDescription)
  }

  private void addAkrantiainSource(StringBuilder currentDescription) {
    String akrantiainSource = currentDescription.toString().trim()
    $dictionary.setAkrantiainSource(akrantiainSource)
  }

}