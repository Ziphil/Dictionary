package ziphil.dictionary

import groovy.transform.CompileStatic
import net.arnx.jsonic.JSONHint


@CompileStatic @Newify
public class SlimeEquivalent {

  private String $title = ""
  private List<String> $names = ArrayList.new()

  public SlimeEquivalent() {
  }

  public SlimeEquivalent(String title, List<String> names) {
    $title = title
    $names = names
  }

  public String getTitle() {
    return $title
  }

  public void setTitle(String title) {
    $title = title
  }

  @JSONHint(name="form")
  public String setName(String name) {
    $names.add(name)
  }

  @JSONHint(name="forms")
  public List<String> getNames() {
    return $names
  }

  @JSONHint(name="forms")
  public void setNames(List<String> names) {
    $names = names
  }

}