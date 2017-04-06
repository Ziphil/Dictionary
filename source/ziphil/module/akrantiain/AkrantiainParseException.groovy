package ziphil.module.akrantiain

import groovy.transform.CompileStatic
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class AkrantiainParseException extends Exception {

  private String $message = ""
  private String $fullMessage = ""

  public AkrantiainParseException() {
    super()
  }

  public AkrantiainParseException(String message, AkrantiainToken token) {
    super()
    $message = message
    makeFullMessage(message, token)
  }

  public AkrantiainParseException(String message, Integer codePoint, Integer lineNumber) {
    super()
    $message = message
    makeFullMessage(message, codePoint, lineNumber)
  }

  private void makeFullMessage(String message, AkrantiainToken token) {
    StringBuilder fullMessage = StringBuilder.new()
    fullMessage.append("AkrantiainParseException: ")
    fullMessage.append(message)
    if (token != null) {
      fullMessage.append("\n")
      fullMessage.append("  \"")
      fullMessage.append(token.getText())
      fullMessage.append("\" (at line ")
      Integer lineNumber = token.getLineNumber()
      if (lineNumber != null) {
        fullMessage.append(lineNumber)
      } else {
        fullMessage.append("?")
      }
      fullMessage.append(")")
    }
    $fullMessage = fullMessage.toString()
  }

  private void makeFullMessage(String message, Integer codePoint, Integer lineNumber) {
    StringBuilder fullMessage = StringBuilder.new()
    fullMessage.append("AkrantiainParseException: ")
    fullMessage.append(message)
    if (codePoint != null) {
      fullMessage.append("\n")
      fullMessage.append("  \"")
      fullMessage.appendCodePoint(codePoint)
      fullMessage.append("\" (at line ")
      if (lineNumber != null) {
        fullMessage.append(lineNumber)
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