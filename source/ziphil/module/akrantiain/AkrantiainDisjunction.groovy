package ziphil.module.akrantiain

import groovy.transform.CompileStatic
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class AkrantiainDisjunction implements AkrantiainMatchable {

  public static final AkrantiainDisjunction EMPTY_DISJUNCTION = AkrantiainDisjunction.new()

  private Boolean $negated = false
  private List<AkrantiainMatchable> $matchables = ArrayList.new()

  public Int matchRight(AkrantiainElementGroup group, Int from, AkrantiainModule module) {
    Int to = -1
    if (!$matchables.isEmpty()) {
      for (Int i = $matchables.size() - 1 ; i >= 0 ; i --) {
        AkrantiainMatchable matchable = $matchables[i]
        Int singleTo = matchable.matchRight(group, from, module)
        if (singleTo >= 0) {
          to = singleTo
          break
        }
      }
    }
    if ($negated) {
      return (to == -1) ? from : -1
    } else {
      return to
    }
  }

  public Int matchLeft(AkrantiainElementGroup group, Int to, AkrantiainModule module) {
    Int from = -1
    if (!$matchables.isEmpty()) {
      for (Int i = $matchables.size() - 1 ; i >= 0 ; i --) {
        AkrantiainMatchable matchable = $matchables[i]
        Int singleFrom = matchable.matchLeft(group, to, module)
        if (singleFrom >= 0) {
          from = singleFrom
          break
        }
      }
    }
    if ($negated) {
      return (from == -1) ? to : -1
    } else {
      return from
    }
  }

  public AkrantiainToken findUnknownIdentifier(AkrantiainModule module) {
    for (AkrantiainMatchable matchable : $matchables) {
      AkrantiainToken unknownIdentifier = matchable.findUnknownIdentifier(module)
      if (unknownIdentifier != null) {
        return unknownIdentifier
      }
    }
    return null
  }

  public AkrantiainToken findCircularIdentifier(List<AkrantiainToken> identifiers, AkrantiainModule module) {
    for (AkrantiainMatchable matchable : $matchables) {
      AkrantiainToken circularIdentifier = matchable.findCircularIdentifier(identifiers, module)
      if (circularIdentifier != null) {
        return circularIdentifier
      }
    }
    return null
  }

  public Boolean isConcrete() {
    return $matchables.size() >= 2 || ($matchables.size() >= 1 && $matchables[0].isConcrete())
  }

  public String toString() {
    StringBuilder string = StringBuilder.new()
    if ($negated) {
      string.append("!")
    }
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

  public Boolean isSingleton() {
    return $matchables.size() == 1
  }

  public Boolean isNegated() {
    return $negated
  }

  public void setNegated(Boolean negated) {
    $negated = negated
  }

  public AkrantiainMatchable getMatchable() {
    return $matchables[0]
  }

  public List<AkrantiainMatchable> getMatchables() {
    return $matchables
  }

  public void setMatchables(List<AkrantiainMatchable> matchables) {
    $matchables = matchables
  }

}