package ziphil.module.akrantiain

import groovy.transform.CompileStatic
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class AkrantiainSequence implements AkrantiainMatchable {

  public static final AkrantiainSequence EMPTY_SEQUENCE = AkrantiainSequence.new()

  private List<AkrantiainMatchable> $matchables = ArrayList.new()

  public Integer matchRight(AkrantiainElementGroup group, Integer from, AkrantiainModule module) {
    if (!$matchables.isEmpty()) {
      Integer pointer = from
      for (Integer i : 0 ..< $matchables.size()) {
        AkrantiainMatchable matchable = $matchables[i]
        Integer to = matchable.matchRight(group, pointer, module)
        if (to != null) {
          pointer = to
        } else {
          return null
        }
      }
      return pointer
    } else {
      return null
    }
  }

  public Integer matchLeft(AkrantiainElementGroup group, Integer to, AkrantiainModule module) {
    if (!$matchables.isEmpty()) {
      Integer pointer = to
      for (Integer i : $matchables.size() - 1 .. 0) {
        AkrantiainMatchable matchable = $matchables[i]
        Integer from = matchable.matchLeft(group, pointer, module)
        if (from != null) {
          pointer = from
        } else {
          return null
        }
      }
      return pointer
    } else {
      return null
    }
  }

  public AkrantiainToken findDeadIdentifier(AkrantiainModule module) {
    for (AkrantiainMatchable matchable : $matchables) {
      AkrantiainToken deadIdentifier = matchable.findDeadIdentifier(module)
      if (deadIdentifier != null) {
        return deadIdentifier
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
    for (Integer i : 0 ..< $matchables.size()) {
      string.append($matchables[i])
      if (i < $matchables.size() - 1) {
        string.append(" ")
      }
    }
    return string.toString()
  }

  public Boolean hasToken() {
    return !$matchables.isEmpty()
  }

  public Boolean isSingleton() {
    return $matchables.size() == 1
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