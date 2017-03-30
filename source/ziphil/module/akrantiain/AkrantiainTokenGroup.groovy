package ziphil.module.akrantiain

import groovy.transform.CompileStatic
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class AkrantiainTokenGroup {

  public static final AkrantiainTokenGroup EMPTY_GROUP = AkrantiainTokenGroup.new()

  private List<AkrantiainToken> $tokens = ArrayList.new()

  public Integer matchSelection(AkrantiainElementGroup group, Integer from, AkrantiainSetting setting) {
    if (!$tokens.isEmpty()) {
      Integer pointer = from
      for (AkrantiainToken token : $tokens) {
        Integer to = token.matchSelection(group, pointer, setting)
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

  public Boolean matchLeftCondition(AkrantiainElementGroup group, Integer to, AkrantiainSetting setting) {
    if (!$tokens.isEmpty()) {
      if ($tokens.size() > 1 || $tokens[0].getType() == AkrantiainTokenType.QUOTE_LITERAL) {
        StringBuilder mergedText = StringBuilder.new()
        for (AkrantiainToken token : $tokens) {
          mergedText.append(token.getText())
        }
        return group.merge(0, to).getPart().endsWith(mergedText.toString())
      } else {
        return setting.findContentOf($tokens[0].getText()).matchLeftCondition(group, to, setting)
      }
    } else {
      return true
    }
  }

  public Boolean matchRightCondition(AkrantiainElementGroup group, Integer from, AkrantiainSetting setting) {
    if (!$tokens.isEmpty()) {
      if ($tokens.size() > 1 || $tokens[0].getType() == AkrantiainTokenType.QUOTE_LITERAL) {
        StringBuilder mergedText = StringBuilder.new()
        for (AkrantiainToken token : $tokens) {
          mergedText.append(token.getText())
        }
        return group.merge(from, group.getElements().size()).getPart().startsWith(mergedText.toString())
      } else {
        return setting.findContentOf($tokens[0].getText()).matchRightCondition(group, from, setting)
      }
    } else {
      return true
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