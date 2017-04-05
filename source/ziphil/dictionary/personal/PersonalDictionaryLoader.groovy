package ziphil.dictionary.personal

import groovy.transform.CompileStatic
import java.util.regex.Matcher
import javafx.collections.ObservableList
import ziphil.dictionary.DictionaryLoader
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class PersonalDictionaryLoader extends DictionaryLoader<PersonalDictionary, PersonalWord> {

  public PersonalDictionaryLoader(PersonalDictionary dictionary, String path) {
    super(dictionary, path)
    updateProgress(0, 1)
  }

  protected Boolean load() {
    File file = File.new($path)
    BufferedReader reader = file.newReader()
    PersonalWord word = PersonalWord.new()
    StringBuilder currentValue = StringBuilder.new()
    Map<Integer, String> headerData = HashMap.new()
    Integer index = 0
    Boolean isFirstLine = true
    Boolean isReadingValue = false
    Boolean isQuoted = false
    try {
      for (Integer codePoint = -1 ; (codePoint = reader.read()) != -1 ;) {
        if (codePoint == '\r') {
          continue
        }
        if (isReadingValue) {
          if (isQuoted) {
            if (codePoint == '"') {
              Integer nextCodePoint = reader.read()
              if (nextCodePoint == '"') {
                currentValue.appendCodePoint(codePoint)
              } else if (nextCodePoint == ',') {
                if (isFirstLine) {
                  fillHeaderData(headerData, index, currentValue)
                } else {
                  fillWord(word, headerData, index, currentValue)
                }
                currentValue.setLength(0)
                index ++
                isReadingValue = false
              } else if (nextCodePoint == '\n' || nextCodePoint == -1) {
                if (isFirstLine) {
                  fillHeaderData(headerData, index, currentValue)
                } else {
                  fillWord(word, headerData, index, currentValue)
                  word.setDictionary($dictionary)
                  $words.add(word)
                }
                word = PersonalWord.new()
                currentValue.setLength(0)
                index = 0
                isReadingValue = false
                isFirstLine = false
              }
            } else {
              currentValue.appendCodePoint(codePoint)
            }
          } else {
            if (codePoint == ',') {
              if (isFirstLine) {
                fillHeaderData(headerData, index, currentValue)
              } else {
                fillWord(word, headerData, index, currentValue)
              }
              currentValue.setLength(0)
              index ++
              isReadingValue = false
            } else if (codePoint == '\n') {
              if (isFirstLine) {
                fillHeaderData(headerData, index, currentValue)
              } else {
                fillWord(word, headerData, index, currentValue)
                word.setDictionary($dictionary)
                $words.add(word)
              }
              word = PersonalWord.new()
              currentValue.setLength(0)
              index = 0
              isReadingValue = false
              isFirstLine = false
            } else {
              currentValue.appendCodePoint(codePoint)
            }
          }
        } else {
          if (codePoint == '"') {
            isReadingValue = true
            isQuoted = true
          } else if (codePoint == ',') {
            currentValue.setLength(0)
            index ++
          } else if (codePoint == '\n') {
            word = PersonalWord.new()
            currentValue.setLength(0)
            index = 0
            isFirstLine = false
          } else {
            currentValue.appendCodePoint(codePoint)
            isReadingValue = true
            isQuoted = false
          }
        }
      }
      if (isReadingValue) {
        if (isFirstLine) {
          fillHeaderData(headerData, index, currentValue)
        } else {
          fillWord(word, headerData, index, currentValue)
          word.setDictionary($dictionary)
          $words.add(word)
        }
      }
    } finally {
      reader.close()
    }
    updateProgress(1, 1)
    return true
  }

  private void fillHeaderData(Map<Integer, String> headerData, Integer index, StringBuilder currentValue) {
    headerData.put(index, currentValue.toString())
  }

  private void fillWord(PersonalWord word, Map<Integer, String> headerData, Integer index, StringBuilder currentValue) {
    String mode = headerData[index]
    if (mode == "word") {
      word.setName(currentValue.toString())
    } else if (mode == "pron") {
      word.setPronunciation(currentValue.toString())
    } else if (mode == "trans") {
      word.setTranslation(currentValue.toString())
    } else if (mode == "exp") {
      word.setUsage(currentValue.toString())
    } else if (mode == "level") {
      word.setLevel(currentValue.toString().toInteger())
    } else if (mode == "memory") {
      word.setLevel(currentValue.toString().toInteger())
    } else if (mode == "modify") {
      word.setModification(currentValue.toString().toInteger())
    }
  }

}