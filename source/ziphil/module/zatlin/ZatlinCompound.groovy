package ziphil.module.zatlin

import groovy.transform.CompileStatic
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class ZatlinCompound implements ZatlinGeneratable {

  private ZatlinGeneratable $generatable = null
  private ZatlinMatchable $matchable = null

  public String toString() {
    StringBuilder string = StringBuilder.new()
    string.append($generatable)
    string.append(" - ")
    string.append($matchable)
    return string.toString()
  }

  public ZatlinGeneratable getGeneratable() {
    return $generatable
  }

  public void setGeneratable(ZatlinGeneratable generatable) {
    $generatable = generatable
  }

  public ZatlinMatchable getMatchable() {
    return $matchable
  }

  public void setMatchable(ZatlinMatchable matchable) {
    $matchable = matchable
  }

}