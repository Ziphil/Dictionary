package ziphil.module

import groovy.transform.CompileStatic
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public enum FontRenderingType {

  DEFAULT_LCD("デフォルト＋LCD"), DEFAULT_GRAY("デフォルト＋グレースケール"), PRISM_LCD("T2K＋LCD"), PRISM_GRAY("T2K＋グレースケール")

  private String $string = ""

  private FontRenderingType(String string) {
    $string = string
  }

  public String toString() {
    return $string
  }

}