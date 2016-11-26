package ziphil.dictionary.slime

import groovy.transform.CompileStatic
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class SlimeEquivalent {

  private String $title = ""
  private List<String> $names = ArrayList.new()

  public SlimeEquivalent(String title, List<String> names) {
    $title = title
    $names = names
  }

  public SlimeEquivalent() {
  }

  public String getTitle() {
    return $title
  }

  public void setTitle(String title) {
    $title = title
  }

  public List<String> getNames() {
    return $names
  }

  public void setNames(List<String> names) {
    $names = names
  }

}