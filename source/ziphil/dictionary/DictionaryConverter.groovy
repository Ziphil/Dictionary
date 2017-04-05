package ziphil.dictionary

import groovy.transform.CompileStatic
import javafx.concurrent.Task
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public abstract class DictionaryConverter<D extends Dictionary, E extends Dictionary, W extends Word> extends Task<Boolean> {

  protected D $newDictionary
  protected E $oldDictionary
  protected List<W> $newWords = ArrayList.new()

  public DictionaryConverter(D newDictionary, E oldDictionary) {
    $newDictionary = newDictionary
    $oldDictionary = oldDictionary
  }

  protected abstract Boolean convert()

  protected Boolean call() {
    if ($oldDictionary != null) {
      Boolean result = convert()
      $newDictionary.getRawWords().addAll($newWords)
      $newDictionary.updateFirst()
      for (Word newWord : $newWords) {
        if (isCancelled()) {
          return false
        }
        newWord.update()
      }
      return result
    } else {
      return false
    }
  }

}