package ziphil.dictionary.shaleia

import groovy.transform.CompileStatic


@CompileStatic @Newify
public class ShaleiaPossibility {

  private String $name
  private String $possibilityName

  public ShaleiaPossibility(String name, String possibilityName) {
    $name = name
    $possibilityName = possibilityName
  }

  public String getName() {
    return $name
  }

  public void setName(String name) {
    $name = name
  }

  public String getPossibilityName() {
    return $possibilityName
  }

  public void setPossibilityName(String possibilityName) {
    $possibilityName = possibilityName
  }

}