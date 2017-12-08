package ziphil.dictionary.personal

import groovy.transform.CompileStatic
import ziphil.dictionary.Dictionary
import ziphil.dictionary.DictionaryFactory
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class PersonalDictionaryFactory extends DictionaryFactory {

  private static final String EXTENSION = "csv"

  public Dictionary loadDictionary(File file) {
    Dictionary dictionary = PersonalDictionary.new(file.getName(), file.getPath())
    return dictionary
  }

  public Dictionary loadEmptyDictionary(File file) {
    Dictionary dictionary = PersonalDictionary.new(file.getName(), null)
    dictionary.setPath(file.getPath())
    return dictionary
  }

  public Dictionary convertDictionary(Dictionary oldDictionary, File file) {
    Dictionary dictionary = PersonalDictionary.new(file.getName(), file.getPath(), oldDictionary)
    return dictionary
  }

  public String getExtension() {
    return EXTENSION
  }

}