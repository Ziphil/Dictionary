package ziphil.dictionary

import com.fasterxml.jackson.annotation.JsonProperty
import groovy.transform.CompileStatic


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

  @JsonProperty("form")
  public String getName() {
    return $name
  }

  @JsonProperty("form")
  public void setName(String name) {
    $name = name
  }

}