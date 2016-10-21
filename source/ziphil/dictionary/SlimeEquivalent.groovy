package ziphil.dictionary

import com.fasterxml.jackson.annotation.JsonProperty
import groovy.transform.CompileStatic


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

  @JsonProperty("form")
  public String setName(String name) {
    $names.add(name)
  }

  @JsonProperty("forms")
  public List<String> getNames() {
    return $names
  }

  @JsonProperty("forms")
  public void setNames(List<String> names) {
    $names = names
  }

}