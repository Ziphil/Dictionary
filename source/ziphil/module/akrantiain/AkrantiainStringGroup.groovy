package ziphil.module.akrantiain

import groovy.transform.CompileStatic
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class AkrantiainStringGroup {

  private List<String> $strings = ArrayList.new()

  public AkrantiainStringGroup(List<String> strings) {
    $strings = strings
  }

}