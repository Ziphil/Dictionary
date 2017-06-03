package ziphil.dictionary

import groovy.transform.CompileStatic
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class EmptyDictionaryConverter<D extends Dictionary, E extends Dictionary, W extends Word> extends DictionaryConverter<D, E, W> {

  public EmptyDictionaryConverter(D newDictionary, E oldDictionary) {
    super(newDictionary, oldDictionary)
    updateProgress(0, 1)
  }

  protected BooleanClass convert() {
    updateProgress(1, 1)
    return true
  }

}