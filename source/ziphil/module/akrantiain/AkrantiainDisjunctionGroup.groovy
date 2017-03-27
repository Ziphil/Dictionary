package ziphil.module.akrantiain

import groovy.transform.CompileStatic
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class AkrantiainDisjunctionGroup {

  private Boolean $isNegated = false
  private List<AkrantiainTokenGroup> $groups = ArrayList.new()

  public String toString() {
    StringBuilder string = StringBuilder.new()
    if ($isNegated) {
      string.append("!")
    }
    string.append("(")
    for (Integer i : 0 ..< $groups.size()) {
      string.append($groups[i])
      if (i < $groups.size() - 1) {
        string.append(", ")
      }
    }
    string.append(")")
    return string.toString()
  }

  public Boolean isSingleton() {
    return $groups.size() == 1
  }

  public Boolean isNegated() {
    return $isNegated
  }

  public void setNegated(Boolean isNegated) {
    $isNegated = isNegated
  }

  public AkrantiainTokenGroup getGroup() {
    return $groups[0]
  }

  public List<AkrantiainTokenGroup> getGroups() {
    return $groups
  }

  public void setGroups(List<AkrantiainTokenGroup> groups) {
    $groups = groups
  }

}