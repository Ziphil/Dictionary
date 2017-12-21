package ziphil.dictionary

import groovy.transform.CompileStatic
import javafx.concurrent.Task
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public abstract class DictionaryConverter<D extends Dictionary, E extends Dictionary, W extends Word> extends Task<BooleanClass> {

  protected D $dictionary
  protected E $sourceDictionary
  protected List<W> $words = ArrayList.new()

  public DictionaryConverter(E sourceDictionary) {
    $sourceDictionary = sourceDictionary
    updateProgress(0, 1)
  }

  protected abstract BooleanClass convert()

  protected BooleanClass call() {
    if ($sourceDictionary != null) {
      BooleanClass result = convert()
      updateProgress(1, 1)
      $dictionary.getRawWords().addAll($words)
      $dictionary.updateFirst()
      for (Word word : $words) {
        if (isCancelled()) {
          return false
        }
        word.update()
      }
      return result
    } else {
      return false
    }
  }

  public void setNewDictionary(D dictionary) {
    $dictionary = dictionary
  }

}