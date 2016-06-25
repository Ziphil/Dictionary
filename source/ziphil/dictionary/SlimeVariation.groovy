package ziphil.dictionary

import groovy.transform.CompileStatic
import net.arnx.jsonic.JSONHint


@CompileStatic @Newify
public class SlimeVariation {

  private String $title = ""
  private String $name = ""

  public SlimeInformation(String title, String name) {
    $title = title
    $name = name
  }

  public String getTitle() {
    return $title
  }

  public void setTitle(String title) {
    $title = title
  }

  @JSONHint(name="getForm")
  public String getName() {
    return $name
  }

  @JSONHint(name="setForm")
  public void setName(String name) {
    $name = name
  }

}