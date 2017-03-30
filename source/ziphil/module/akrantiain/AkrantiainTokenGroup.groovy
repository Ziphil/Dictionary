package ziphil.module.akrantiain

import groovy.transform.CompileStatic
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class AkrantiainTokenGroup {

  public static final AkrantiainTokenGroup EMPTY_GROUP = AkrantiainTokenGroup.new()

  private List<AkrantiainToken> $tokens = ArrayList.new()

  public Integer matchRight(AkrantiainElementGroup group, Integer from, AkrantiainSetting setting) {
    if (!$tokens.isEmpty()) {
      Integer pointer = from
      for (Integer i : 0 ..< $tokens.size()) {
        AkrantiainToken token = $tokens[i]
        Integer to = token.matchRight(group, pointer, setting)
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

  public Integer matchLeft(AkrantiainElementGroup group, Integer to, AkrantiainSetting setting) {
    if (!$tokens.isEmpty()) {
      Integer pointer = to
      for (Integer i : $tokens.size() - 1 .. 0) {
        AkrantiainToken token = $tokens[i]
        Integer from = token.matchLeft(group, pointer, setting)
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

  public String toString() {
    StringBuilder string = StringBuilder.new()
    for (Integer i : 0 ..< $tokens.size()) {
      string.append($tokens[i])
      if (i < $tokens.size() - 1) {
        string.append(" ")
      }
    }
    return string.toString()
  }

  public Boolean hasToken() {
    return !$tokens.isEmpty()
  }

  public Boolean isSingleton() {
    return $tokens.size() == 1
  }

  public AkrantiainToken getToken() {
    return $tokens[0]
  }

  public List<AkrantiainToken> getTokens() {
    return $tokens
  }

  public void setTokens(List<AkrantiainToken> tokens) {
    $tokens = tokens
  }

}