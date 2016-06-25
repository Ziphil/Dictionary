package ziphil.dictionary

import groovy.transform.CompileStatic
import javafx.collections.ObservableList


@CompileStatic @Newify
public abstract class Dictionary {

  public abstract void searchByName(String search, Boolean isStrict)

  public abstract void searchByEquivalent(String search, Boolean isStrict)

  public abstract void searchByContent(String search)

  public abstract void addWord(Word word)

  public abstract void removeWord(Word word)

  public abstract void save()

  public abstract Boolean supportsEquivalent()

  public abstract String getName()

  public abstract void setName(String name)

  public abstract String getPath()

  public abstract void setPath(String path)

  public abstract DictionaryType getType()

  public abstract ObservableList<? extends Word> getWords()

  public abstract ObservableList<? extends Word> getRawWords()

  public static Dictionary loadDictionary(File file) {
    Dictionary dictionary
    String fileName = file.getName()
    String filePath = file.getPath()
    DictionaryType type = DictionaryType.valueOfPath(filePath)
    if (type == DictionaryType.SHALEIA) {
      dictionary = ShaleiaDictionary.new(fileName, filePath)
    } else if (type == DictionaryType.PERSONAL) {
      dictionary = PersonalDictionary.new(fileName, filePath)
    }
    return dictionary
  }

  public static Dictionary loadEmptyDictionary(File file) {
    Dictionary dictionary
    String fileName = file.getName()
    String filePath = file.getPath()
    DictionaryType type = DictionaryType.valueOfPath(filePath)
    if (type == DictionaryType.SHALEIA) {
      dictionary = ShaleiaDictionary.new(fileName, null)
      dictionary.setPath(filePath)
    } else if (type == DictionaryType.PERSONAL) {
      dictionary = PersonalDictionary.new(fileName, null)
      dictionary.setPath(filePath)
    }
    return dictionary
  }

}