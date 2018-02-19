package ziphil.dictionary

import groovy.transform.CompileStatic
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class EmptyConverter<D extends Dictionary, W extends Word> extends Loader<D, W> {

  public EmptyConverter(Dictionary sourceDictionary) {
    super()
  }

  protected BooleanClass load() {
    updateProgress(1, 1)
    return true
  }

}