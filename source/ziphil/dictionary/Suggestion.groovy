package ziphil.dictionary

import groovy.transform.CompileStatic


@CompileStatic
public interface Suggestion<P> extends Element {

  public Boolean isDisplayed()

  public void setDisplayed(Boolean isDisplayed)

  public List<P> getPossibilities()

  public void setPossibilities(List<P> possibilities)

}