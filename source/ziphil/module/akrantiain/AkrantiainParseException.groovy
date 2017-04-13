package ziphil.module.akrantiain

import groovy.transform.CompileStatic
import ziphil.module.ExtendedBufferedReader
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class AkrantiainParseException extends Exception {

  private String $message = ""
  private String $fullMessage = ""

  public AkrantiainParseException() {
    super()
  }

  public AkrantiainParseException(String message) {
    super()
    $message = message
    makeFullMessage((AkrantiainToken)null)
  }

  public AkrantiainParseException(String message, AkrantiainToken token) {
    super()
    $message = message
    makeFullMessage(token)
  }

  public AkrantiainParseException(String message, List<AkrantiainToken> tokens) {
    super()
    $message = message
    makeFullMessage(tokens)
  }

  public AkrantiainParseException(String message, Integer codePoint, ExtendedBufferedReader reader) {
    super()
    $message = message
    makeFullMessage(codePoint, reader)
  }

  private void makeFullMessage(AkrantiainToken token) {
    StringBuilder fullMessage = StringBuilder.new()
    fullMessage.append("Parse Error: ")
    fullMessage.append($message)
    if (token != null) {
      fullMessage.append("\n  ")
      fullMessage.append(token.getFullText())
      fullMessage.append(" (at line ")
      Integer lineNumber = token.getLineNumber()
      Integer columnNumber = token.getColumnNumber()
      if (lineNumber != null) {
        fullMessage.append(lineNumber)
      } else {
        fullMessage.append("?")
      }
      fullMessage.append(" column ")
      if (columnNumber != null) {
        fullMessage.append(columnNumber)
      } else {
        fullMessage.append("?")
      }
      fullMessage.append(")")
    }
    $fullMessage = fullMessage.toString()
  }

  private void makeFullMessage(List<AkrantiainToken> tokens) {
    StringBuilder fullMessage = StringBuilder.new()
    fullMessage.append("Parse Error: ")
    fullMessage.append($message)
    if (!tokens.isEmpty()) {
      fullMessage.append("\n  ")
      for (AkrantiainToken token : tokens) {
        fullMessage.append(token.getFullText())
      }
      fullMessage.append(" (at line ")
      Integer lineNumber = tokens.last().getLineNumber()
      Integer columnNumber = tokens.last().getColumnNumber()
      if (lineNumber != null) {
        fullMessage.append(lineNumber)
      } else {
        fullMessage.append("?")
      }
      fullMessage.append(" column ")
      if (columnNumber != null) {
        fullMessage.append(columnNumber)
      } else {
        fullMessage.append("?")
      }
      fullMessage.append(")")
    }
    $fullMessage = fullMessage.toString()
  }

  private void makeFullMessage(Integer codePoint, ExtendedBufferedReader reader) {
    StringBuilder fullMessage = StringBuilder.new()
    fullMessage.append("Parse Error: ")
    fullMessage.append($message)
    if (codePoint != null) {
      fullMessage.append("\n  ")
      fullMessage.appendCodePoint(codePoint)
    }
    if (reader != null) {
      if (codePoint == null) {
        fullMessage.append("\n  ")
      }
      fullMessage.append(" (at line ")
      Integer lineNumber = reader.getLineNumber()
      Integer columnNumber = reader.getColumnNumber()
      if (lineNumber != null) {
        fullMessage.append(lineNumber)
      } else {
        fullMessage.append("?")
      }
      fullMessage.append(" column ")
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