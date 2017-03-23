package ziphil.module

import groovy.transform.CompileStatic
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public enum HahCompressionType {

  NORMAL("通常"), RANDOM("ランダム型"), SORT("ソート型")

  private String $string = ""

  private HahCompressionType(String string) {
    $string = string
  }

  public String toString() {
    return $string
  }

}