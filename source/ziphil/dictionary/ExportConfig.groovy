package ziphil.dictionary

import groovy.transform.CompileStatic
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class ExportConfig {

  protected String $path

  public String getPath() {
    return $path
  }

  public void setPath(String path) {
    $path = path
  }

}