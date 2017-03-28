package ziphil.module.akrantiain

import groovy.transform.CompileStatic
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class AkrantiainDefinition {

  private AkrantiainToken $identifier
  private AkrantiainDisjunctionGroup $right

  public String toString() {
    StringBuilder string = StringBuilder.new()
    string.append($identifier)
    string.append(" = ")
    string.append($right)
    return string.toString()
  }

  public AkrantiainToken getIdentifier() {
    return $identifier
  }

  public void setIdentifier(AkrantiainToken identifier) {
    $identifier = identifier
  }

  public AkrantiainDisjunctionGroup getRight() {
    return $right
  }

  public void setRight(AkrantiainDisjunctionGroup right) {
    $right = right
  }

}