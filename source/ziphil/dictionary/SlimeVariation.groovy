package ziphil.dictionary

import groovy.transform.CompileStatic
import net.arnx.jsonic.JSONHint


@CompileStatic @Newify
public class SlimeVariation {

  private String $title = ""
  private String $name = ""

  public SlimeVariation() {
  }

  public SlimeVariation(String title, String name) {
    $title = title
    $name = name
  }

  public String getTitle() {
    return $title
  }

  public void setTitle(String title) {
    $title = title
  }

  @JSONHint(name="form")
  public String getName() {
    return $name
  }

  @JSONHint(name="form")
  public void setName(String name) {
    $name = name
  }

}