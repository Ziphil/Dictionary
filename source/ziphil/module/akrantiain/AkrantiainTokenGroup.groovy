package ziphil.module.akrantiain

import groovy.transform.CompileStatic
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class AkrantiainTokenGroup {

  private List<AkrantiainToken> $tokens = ArrayList.new()

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

  public Boolean hasLiteral() {
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