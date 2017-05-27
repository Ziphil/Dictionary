package ziphil.module.akrantiain

import groovy.transform.CompileStatic
import ziphilib.transform.Ziphilify
import ziphilib.type.PrimBoolean


@CompileStatic @Ziphilify
public class AkrantiainModuleName {

  private List<AkrantiainToken> $tokens = ArrayList.new()

  public PrimBoolean equals(Object object) {
    if (object instanceof AkrantiainModuleName) {
      return $tokens == object.getTokens()
    } else {
      return false
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

  public List<AkrantiainToken> getTokens() {
    return $tokens
  }

  public void setTokens(List<AkrantiainToken> tokens) {
    $tokens = tokens
  }

}