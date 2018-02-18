package ziphil.dictionary.exporter

import groovy.transform.CompileStatic
import ziphil.dictionary.ExportConfig
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class PdfExportConfig extends ExportConfig {

  protected String $externalCommand = null

  public String getExternalCommand() {
    return $externalCommand
  }

  public void setExternalCommand(String externalCommand) {
    $externalCommand = externalCommand
  }

}