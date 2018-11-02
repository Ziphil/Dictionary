package ziphil.dictionary

import groovy.transform.CompileStatic
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class IdentityConverter<D extends Dictionary, W extends Word> extends Loader<D, W> {

  private D $sourceDictionary

  public IdentityConverter(D sourceDictionary) {
    super()
    $sourceDictionary = sourceDictionary
  }

  protected BooleanClass load() {
    $words.addAll($sourceDictionary.getRawWords())
    updateProgress(1, 1)
    return true
  }

}