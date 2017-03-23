package ziphil.dictionary.shaleia

import groovy.transform.CompileStatic
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class ShaleiaPossibility {

  private String $name
  private String $explanation

  public ShaleiaPossibility(String name, String explanation) {
    $name = name
    $explanation = explanation
  }

  public String getName() {
    return $name
  }

  public void setName(String name) {
    $name = name
  }

  public String getExplanation() {
    return $explanation
  }

  public void setExplanation(String explanation) {
    $explanation = explanation
  }

}