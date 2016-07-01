package ziphil.dictionary

import groovy.transform.CompileStatic
import javafx.collections.ObservableList


@CompileStatic @Newify
public abstract class Dictionary<W extends Word> {

  public abstract void searchByName(String search, Boolean isStrict)

  public abstract void searchByEquivalent(String search, Boolean isStrict)

  public abstract void searchByContent(String search)

  public abstract void modifyWord(W oldWord, W newWord)

  public abstract void addWord(W word)

  public abstract void removeWord(W word)

  public abstract W emptyWord()

  public abstract W copiedWord(W oldWord)

  public abstract W inheritedWord(W oldWord)

  public abstract void save()

  public abstract Boolean supportsEquivalent()

  public abstract String getName()

  public abstract void setName(String name)

  public abstract String getPath()

  public abstract void setPath(String path)

  public abstract ObservableList<W> getWords()

  public abstract ObservableList<W> getRawWords()

  public static Dictionary loadDictionary(File file) {
    Dictionary dictionary
    String fileName = file.getName()
    String filePath = file.getPath()
    if (filePath.endsWith(".xdc")) {
      dictionary = ShaleiaDictionary.new(fileName, filePath)
    } else if (filePath.endsWith(".csv")) {
      dictionary = PersonalDictionary.new(fileName, filePath)
    } else if (filePath.endsWith(".json")) {
      dictionary = SlimeDictionary.new(fileName, filePath)
    }
    return dictionary
  }

  public static Dictionary loadEmptyDictionary(File file) {
    Dictionary dictionary
    String fileName = file.getName()
    String filePath = file.getPath()
    if (filePath.endsWith(".xdc")) {
      dictionary = ShaleiaDictionary.new(fileName, null)
      dictionary.setPath(filePath)
    } else if (filePath.endsWith(".xdc")) {
      dictionary = PersonalDictionary.new(fileName, null)
      dictionary.setPath(filePath)
    } else if (filePath.endsWith(".xdc")) {
      dictionary = SlimeDictionary.new(fileName, null)
      dictionary.setPath(filePath)
    }
    return dictionary
  }

}