package ziphil.module.akrantiain

import groovy.transform.CompileStatic
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class AkrantiainSequence implements AkrantiainMatchable {

  public static final AkrantiainSequence EMPTY_SEQUENCE = AkrantiainSequence.new()

  private List<AkrantiainMatchable> $matchables = ArrayList.new()

  public Int matchRight(AkrantiainElementGroup group, Int from, AkrantiainModule module) {
    if (!$matchables.isEmpty()) {
      Int pointer = from
      for (Int i = 0 ; i < $matchables.size() ; i ++) {
        AkrantiainMatchable matchable = $matchables[i]
        Int to = matchable.matchRight(group, pointer, module)
        if (to >= 0) {
          pointer = to
        } else {
          return -1
        }
      }
      return pointer
    } else {
      return -1
    }
  }

  public Int matchLeft(AkrantiainElementGroup group, Int to, AkrantiainModule module) {
    if (!$matchables.isEmpty()) {
      Int pointer = to
      for (Int i = $matchables.size() - 1 ; i >= 0 ; i --) {
        AkrantiainMatchable matchable = $matchables[i]
        Int from = matchable.matchLeft(group, pointer, module)
        if (from >= 0) {
          pointer = from
        } else {
          return -1
        }
      }
      return pointer
    } else {
      return -1
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
    for (Int i = 0 ; i < $matchables.size() ; i ++) {
      string.append($matchables[i])
      if (i < $matchables.size() - 1) {
        string.append(" ")
      }
    }
    return string.toString()
  }

  public Boolean hasMatchable() {
    return !$matchables.isEmpty()
  }

  public Boolean isSingleton() {
    return $matchables.size() == 1
  }

  public List<AkrantiainMatchable> getMatchables() {
    return $matchables
  }

  public void setMatchables(List<AkrantiainMatchable> matchables) {
    $matchables = matchables
  }

}