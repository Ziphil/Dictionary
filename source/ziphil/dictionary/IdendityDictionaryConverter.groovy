package ziphil.dictionary

import groovy.transform.CompileStatic
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class IdentityDictionaryConverter<D extends Dictionary, W extends Word> extends DictionaryConverter<D, D, W> {

  public IdentityDictionaryConverter(D sourceDictionary) {
    super(sourceDictionary)
    updateProgress(0, 1)
  }

  protected BooleanClass convert() {
    $words.addAll($sourceDictionary.getRawWords())
    updateProgress(1, 1)
    return true
  }

}