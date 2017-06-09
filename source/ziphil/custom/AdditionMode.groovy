package ziphil.custom

import groovy.transform.CompileStatic
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public enum AdditionMode {

  NORMAL("1要素として"),
  SPLIT_SINGLE("1文字ずつ分割して"),
  SPLIT_COMMA("コンマで分割して")

  private String $string = ""

  private AdditionMode(String string) {
    $string = string
  }

  public String toString() {
    return $string
  }

}