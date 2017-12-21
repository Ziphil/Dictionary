package ziphil.dictionary

import groovy.transform.CompileStatic
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class EmptyDictionaryConverter<D extends Dictionary, W extends Word> extends DictionaryLoader<D, W> {

  public EmptyDictionaryConverter(Dictionary sourceDictionary) {
    super()
  }

  protected BooleanClass load() {
    updateProgress(1, 1)
    return true
  }

}