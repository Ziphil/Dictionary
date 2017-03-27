package ziphil.module.akrantiain

import groovy.transform.CompileStatic
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class AkrantiainToken {

  private AkrantiainTokenType $type
  private String $text

  public AkrantiainToken(AkrantiainTokenType type, String text) {
    $type = type
    $text = text
  }

  public String toString() {
    return "<${$type}: \"${$text}\">"
  }

  public AkrantiainTokenType getType() {
    return $type
  }

  public String getText() {
    return $text
  }

}