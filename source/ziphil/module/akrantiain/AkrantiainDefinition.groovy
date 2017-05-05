package ziphil.module.akrantiain

import groovy.transform.CompileStatic
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class AkrantiainDefinition {

  private AkrantiainToken $identifier
  private AkrantiainMatchable $content

  public AkrantiainToken findUnknownIdentifier(AkrantiainModule module) {
    return $content.findUnknownIdentifier(module)
  } 

  public AkrantiainToken findCircularIdentifier(List<AkrantiainToken> identifiers, AkrantiainModule module) {
    ArrayList nextIdentifiers = ArrayList.new(identifiers)
    nextIdentifiers.add($identifier)
    return $content.findCircularIdentifier(nextIdentifiers, module)
  }

  public AkrantiainToken findCircularIdentifier(AkrantiainModule module) {
    ArrayList identifiers = ArrayList.new()
    identifiers.add($identifier)
    return $content.findCircularIdentifier(identifiers, module)
  }

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

  public AkrantiainMatchable getContent() {
    return $content
  }

  public void setContent(AkrantiainMatchable content) {
    $content = content
  }

}