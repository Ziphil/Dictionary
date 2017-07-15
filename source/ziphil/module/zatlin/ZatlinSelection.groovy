package ziphil.module.zatlin

import groovy.transform.CompileStatic
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class ZatlinSelection implements ZatlinGeneratable {

  public static final ZatlinSelection EMPTY_SELECTION = ZatlinSelection.new()
  private static final Random RANDOM = Random.new()

  private List<ZatlinGeneratable> $generatables = ArrayList.new()
  private List<IntegerClass> $weights = ArrayList.new()

  public String generate(ZatlinRoot root) {
    String output = ""
    Int number = RANDOM.nextInt(totalWeight())
    Int currentWeight = 0
    for (Int i = 0 ; i < $generatables.size() ; i ++) {
      currentWeight += $weights[i]
      if (number < currentWeight) {
        output = $generatables[i].generate(root)
      }
    }
    return output
  }

  private Int totalWeight() {
    Int totalWeight = 0
    for (Int weight : $weights) {
      totalWeight += weight
    }
    return totalWeight
  }

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