package ziphil.dictionary

import groovy.transform.CompileStatic
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public abstract class Suggestion<P> extends Word {

  protected List<P> $possibilities = ArrayList.new()

  public List<P> getPossibilities() {
    return $possibilities
  }

  public void setPossibilities(List<P> possibilities) {
    $possibilities = possibilities
  }

}