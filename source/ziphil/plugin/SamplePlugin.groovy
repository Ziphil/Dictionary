package ziphil.plugin

import groovy.transform.CompileStatic
import ziphil.dictionary.Dictionary
import ziphil.dictionary.DictionaryType
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class SamplePlugin implements Plugin {

  private static final String NAME = "サンプル"

  public void call(Dictionary dictionary) {
    println("This is a sample")
  }

  public Boolean isSupported(Dictionary dictionary) {
    DictionaryType dictionaryType = DictionaryType.valueOfDictionary(dictionary)
    return dictionaryType == DictionaryType.SHALEIA
  }

  public String getName() {
    return NAME
  }

}