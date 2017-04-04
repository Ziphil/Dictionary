package ziphil.dictionary.converter

import groovy.transform.CompileStatic
import ziphil.dictionary.Dictionary
import ziphil.dictionary.DictionaryConverter
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class IdentityDictionaryConverter<D extends Dictionary> implements DictionaryConverter<D, D> {

  public D convert(D oldDictionary) {
    return oldDictionary
  }

}