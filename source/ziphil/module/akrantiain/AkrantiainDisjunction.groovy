package ziphil.module.akrantiain

import groovy.transform.CompileStatic
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class AkrantiainDisjunction implements AkrantiainMatchable {

  public static final AkrantiainDisjunction EMPTY_DISJUNCTION = AkrantiainDisjunction.new()

  private Boolean $isNegated = false
  private List<AkrantiainMatchable> $matchables = ArrayList.new()

  public Integer matchRight(AkrantiainElementGroup group, Integer from, AkrantiainSetting setting) {
    Integer to = null
    if (!$matchables.isEmpty()) {
      for (Integer i : $matchables.size() - 1 .. 0) {
        AkrantiainMatchable matchable = $matchables[i]
        Integer singleTo = matchable.matchRight(group, from, setting)
        if (singleTo != null) {
          to = singleTo
          break
        }
      }
    }
    if ($isNegated) {
      return (to == null) ? from : null
    } else {
      return to
    }
  }

  public Integer matchLeft(AkrantiainElementGroup group, Integer to, AkrantiainSetting setting) {
    Integer from = null
    if (!$matchables.isEmpty()) {
      for (Integer i : $matchables.size() - 1 .. 0) {
        AkrantiainMatchable matchable = $matchables[i]
        Integer singleFrom = matchable.matchLeft(group, to, setting)
        if (singleFrom != null) {
          from = singleFrom
          break
        }
      }
    }
    if ($isNegated) {
      return (from == null) ? to : null
    } else {
      return from
    }
  }

  public Boolean isConcrete() {
    return $matchables.size() >= 2 || ($matchables.size() >= 1 && $matchables[0].isConcrete())
  }

  public String toString() {
    StringBuilder string = StringBuilder.new()
    if ($isNegated) {
      string.append("!")
    }
    string.append("(")
    for (Integer i : 0 ..< $matchables.size()) {
      string.append($matchables[i])
      if (i < $matchables.size() - 1) {
        string.append(", ")
      }
    }
    string.append(")")
    return string.toString()
  }

  public Boolean isSingleton() {
    return $matchables.size() == 1
  }

  public Boolean isNegated() {
    return $isNegated
  }

  public void setNegated(Boolean isNegated) {
    $isNegated = isNegated
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