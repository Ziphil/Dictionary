package ziphil.dictionary

import groovy.transform.CompileStatic


@CompileStatic
public interface DictionaryConverter<D extends Dictionary, E extends Dictionary> {

  public E convert(D oldDictionary)

}