package ziphil.dictionary.personal

import groovy.transform.CompileStatic
import ziphil.dictionary.Dictionary
import ziphil.dictionary.DictionaryConverter
import ziphil.dictionary.DictionaryLoader
import ziphil.dictionary.DictionarySaver
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class BinaryDictionary extends PersonalDictionary {

  public BinaryDictionary(String name, String path) {
    super(name, path)
  }

  public BinaryDictionary(String name, String path, DictionaryConverter converter) {
    super(name, path, converter)
  }

  protected DictionaryLoader createLoader() {
    BinaryDictionaryLoader loader = BinaryDictionaryLoader.new(this, $path)
    return loader
  }

  protected DictionarySaver createSaver() {
    return null
  }

  protected DictionarySaver createExporter(String path) {
    return null
  }

}