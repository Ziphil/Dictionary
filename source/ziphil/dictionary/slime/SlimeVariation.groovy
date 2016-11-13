package ziphil.dictionary.slime

import groovy.transform.CompileStatic


@CompileStatic @Newify
public class SlimeVariation {

  private String $title = ""
  private String $name = ""

  public SlimeVariation(String title, String name) {
    $title = title
    $name = name
  }

  public SlimeVariation() {
  }

  public String getTitle() {
    return $title
  }

  public void setTitle(String title) {
    $title = title
  }

  public String getName() {
    return $name
  }

  public void setName(String name) {
    $name = name
  }

}