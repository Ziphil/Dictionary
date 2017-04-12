package ziphil.module.akrantiain

import groovy.transform.CompileStatic
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class AkrantiainException extends Exception {

  private String $message = ""
  private String $fullMessage = ""

  public AkrantiainException() {
    super()
  }

  public AkrantiainException(String message, List<AkrantiainElement> elements) {
    super()
    $message = message
    makeFullMessage(elements)
  }

  private void makeFullMessage(List<AkrantiainElement> elements) {
    StringBuilder fullMessage = StringBuilder.new()
    fullMessage.append("Runtime Error: ")
    fullMessage.append($message)
    for (AkrantiainElement element : elements) {
      fullMessage.append("\n  ")
      fullMessage.append(element.getPart())
      fullMessage.append(" (at column ")
      Integer columnNumber = element.getColumnNumber()
      if (columnNumber != null) {
        fullMessage.append(columnNumber)
      } else {
        fullMessage.append("?")
      }
      fullMessage.append(")")
    }
    $fullMessage = fullMessage.toString()
  }

  public String getMessage() {
    return $message
  }

  public String getFullMessage() {
    return $fullMessage
  }

}