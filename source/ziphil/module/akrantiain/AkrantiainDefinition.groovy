package ziphil.module.akrantiain

import groovy.transform.CompileStatic
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class AkrantiainDefinition {

  private AkrantiainToken $identifier
  private AkrantiainDisjunctionGroup $content

  public String toString() {
    StringBuilder string = StringBuilder.new()
    string.append($identifier)
    string.append(" = ")
    string.append($content)
    return string.toString()
  }

  public AkrantiainToken getIdentifier() {
    return $identifier
  }

  public void setIdentifier(AkrantiainToken identifier) {
    $identifier = identifier
  }

  public AkrantiainDisjunctionGroup getContent() {
    return $content
  }

  public void setContent(AkrantiainDisjunctionGroup content) {
    $content = content
  }

}