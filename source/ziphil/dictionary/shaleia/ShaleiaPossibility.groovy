package ziphil.dictionary.shaleia

import groovy.transform.CompileStatic
import ziphil.dictionary.NormalSearchParameter
import ziphil.dictionary.Possibility
import ziphil.dictionary.SearchParameter
import ziphil.dictionary.SearchMode
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class ShaleiaPossibility implements Possibility {

  private String $name
  private String $explanation

  public ShaleiaPossibility(String name, String explanation) {
    $name = name
    $explanation = explanation
  }

  public SearchParameter createParameter() {
    return NormalSearchParameter.new($name, SearchMode.NAME, true, true)
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