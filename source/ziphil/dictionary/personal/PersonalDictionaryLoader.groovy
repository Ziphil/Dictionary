package ziphil.dictionary.personal

import groovy.transform.CompileStatic
import java.util.regex.Matcher
import javafx.collections.ObservableList
import ziphil.dictionary.DictionaryLoader
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class PersonalDictionaryLoader extends DictionaryLoader<PersonalDictionary, PersonalWord> {

  private static final Integer QUOTATION = 34
  private static final Integer COMMA = 44
  private static final Integer LINE_BREAK = 10
  private static final Integer CARRIAGE_RETURN = 13

  public PersonalDictionaryLoader(PersonalDictionary dictionary, String path) {
    super(dictionary, path)
  }

  protected ObservableList<PersonalWord> call() {
    if ($path != null) {
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
          if (codePoint == CARRIAGE_RETURN) {
            continue
          }
          if (isReadingValue) {
            if (isQuoted) {
              if (codePoint == QUOTATION) {
                Integer nextCodePoint = reader.read()
                if (nextCodePoint == QUOTATION) {
                  currentValue.appendCodePoint(codePoint)
                } else if (nextCodePoint == COMMA) {
                  if (isFirstLine) {
                    fillHeaderData(headerData, index, currentValue)
                  } else {
                    fillWord(word, headerData, index, currentValue)
                  }
                  currentValue.setLength(0)
                  index ++
                  isReadingValue = false
                } else if (nextCodePoint == LINE_BREAK || nextCodePoint == -1) {
                  if (isFirstLine) {
                    fillHeaderData(headerData, index, currentValue)
                  } else {
                    fillWord(word, headerData, index, currentValue)
                    word.setDictionary($dictionary)
                    word.update()
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
              if (codePoint == COMMA) {
                if (isFirstLine) {
                  fillHeaderData(headerData, index, currentValue)
                } else {
                  fillWord(word, headerData, index, currentValue)
                }
                currentValue.setLength(0)
                index ++
                isReadingValue = false
              } else if (codePoint == LINE_BREAK) {
                if (isFirstLine) {
                  fillHeaderData(headerData, index, currentValue)
                } else {
                  fillWord(word, headerData, index, currentValue)
                  word.setDictionary($dictionary)
                  word.update()
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
            if (codePoint == QUOTATION) {
              isReadingValue = true
              isQuoted = true
            } else if (codePoint == COMMA) {
              currentValue.setLength(0)
              index ++
            } else if (codePoint == LINE_BREAK) {
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
            word.update()
            $words.add(word)
          }
        }
      } finally {
        reader.close()
      }
    }
    return $words
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