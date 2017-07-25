package ziphil.module.zatlin

import groovy.transform.CompileStatic
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class ZatlinSelection implements ZatlinGeneratable {

  public static final ZatlinSelection EMPTY_SELECTION = ZatlinSelection.new()
  private static final Random RANDOM = Random.new()

  private List<ZatlinGeneratable> $generatables = ArrayList.new()
  private List<DoubleClass> $weights = ArrayList.new()

  public String generate(ZatlinRoot root) {
    String output = ""
    Double number = RANDOM.nextDouble() * totalWeight()
    Double currentWeight = 0
    for (Int i = 0 ; i < $generatables.size() ; i ++) {
      currentWeight += $weights[i]
      if (number < currentWeight) {
        output = $generatables[i].generate(root)
        break
      }
    }
    return output
  }

  public ZatlinToken findUnknownIdentifier(ZatlinRoot root) {
    for (ZatlinGeneratable generatable : $generatables) {
      ZatlinToken unknownIdentifier = generatable.findUnknownIdentifier(root)
      if (unknownIdentifier != null) {
        return unknownIdentifier
      }
    }
    return null
  }

  public ZatlinToken findCircularIdentifier(List<ZatlinToken> identifiers, ZatlinRoot root) {
    for (ZatlinGeneratable generatable : $generatables) {
      ZatlinToken circularIdentifier = generatable.findCircularIdentifier(identifiers, root)
      if (circularIdentifier != null) {
        return circularIdentifier
      }
    }
    return null
  }

  private Double totalWeight() {
    Double totalWeight = 0
    for (Double weight : $weights) {
      totalWeight += weight
    }
    return totalWeight
  }

  public String toString() {
    StringBuilder string = StringBuilder.new()
    string.append("(")
    for (Int i = 0 ; i < $generatables.size() ; i ++) {
      string.append($generatables[i])
      string.append(" ")
      string.append($weights[i])
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

  public List<DoubleClass> getWeights() {
    return $weights
  }

  public void setWeights(List<DoubleClass> weights) {
    $weights = weights
  }

}