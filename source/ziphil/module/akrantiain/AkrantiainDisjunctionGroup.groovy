package ziphil.module.akrantiain

import groovy.transform.CompileStatic
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class AkrantiainDisjunctionGroup implements AkrantiainMatchable {

  public static final AkrantiainDisjunctionGroup EMPTY_GROUP = AkrantiainDisjunctionGroup.new()

  private Boolean $isNegated = false
  private List<AkrantiainTokenGroup> $tokenGroups = ArrayList.new()

  public Integer matchRight(AkrantiainElementGroup group, Integer from, AkrantiainSetting setting) {
    Integer to = null
    if (!$tokenGroups.isEmpty()) {
      for (Integer i : $tokenGroups.size() - 1 .. 0) {
        AkrantiainTokenGroup tokenGroup = $tokenGroups[i]
        Integer singleTo = tokenGroup.matchRight(group, from, setting)
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
    if (!$tokenGroups.isEmpty()) {
      for (Integer i : $tokenGroups.size() - 1 .. 0) {
        AkrantiainTokenGroup tokenGroup = $tokenGroups[i]
        Integer singleFrom = tokenGroup.matchLeft(group, to, setting)
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
    return $tokenGroups.size() >= 2 || ($tokenGroups.size() >= 1 && $tokenGroups[0].isConcrete())
  }

  public String toString() {
    StringBuilder string = StringBuilder.new()
    if ($isNegated) {
      string.append("!")
    }
    string.append("(")
    for (Integer i : 0 ..< $tokenGroups.size()) {
      string.append($tokenGroups[i])
      if (i < $tokenGroups.size() - 1) {
        string.append(", ")
      }
    }
    string.append(")")
    return string.toString()
  }

  public Boolean isSingleton() {
    return $tokenGroups.size() == 1
  }

  public Boolean isNegated() {
    return $isNegated
  }

  public void setNegated(Boolean isNegated) {
    $isNegated = isNegated
  }

  public AkrantiainTokenGroup getTokenGroup() {
    return $tokenGroups[0]
  }

  public List<AkrantiainTokenGroup> getTokenGroups() {
    return $tokenGroups
  }

  public void setTokenGroups(List<AkrantiainTokenGroup> tokenGroups) {
    $tokenGroups = tokenGroups
  }

}