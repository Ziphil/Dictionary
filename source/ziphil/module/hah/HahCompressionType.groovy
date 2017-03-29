package ziphil.module.hah

import groovy.transform.CompileStatic
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public enum HahCompressionType {

  NORMAL("通常"), RANDOM("完全ランダム"), RANDOM_SORT("ランダム→ソート"), SORT("辞書順ソート")

  private String $string = ""

  private HahCompressionType(String string) {
    $string = string
  }

  public String toString() {
    return $string
  }

}