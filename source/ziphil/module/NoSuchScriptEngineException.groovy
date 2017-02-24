package ziphil.module

import groovy.transform.CompileStatic
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class NoSuchScriptEngineException extends RuntimeException {

  public NoSuchScriptEngineException(String message) {
    super(message)
  }

}