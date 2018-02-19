package ziphil.dictionary.personal

import groovy.transform.CompileStatic
import ziphil.dictionary.Dictionary
import ziphil.dictionary.Loader
import ziphil.dictionary.Saver
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class BinaryDictionary extends PersonalDictionary {

  public BinaryDictionary(String name, String path) {
    super(name, path)
  }

  public BinaryDictionary(String name, String path, Loader loader) {
    super(name, path, loader)
  }

}