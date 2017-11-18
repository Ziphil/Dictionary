package ziphil.plugin

import groovy.transform.CompileStatic
import ziphil.dictionary.DictionaryType


@CompileStatic
public interface Plugin {

  public void call()

  public String getName()

  public DictionaryType getDictionaryType()

}