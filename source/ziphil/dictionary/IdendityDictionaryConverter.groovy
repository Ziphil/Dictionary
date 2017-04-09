package ziphil.dictionary

import groovy.transform.CompileStatic
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class IdentityDictionaryConverter<D extends Dictionary, W extends Word> extends DictionaryConverter<D, D, W> {

  public IdentityDictionaryConverter(D newDictionary, D oldDictionary) {
    super(newDictionary, oldDictionary)
    updateProgress(0, 1)
  }

  protected Boolean convert() {
    $newWords.addAll($newDictionary.getRawWords())
    updateProgress(1, 1)
    return true
  }

}