package ziphil.plugin

import groovy.transform.CompileStatic
import ziphil.dictionary.Dictionary
import ziphil.dictionary.DictionaryType
import ziphilib.transform.ConvertPrimitives


@CompileStatic @ConvertPrimitives
public interface Plugin {

  public void call(Dictionary dictionary)

  public Boolean isSupported(Dictionary dictionary)

  public String getName()

}