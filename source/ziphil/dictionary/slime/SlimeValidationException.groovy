package ziphil.dictionary.slime

import groovy.transform.CompileStatic
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class SlimeValidationException extends Exception {

  public SlimeValidationException() {
    super()
  }

  public SlimeValidationException(String message) {
    super(message)
  }

}