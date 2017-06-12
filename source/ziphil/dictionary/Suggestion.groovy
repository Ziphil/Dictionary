package ziphil.dictionary

import groovy.transform.CompileStatic
import ziphilib.transform.ConvertPrimitives


@CompileStatic @ConvertPrimitives
public interface Suggestion<P extends Possibility> extends Element {

  public Boolean isDisplayed()

  public void setDisplayed(Boolean displayed)

  public List<P> getPossibilities()

  public void setPossibilities(List<P> possibilities)

}