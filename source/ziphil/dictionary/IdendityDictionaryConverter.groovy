package ziphil.dictionary

import groovy.transform.CompileStatic
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class IdentityDictionaryConverter<D extends Dictionary, W extends Word> extends DictionaryLoader<D, W> {

  private D $sourceDictionary

  public IdentityDictionaryConverter(D sourceDictionary) {
    super()
    $sourceDictionary = sourceDictionary
  }

  protected BooleanClass load() {
    $words.addAll($sourceDictionary.getRawWords())
    updateProgress(1, 1)
    return true
  }

}