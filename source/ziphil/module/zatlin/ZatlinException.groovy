package ziphil.module.zatlin

import groovy.transform.CompileStatic
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class ZatlinException extends Exception {

  private String $message = ""
  private String $fullMessage = ""

  public ZatlinException() {
    super()
  }

  public ZatlinException(String message) {
    super()
    $message = message
    makeFullMessage()
  }

  private void makeFullMessage() {
    StringBuilder fullMessage = StringBuilder.new()
    fullMessage.append("Runtime Error: ")
    fullMessage.append($message)
    $fullMessage = fullMessage.toString()
  }

  public String getMessage() {
    return $message
  }

  public String getFullMessage() {
    return $fullMessage
  }

}