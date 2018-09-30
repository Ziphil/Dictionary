package ziphil.dictionary

import groovy.transform.CompileStatic
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public enum WordOrderType {

  CUSTOM("辞書の個別設定順"),
  IDENTIFIER("識別子順"),
  UNICODE("Unicode順")

  private String $string = ""

  public WordOrderType(String string) {
    $string = string
  }

  public String toString() {
    return $string
  }

}