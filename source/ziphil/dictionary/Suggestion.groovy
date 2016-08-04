package ziphil.dictionary

import groovy.transform.CompileStatic


@CompileStatic @Newify
public abstract class Suggestion<P> extends Word {

  protected List<P> $possibilities = ArrayList.new()

  public List<P> getPossibilities() {
    return $possibilities
  }

  public void setPossibilities(List<P> possibilities) {
    $possibilities = possibilities
  }

}