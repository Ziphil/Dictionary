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

  public AkrantiainException(String message, AkrantiainToken token) {
    super()
    $message = message
    makeFullMessage(message, token)
  }

  public AkrantiainException(String message, List<AkrantiainElement> elements) {
    super()
    $message = message
    makeFullMessage(message, elements)
  }

  private void makeFullMessage(String message, AkrantiainToken token) {
    StringBuilder fullMessage = StringBuilder.new()
    fullMessage.append("AkrantiainParseException: ")
    fullMessage.append(message)
    fullMessage.append("\n")
    if (token != null) {
      fullMessage.append("  \"")
      fullMessage.append(token.getText())
      fullMessage.append("\" (at line ")
      Integer lineNumber = token.getLineNumber()
      if (lineNumber != null) {
        fullMessage.append(lineNumber)
      } else {
        fullMessage.append("?")
      }
    } else {
      fullMessage.append("? (at line ?")
    }
    fullMessage.append(")")
    $fullMessage = fullMessage.toString()
  }

  private void makeFullMessage(String message, List<AkrantiainElement> elements) {
    StringBuilder fullMessage = StringBuilder.new()
    fullMessage.append("AkrantiainException: ")
    fullMessage.append(message)
    fullMessage.append("\n")
    for (AkrantiainElement element : elements) {
      fullMessage.append("  \"")
      fullMessage.append(element.getPart())
      fullMessage.append("\" (at column ")
      Integer columnNumber = element.getColumnNumber()
      if (columnNumber != null) {
        fullMessage.append(columnNumber)
      } else {
        fullMessage.append("?")
      }
      fullMessage.append(")\n")
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