package ziphil.dictionary.personal

import groovy.transform.CompileStatic
import ziphil.dictionary.DictionaryLoader
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class BinaryDictionaryLoader extends DictionaryLoader<PersonalDictionary, PersonalWord> {

  public BinaryDictionaryLoader(PersonalDictionary dictionary, String path) {
    super(dictionary, path)
  }

  protected BooleanClass load() {
    File file = File.new($path)
    return true
  }

}