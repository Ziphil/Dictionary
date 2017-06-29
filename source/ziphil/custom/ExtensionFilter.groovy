package ziphil.custom

import groovy.transform.CompileStatic
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class ExtensionFilter {

  private String $name
  private String $extension

  public ExtensionFilter(String name, String extension) {
    $name = name
    $extension = extension
  }

  public Boolean accepts(File file) {
    if ($extension != null) {
      return file.getName().endsWith("." + $extension)
    } else {
      return true
    }
  }

  public String toString() {
    if ($extension != null) {
      return "${$name} (*.${$extension})"
    } else {
      return $name
    }
  }

  public String getName() {
    return $name
  }

  public void setName(String name) {
    $name = name
  }

  public String getExtension() {
    return $extension
  }

  public void setExtension(String extension) {
    $extension = extension
  }

}