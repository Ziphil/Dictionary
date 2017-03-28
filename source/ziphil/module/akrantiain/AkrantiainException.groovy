package ziphil.module.akrantiain

import groovy.transform.CompileStatic
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class AkrantiainException extends Exception {

  public AkrantiainException() {
    super()
  }

  public AkrantiainException(String message) {
    super(message)
  }

}