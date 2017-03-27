package ziphil.module.akrantiain

import groovy.transform.CompileStatic
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class AkrantiainLexer implements Closeable, AutoCloseable {

  private Reader $reader
  private String $version
  private Boolean $isFirst = true
  private Boolean $isAfterSemicolon = false

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
    Boolean isNextLine = skipBlank()
    AkrantiainToken token = null
    if ($isFirst || $isAfterSemicolon || !isNextLine) {
      $isFirst = false
      $reader.mark(3)
      Integer codePoint = $reader.read()
      if (codePoint == '"') {
        $reader.reset()
        token = nextStringLiteral('"')
      } else if (codePoint == '/') {
        $reader.reset()
        token = nextStringLiteral('/')
      } else if (codePoint == '@') {
        $reader.reset()
        token = nextEnvironmentLiteral()
      } else if (codePoint == '=') {
        token = AkrantiainToken.new(AkrantiainTokenType.EQUAL, "=")
      } else if (codePoint == '-') {
        Integer nextCodePoint = $reader.read()
        if (nextCodePoint == '>') {
          token = AkrantiainToken.new(AkrantiainTokenType.ARROW, "->")
        } else {
          throw AkrantiainParseException.new("Invalid symbol")
        }
      } else if (codePoint == '|') {
        token = AkrantiainToken.new(AkrantiainTokenType.VERTICAL, "|")
      } else if (codePoint == '^') {
        token = AkrantiainToken.new(AkrantiainTokenType.CIRCUMFLEX, "^")
      } else if (codePoint == '$') {
        token = AkrantiainToken.new(AkrantiainTokenType.DOLLAR, "\$")
      } else if (codePoint == '!') {
        token = AkrantiainToken.new(AkrantiainTokenType.EXCLAMATION, "!")
      } else if (codePoint == '(') {
        token = AkrantiainToken.new(AkrantiainTokenType.OPEN_PAREN, "(")
      } else if (codePoint == ')') {
        token = AkrantiainToken.new(AkrantiainTokenType.CLOSE_PAREN, ")")
      } else if (codePoint == ';') {
        token = AkrantiainToken.new(AkrantiainTokenType.SEMICOLON, ";")
      } else if (AkrantiainLexer.isLetter(codePoint)) {
        $reader.reset()
        token = nextIdentifier()
      } else if (codePoint == -1) {
        if (!$isAfterSemicolon) {
          token = AkrantiainToken.new(AkrantiainTokenType.SEMICOLON, ";")
        } else {
          token = null
        }
      } else {
        throw AkrantiainParseException.new()
      }
    } else {
      token = AkrantiainToken.new(AkrantiainTokenType.SEMICOLON, ";")
    }
    $isAfterSemicolon = token != null && token.getType() == AkrantiainTokenType.SEMICOLON
    return token
  }

  private AkrantiainToken nextIdentifier() {
    StringBuilder currentName = StringBuilder.new()
    while (true) {
      $reader.mark(1)
      Integer codePoint = $reader.read()
      if (AkrantiainLexer.isLetter(codePoint)) {
        currentName.appendCodePoint(codePoint)
      } else {
        $reader.reset()
        break
      }
    }
    AkrantiainToken token = AkrantiainToken.new(AkrantiainTokenType.IDENTIFIER, currentName.toString())
    return token
  }

  private AkrantiainToken nextStringLiteral(String separator) {
    StringBuilder currentContent = StringBuilder.new()
    Boolean isInside = false
    while (true) {
      Integer codePoint = $reader.read()
      if (isInside) {
        if (codePoint == '\\') {
          Integer nextCodePoint = $reader.read()
          if (nextCodePoint == separator || nextCodePoint == '\\') {
            currentContent.appendCodePoint(nextCodePoint)
          } else {
            throw AkrantiainParseException.new("Invalid escape sequence")
          }
        } else if (codePoint == -1) {
          throw AkrantiainParseException.new("The file ended before a string literal is closed")
        } else if (codePoint == separator) {
          break
        } else {
          currentContent.appendCodePoint(codePoint)
        }
      } else {
        if (codePoint == separator) {
          isInside = true
        } else if (codePoint == -1) {
          break
        }
      }
    }
    AkrantiainToken token = null
    if (separator == '"') {
      token = AkrantiainToken.new(AkrantiainTokenType.QUOTE_LITERAL, currentContent.toString())
    } else if (separator == '/') {
      token = AkrantiainToken.new(AkrantiainTokenType.SLASH_LITERAL, currentContent.toString())
    }
    return token
  }

  private AkrantiainToken nextEnvironmentLiteral() {
    StringBuilder currentContent = StringBuilder.new()
    Boolean isInside = false
    while (true) {
      $reader.mark(1)
      Integer codePoint = $reader.read()
      if (isInside) {
        if (AkrantiainLexer.isLetter(codePoint)) {
          currentContent.appendCodePoint(codePoint)
        } else {
          $reader.reset()
          break
        }
      } else {
        if (codePoint == '@') {
          isInside = true
        } else if (codePoint == -1) {
          break
        }
      }
    }
    AkrantiainToken token = AkrantiainToken.new(AkrantiainTokenType.ENVIRONMENT_LITERAL, currentContent.toString())
    return token
  }

  private Boolean skipBlank() {
    Boolean isInComment = false
    Boolean isNextLine = false
    while (true) {
      if (isInComment) {
        Integer codePoint = $reader.read()
        if (codePoint == '\n' || codePoint == -1) {
          isInComment = false
          isNextLine = true
        }
      } else {
        $reader.mark(1)
        Integer codePoint = $reader.read()
        if (codePoint == '#') {
          isInComment = true
        } else if (codePoint == '\n') {
          isNextLine = true
        } else if (!AkrantiainLexer.isWhitespace(codePoint)) {
          $reader.reset()
          break
        }
      }
    }
    return isNextLine
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