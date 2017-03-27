package ziphil.module.akrantiain

import groovy.transform.CompileStatic
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class AkrantiainDefinition {

  private AkrantiainToken $identifier
  private List<AkrantiainToken> $literals = ArrayList.new()

  public String toString() {
    StringBuilder string = StringBuilder.new()
    string.append($identifier)
    string.append(" = [")
    for (Integer i : 0 ..< $literals.size()) {
      string.append($literals[i])
      if (i < $literals.size() - 1) {
        string.append(", ")
      }
    }
    string.append("]")
    return string.toString()
  }

  public AkrantiainToken getIdentifier() {
    return $identifier
  }

  public void setIdentifier(AkrantiainToken identifier) {
    $identifier = identifier
  }

  public List<AkrantiainToken> getLiterals() {
    return $literals
  }

  public void setLiterals(List<AkrantiainToken> literals) {
    $literals = literals
  }

}