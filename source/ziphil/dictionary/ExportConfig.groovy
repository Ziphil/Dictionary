package ziphil.dictionary

import groovy.transform.CompileStatic
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class ExportConfig {

  protected ExportType $type
  protected String $path

  public ExportType getType() {
    return $type
  }

  public void setType(ExportType type) {
    $type = type
  }

  public String getPath() {
    return $path
  }

  public void setPath(String path) {
    $path = path
  }

}