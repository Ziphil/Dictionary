package ziphil.dictionary

import groovy.transform.CompileStatic
import javafx.collections.ObservableList
import javafx.concurrent.Task


@CompileStatic 
public interface EditableDictionary<W extends Word, V extends Word> {

  public void modifyWord(W oldWord, V newWord)

  public void addWord(V word)

  public void addWords(List<? extends V> words)

  public void removeWord(V word)

  public void removeWords(List<? extends V> words)

  public void mergeWord(V mergedWord, V removedWord)

  public V createWord(String defaultName)

  public V copyWord(W oldWord)

  public V inheritWord(W oldWord)

  public V determineWord(String name, PseudoWord psuedoWord)

}