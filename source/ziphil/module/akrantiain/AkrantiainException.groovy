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

  public AkrantiainException(String plainMessage, AkrantiainToken token) {
    super()
    makeMessage(plainMessage, token)
    makeFullMessage(plainMessage, token)
  }

  public AkrantiainException(String plainMessage, List<AkrantiainElement> elements) {
    super()
    makeMessage(plainMessage, elements)
    makeFullMessage(plainMessage, elements)
  }

  private void makeMessage(String plainMessage, AkrantiainToken token) {
    StringBuilder message = StringBuilder.new()
    message.append(plainMessage)
    message.append(" (at line ")
    if (token != null) {
      Integer lineNumber = token.getLineNumber()
      if (lineNumber != null) {
        message.append(lineNumber)
      } else {
        message.append("?")
      }
    } else {
      message.append("?")
    }
    message.append(")")
    $message = message.toString()
  }

  private void makeMessage(String plainMessage, List<AkrantiainElement> elements) {
    StringBuilder message = StringBuilder.new()
    message.append(plainMessage)
    $message = message.toString()
  }

  private void makeFullMessage(String plainMessage, AkrantiainToken token) {
    StringBuilder fullMessage = StringBuilder.new()
    fullMessage.append("AkrantiainParseException: ")
    fullMessage.append(plainMessage)
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

  private void makeFullMessage(String plainMessage, List<AkrantiainElement> elements) {
    StringBuilder fullMessage = StringBuilder.new()
    fullMessage.append("AkrantiainException: ")
    fullMessage.append(plainMessage)
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