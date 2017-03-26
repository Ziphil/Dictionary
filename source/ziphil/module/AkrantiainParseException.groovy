package ziphil.module

import groovy.transform.CompileStatic
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class AkrantiainParseException extends Exception {

  public AkrantiainParseException() {
    super()
  }

  public AkrantiainParseException(String message) {
    super(message)
  }

}