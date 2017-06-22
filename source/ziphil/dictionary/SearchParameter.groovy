package ziphil.dictionary

import groovy.transform.CompileStatic
import ziphilib.transform.ConvertPrimitives


@CompileStatic @ConvertPrimitives
public interface SearchParameter<W extends Word> {

  public void prepare(Dictionary dictionary)

  public Boolean matches(W word)

}