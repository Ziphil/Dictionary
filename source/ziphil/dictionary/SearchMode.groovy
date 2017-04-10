package ziphil.dictionary

import groovy.transform.CompileStatic
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public enum SearchMode {

  NAME("単語"),
  EQUIVALENT("訳語"),
  CONTENT("全文")

  private String $string = ""

  private SearchMode(String string) {
    $string = string
  }

  public String toString() {
    return $string
  }

}