package ziphil.module.akrantiain

import groovy.transform.CompileStatic
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class AkrantiainParseException extends Exception {

  private String $plainMessage = ""
  private Integer $lineNumber

  public AkrantiainParseException() {
    super()
  }

  public AkrantiainParseException(String plainMessage, Integer lineNumber) {
    super()
    $plainMessage = plainMessage
    $lineNumber = lineNumber
  }

  public String getMessage() {
    return "${$lineNumber}: ${$plainMessage}"
  }

}