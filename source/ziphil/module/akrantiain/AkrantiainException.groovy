package ziphil.module.akrantiain

import groovy.transform.CompileStatic
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class AkrantiainException extends Exception {

  private String $plainMessage = ""

  public AkrantiainException() {
    super()
  }

  public AkrantiainException(String plainMessage) {
    super()
    $plainMessage = plainMessage
  }

  public String getMessage() {
    return $plainMessage
  }

}