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

  public AkrantiainParseException(String plainMessage, AkrantiainToken token) {
    super()
    makeMessage(plainMessage, token)
    makeFullMessage(plainMessage, token)
  }

  public AkrantiainParseException(String plainMessage, Integer codePoint, Integer lineNumber) {
    super()
    makeMessage(plainMessage, codePoint, lineNumber)
    makeFullMessage(plainMessage, codePoint, lineNumber)
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

  private void makeMessage(String plainMessage, Integer codePoint, Integer lineNumber) {
    StringBuilder message = StringBuilder.new()
    message.append(plainMessage)
    message.append(" (at line ")
    if (lineNumber != null) {
      message.append(lineNumber)
    } else {
      message.append("?")
    }
    message.append(")")
    $message = message.toString()
  }

  private void makeFullMessage(String plainMessage, AkrantiainToken token) {
    StringBuilder fullMessage = StringBuilder.new()
    fullMessage.append("Parse Error: ")
    fullMessage.append(plainMessage)
    fullMessage.append("\n\t")
    if (token != null) {
      fullMessage.append("\"")
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

  private void makeFullMessage(String plainMessage, Integer codePoint, Integer lineNumber) {
    StringBuilder fullMessage = StringBuilder.new()
    fullMessage.append("Parse Error: ")
    fullMessage.append(plainMessage)
    fullMessage.append("\n\t")
    if (codePoint != null) {
      fullMessage.append("\"")
      fullMessage.appendCodePoint(codePoint)
      fullMessage.append("\" (at line ")
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

  public String getMessage() {
    return $message
  }

  public String getFullMessage() {
    return $fullMessage
  }

}