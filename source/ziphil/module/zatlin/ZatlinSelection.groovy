package ziphil.module.zatlin

import groovy.transform.CompileStatic
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class ZatlinSelection implements ZatlinGeneratable {

  public static final ZatlinSelection EMPTY_SELECTION = ZatlinSelection.new()

  private List<ZatlinGeneratable> $generatables = ArrayList.new()
  private List<IntegerClass> $weights = ArrayList.new()

  public String toString() {
    StringBuilder string = StringBuilder.new()
    string.append("(")
    for (Int i = 0 ; i < $generatables.size() ; i ++) {
      string.append($generatables[i])
      if (i < $generatables.size() - 1) {
        string.append(" | ")
      }
    }
    string.append(")")
    return string.toString()
  }

  public List<ZatlinGeneratable> getGeneratables() {
    return $generatables
  }

  public void setGeneratables(List<ZatlinGeneratable> generatables) {
    $generatables = generatables
  }

  public List<IntegerClass> getWeights() {
    return $weights
  }

  public void setWeights(List<IntegerClass> weights) {
    $weights = weights
  }

}