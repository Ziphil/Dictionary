package ziphil.plugin

import groovy.transform.CompileStatic
import ziphil.dictionary.DictionaryType
import ziphilib.transform.Ziphilify


@CompileStatic
public class SamplePlugin implements Plugin {

  private static final String NAME = "サンプル"

  public void call() {
    println("This is a sample")
  }

  public String getName() {
    return NAME
  }

  public DictionaryType getDictionaryType() {
    return DictionaryType.SHALEIA
  }

}