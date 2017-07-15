package ziphil.module.zatlin

import groovy.transform.CompileStatic
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class ZatlinDisjunction implements ZatlinMatchable {

  public static final ZatlinDisjunction EMPTY_DISJUNCTION = ZatlinDisjunction.new()

  private List<ZatlinMatchable> $matchables = ArrayList.new()

  public String toString() {
    StringBuilder string = StringBuilder.new()
    string.append("(")
    for (Int i = 0 ; i < $matchables.size() ; i ++) {
      string.append($matchables[i])
      if (i < $matchables.size() - 1) {
        string.append(" | ")
      }
    }
    string.append(")")
    return string.toString()
  }

  public List<ZatlinMatchable> getMatchables() {
    return $matchables
  }

  public void setMatchables(List<ZatlinMatchable> matchables) {
    $matchables = matchables
  }

}