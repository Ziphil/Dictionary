package ziphil.module.zatlin

import groovy.transform.CompileStatic
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class ZatlinDefinition {

  private ZatlinToken $identifier
  private ZatlinGeneratable $content

  public String toString() {
    StringBuilder string = StringBuilder.new()
    string.append($identifier)
    string.append(" = ")
    string.append($content)
    return string.toString()
  }

  public ZatlinToken getIdentifier() {
    return $identifier
  }

  public void setIdentifier(ZatlinToken identifier) {
    $identifier = identifier
  }

  public ZatlinGeneratable getContent() {
    return $content
  }

  public void setContent(ZatlinGeneratable content) {
    $content = content
  }

}