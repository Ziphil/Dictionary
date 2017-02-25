package ziphil.dictionary

import groovy.transform.CompileStatic
import javafx.collections.ObservableList
import javafx.concurrent.Task


@CompileStatic 
public interface EditableDictionary<W extends Word, V extends Word> {

  public void modifyWord(W oldWord, V newWord)

  public void addWord(V word)

  public void removeWord(V word)

  public V emptyWord(String defaultName)

  public V copiedWord(W oldWord)

  public V inheritedWord(W oldWord)

}