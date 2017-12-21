package ziphil.dictionary

import groovy.transform.CompileStatic
import ziphil.custom.ExtensionFilter
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public enum ExportType {

  PDF("PDF", "pdf"),
  HTML("HTML", "html")

  private String $name
  private String $extension

  private ExportType(String name, String extension) {
    $name = name
    $extension = extension
  }

  public ExtensionFilter createExtensionFilter() {
    ExtensionFilter extensionFilter = ExtensionFilter.new($name, $extension)
    return extensionFilter
  }

  public String getName() {
    return $name
  }

}