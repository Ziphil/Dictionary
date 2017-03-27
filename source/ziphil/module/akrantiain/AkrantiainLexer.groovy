package ziphil.module.akrantiain

import groovy.transform.CompileStatic
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class AkrantiainLexer implements Closeable, AutoCloseable {

  private Reader $reader
  private String $version

  public AkrantiainLexer(Reader reader, String version) {
    $reader = reader
    $version = version
    checkReader()
  }

  private void checkReader() {
    if (!$reader.markSupported()) {
      throw IllegalArgumentException.new("Reader does not support the mark operation")
    }
  }

  public AkrantiainToken nextToken() {
    return null
  }

  private String nextQuoteLiteral() {
    StringBuilder currentContent = StringBuilder.new()
    Boolean isInside = false
    for (Integer codePoint = -1 ; (codePoint = $reader.read()) != -1 ;) {
      if (isInside) {
        if (codePoint == '\\') {
          Integer nextCodePoint = $reader.read()
          if (nextCodePoint == '"' || nextCodePoint == '\\') {
            currentContent.appendCodePoint(nextCodePoint)
          } else {
            throw AkrantiainParseException.new("Invalid escape sequence")
          }
        } else if (codePoint == '"') {
          break
        } else {
          currentContent.appendCodePoint(codePoint)
        }
      } else {
        if (codePoint == '"') {
          isInside = true
        }
      }
    }
    AkrantiainToken token = AkrantiainToken.new(AkrantiainTokenType.QUOTE_LITERAL, currentContent.toString())
    return token
  }

  private String skipBlank() {
    Boolean isInComment = false
    while (true) {
      if (isInComment) {
        Integer codePoint = $reader.read()
        if (codePoint == '\n') {
          isInComment = false
        }
      } else {
        $reader.mark(1)
        Integer codePoint = $reader.read()
        if (codePoint == '#') {
          isInComment = true
        } else if (!AkrantiainLexer.isWhitespace(codePoint)) {
          $reader.reset()
          break
        }
      }
    }
  }

  private Integer awaitingCodePoint() {
    $reader.mark(1)
    Integer appendCodePoint = $reader.read()
    $reader.reset()
    return appendCodePoint
  }

  private static Boolean isWhitespace(Integer codePoint) {
    return Character.isWhitespace(codePoint)
  }

  private static Boolean isLetter(Integer codePoint) {
    return (codePoint >= 48 && codePoint <= 57) || (codePoint >= 65 && codePoint <= 90) || (codePoint >= 97 && codePoint <= 122) || codePoint == 95
  }

  public void close() {
    $reader.close()
  }

}