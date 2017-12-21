package ziphil.dictionary.personal

import groovy.transform.CompileStatic
import ziphil.dictionary.Dictionary
import ziphil.dictionary.DictionaryLoader
import ziphil.dictionary.DictionarySaver
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class BinaryDictionary extends PersonalDictionary {

  public BinaryDictionary(String name, String path) {
    super(name, path)
  }

  public BinaryDictionary(String name, String path, DictionaryLoader loader) {
    super(name, path, loader)
  }

}