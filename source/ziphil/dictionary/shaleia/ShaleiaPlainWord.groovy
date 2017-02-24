package ziphil.dictionary.shaleia

import groovy.transform.CompileStatic
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class ShaleiaPlainWord {

  private String $name = ""
  private String $uniqueName = ""
  private String $data = ""

  public String getName() {
    return $name
  }

  public void setName(String name) {
    $name = name
  }

  public String getUniqueName() {
    return $uniqueName
  }

  public void setUniqueName(String uniqueName) {
    $uniqueName = uniqueName
  }

  public String getData() {
    return $data
  }

  public void setData(String data) {
    $data = data
  }

}