package ziphil.module.zatlin

import groovy.transform.CompileStatic
import ziphil.module.ExtendedBufferedReader
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class ZatlinParseException extends Exception {

  private String $message = ""
  private String $fullMessage = ""

  public ZatlinParseException() {
    super()
  }

  public ZatlinParseException(String message) {
    super()
    $message = message
    makeFullMessage((ZatlinToken)null)
  }

  public ZatlinParseException(String message, ZatlinToken token) {
    super()
    $message = message
    makeFullMessage(token)
  }

  public ZatlinParseException(String message, List<ZatlinToken> tokens) {
    super()
    $message = message
    makeFullMessage(tokens)
  }

  public ZatlinParseException(String message, Int codePoint, ExtendedBufferedReader reader) {
    super()
    $message = message
    makeFullMessage(codePoint, reader)
  }

  private void makeFullMessage(ZatlinToken token) {
    StringBuilder fullMessage = StringBuilder.new()
    fullMessage.append("Parse Error: ")
    fullMessage.append($message)
    if (token != null) {
      fullMessage.append("\n  ")
      fullMessage.append(token.getFullText())
      fullMessage.append(" (at line ")
      Int lineNumber = token.getLineNumber()
      Int columnNumber = token.getColumnNumber()
      if (lineNumber >= 0) {
        fullMessage.append(lineNumber)
      } else {
        fullMessage.append("?")
      }
      fullMessage.append(" column ")
      if (columnNumber >= 0) {
        fullMessage.append(columnNumber)
      } else {
        fullMessage.append("?")
      }
      fullMessage.append(")")
    }
    $fullMessage = fullMessage.toString()
  }

  private void makeFullMessage(List<ZatlinToken> tokens) {
    StringBuilder fullMessage = StringBuilder.new()
    fullMessage.append("Parse Error: ")
    fullMessage.append($message)
    if (!tokens.isEmpty()) {
      fullMessage.append("\n  ")
      for (ZatlinToken token : tokens) {
        fullMessage.append(token.getFullText())
      }
      fullMessage.append(" (at line ")
      Int lineNumber = tokens.last().getLineNumber()
      Int columnNumber = tokens.last().getColumnNumber()
      if (lineNumber >= 0) {
        fullMessage.append(lineNumber)
      } else {
        fullMessage.append("?")
      }
      fullMessage.append(" column ")
      if (columnNumber >= 0) {
        fullMessage.append(columnNumber)
      } else {
        fullMessage.append("?")
      }
      fullMessage.append(")")
    }
    $fullMessage = fullMessage.toString()
  }

  private void makeFullMessage(Int codePoint, ExtendedBufferedReader reader) {
    StringBuilder fullMessage = StringBuilder.new()
    fullMessage.append("Parse Error: ")
    fullMessage.append($message)
    if (codePoint >= 0) {
      fullMessage.append("\n  ")
      fullMessage.appendCodePoint(codePoint)
    }
    if (reader != null) {
      if (codePoint == null) {
        fullMessage.append("\n  ")
      }
      fullMessage.append(" (at line ")
      Int lineNumber = reader.getLineNumber()
      Int columnNumber = reader.getColumnNumber()
      if (lineNumber >= 0) {
        fullMessage.append(lineNumber)
      } else {
        fullMessage.append("?")
      }
      fullMessage.append(" column ")
      if (columnNumber >= 0) {
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