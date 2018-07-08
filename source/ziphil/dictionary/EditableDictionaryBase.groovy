package ziphil.dictionary

import groovy.transform.CompileStatic
import javafx.concurrent.Task
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public abstract class EditableDictionaryBase<W extends Word, S extends Suggestion, F extends EditableDictionaryFactory> extends DictionaryBase<W, S, F> implements EditableDictionary<W, W, F> {

  public EditableDictionaryBase(String name, String path) {
    super(name, path)
  }

  public EditableDictionaryBase(String name, String path, Loader loader) {
    super(name, path, loader)
  }

  public void modifyWord(W oldWord, W newWord) {
    $changed = true
  }

  public void addWord(W word) {
    $words.add(word)
    $changed = true
  }

  public void addWords(List<? extends W> words) {
    $words.addAll(words)
    $changed = true
  }

  public void removeWord(W word) {
    $words.remove(word)
    $changed = true
  }

  public void removeWords(List<? extends W> words) {
    $words.removeAll(words)
    $changed = true
  }

  public void mergeWord(W mergedWord, W removedWord) {
    $words.remove(removedWord)
    $changed = true
  }

  public abstract W createWord(String defaultName)

  public abstract W copyWord(W oldWord)

  public abstract W inheritWord(W oldWord)

  public abstract W determineWord(String name, PseudoWord psuedoWord)

}