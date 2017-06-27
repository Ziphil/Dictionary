package ziphil.module.akrantiain

import groovy.transform.CompileStatic
import ziphil.module.ExtendedBufferedReader
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class AkrantiainLexer implements Closeable, AutoCloseable {

  private ExtendedBufferedReader $reader
  private Boolean $first = true
  private Boolean $afterSemicolon = false

  public AkrantiainLexer(Reader reader) {
    $reader = ExtendedBufferedReader.new(reader)
  }

  // 次のトークンを取得します。
  // ファイルの終わりに達した場合は null を返します。
  // スペースとコメントは無視されます。
  // もともとの入力で改行前のセミコロンが省略されている場合でも、自動的にセミコロンを補って動作します。
  public AkrantiainToken nextToken() {
    Boolean nextLine = skipBlank()
    AkrantiainToken token = null
    if ($first || $afterSemicolon || !nextLine) {
      $first = false
      $reader.mark(3)
      Int codePoint = $reader.read()
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
        $reader.mark(1)
        Int nextCodePoint = $reader.read()
        if (nextCodePoint == '>') {
          token = AkrantiainToken.new(AkrantiainTokenType.BOLD_ARROW, "=>", $reader)
        } else {
          $reader.reset()
          token = AkrantiainToken.new(AkrantiainTokenType.EQUAL, "=", $reader)
        }
      } else if (codePoint == '-') {
        Int nextCodePoint = $reader.read()
        if (nextCodePoint == '>') {
          token = AkrantiainToken.new(AkrantiainTokenType.ARROW, "->", $reader)
        } else {
          throw AkrantiainParseException.new("Invalid symbol", nextCodePoint, $reader)
        }
      } else if (codePoint == '>') {
        Int nextCodePoint = $reader.read()
        if (nextCodePoint == '>') {
          token = AkrantiainToken.new(AkrantiainTokenType.ADVANCE, ">>", $reader)
        } else {
          throw AkrantiainParseException.new("Invalid symbol", nextCodePoint, $reader)
        }
      } else if (codePoint == '|') {
        token = AkrantiainToken.new(AkrantiainTokenType.VERTICAL, "|", $reader)
      } else if (codePoint == '^') {
        token = AkrantiainToken.new(AkrantiainTokenType.CIRCUMFLEX, "^", $reader)
      } else if (codePoint == '$') {
        token = AkrantiainToken.new(AkrantiainTokenType.DOLLAR, "\$", $reader)
      } else if (codePoint == '!') {
        token = AkrantiainToken.new(AkrantiainTokenType.EXCLAMATION, "!", $reader)
      } else if (codePoint == '%') {
        $reader.mark(1)
        Int nextCodePoint = $reader.read()
        if (nextCodePoint == '%') {
          token = AkrantiainToken.new(AkrantiainTokenType.DOUBLE_PERCENT, "%%", $reader)
        } else {
          $reader.reset()
          token = AkrantiainToken.new(AkrantiainTokenType.PERCENT, "%", $reader)
        }
      } else if (codePoint == '(') {
        token = AkrantiainToken.new(AkrantiainTokenType.OPEN_PAREN, "(", $reader)
      } else if (codePoint == ')') {
        token = AkrantiainToken.new(AkrantiainTokenType.CLOSE_PAREN, ")", $reader)
      } else if (codePoint == '{') {
        token = AkrantiainToken.new(AkrantiainTokenType.OPEN_CURLY, "{", $reader)
      } else if (codePoint == '}') {
        token = AkrantiainToken.new(AkrantiainTokenType.CLOSE_CURLY, "}", $reader)
      } else if (codePoint == ';') {
        token = AkrantiainToken.new(AkrantiainTokenType.SEMICOLON, ";", $reader)
      } else if (AkrantiainLexer.isNumeric(codePoint)) {
        $reader.reset()
        token = nextNumeric()
      } else if (AkrantiainLexer.isLetter(codePoint)) {
        $reader.reset()
        token = nextIdentifier()
      } else if (codePoint == -1) {
        if (!$afterSemicolon) {
          token = AkrantiainToken.new(AkrantiainTokenType.SEMICOLON, "", $reader)
        } else {
          token = null
        }
      } else {
        throw AkrantiainParseException.new("Invalid symbol", codePoint, $reader)
      }
    } else {
      token = AkrantiainToken.new(AkrantiainTokenType.SEMICOLON, "", $reader)
    }
    $afterSemicolon = false 
    if (token != null) {
      AkrantiainTokenType tokenType = token.getType()
      if (tokenType == AkrantiainTokenType.SEMICOLON || tokenType == AkrantiainTokenType.OPEN_CURLY || tokenType == AkrantiainTokenType.CLOSE_CURLY) {
        $afterSemicolon = true
      }
    }
    return token
  }

  private AkrantiainToken nextIdentifier() {
    StringBuilder currentName = StringBuilder.new()
    while (true) {
      $reader.mark(1)
      Int codePoint = $reader.read()
      if (AkrantiainLexer.isLetter(codePoint)) {
        currentName.appendCodePoint(codePoint)
      } else {
        $reader.reset()
        break
      }
    }
    AkrantiainToken token = AkrantiainToken.new(AkrantiainTokenType.IDENTIFIER, currentName.toString(), $reader)
    return token
  }

  private AkrantiainToken nextNumeric() {
    StringBuilder currentNumber = StringBuilder.new()
    while (true) {
      $reader.mark(1)
      Int codePoint = $reader.read()
      if (AkrantiainLexer.isLetter(codePoint)) {
        if (AkrantiainLexer.isNumeric(codePoint)) {
          currentNumber.appendCodePoint(codePoint)
        } else {
          throw AkrantiainParseException.new("Identifier starts with numeric", codePoint, $reader)
        }
      } else {
        $reader.reset()
        break
      }
    }
    AkrantiainToken token = AkrantiainToken.new(AkrantiainTokenType.NUMERIC, currentNumber.toString(), $reader)
    return token
  }

  private AkrantiainToken nextStringLiteral(String separator) {
    StringBuilder currentContent = StringBuilder.new()
    Boolean inside = false
    while (true) {
      Int codePoint = $reader.read()
      if (inside) {
        if (codePoint == '\\') {
          Int nextCodePoint = $reader.read()
          if (nextCodePoint == separator || nextCodePoint == '\\') {
            currentContent.appendCodePoint(nextCodePoint)
          } else if (nextCodePoint == 'u') {
            StringBuilder escapeCodePointString = StringBuilder.new()
            for (Int i = 0 ; i < 4 ; i ++) {
              Int escapeCodePoint = $reader.read()
              if (AkrantiainLexer.isHex(escapeCodePoint)) {
                escapeCodePointString.appendCodePoint(escapeCodePoint)
              } else {
                throw AkrantiainParseException.new("Invalid escape sequence", codePoint, $reader)
              }
            }
            currentContent.appendCodePoint(IntegerClass.parseInt(escapeCodePointString.toString(), 16))
          } else {
            throw AkrantiainParseException.new("Invalid escape sequence", codePoint, $reader)
          }
        } else if (codePoint == '\n') {
          throw AkrantiainParseException.new("The line ended before a string literal is closed", -1, $reader)
        } else if (codePoint == -1) {
          throw AkrantiainParseException.new("The line ended before a string literal is closed", -1, $reader)
        } else if (codePoint == separator) {
          break
        } else {
          currentContent.appendCodePoint(codePoint)
        }
      } else {
        if (codePoint == separator) {
          inside = true
        } else if (codePoint == -1) {
          break
        }
      }
    }
    AkrantiainToken token = null
    if (separator == '"') {
      token = AkrantiainToken.new(AkrantiainTokenType.QUOTE_LITERAL, currentContent.toString(), separator + currentContent.toString() + separator, $reader)
    } else if (separator == '/') {
      token = AkrantiainToken.new(AkrantiainTokenType.SLASH_LITERAL, currentContent.toString(), separator + currentContent.toString() + separator, $reader)
    }
    return token
  }

  private AkrantiainToken nextEnvironmentLiteral() {
    StringBuilder currentContent = StringBuilder.new()
    Boolean inside = false
    while (true) {
      $reader.mark(1)
      Int codePoint = $reader.read()
      if (inside) {
        if (AkrantiainLexer.isLetter(codePoint)) {
          currentContent.appendCodePoint(codePoint)
        } else {
          $reader.reset()
          break
        }
      } else {
        if (codePoint == '@') {
          inside = true
        } else if (codePoint == -1) {
          break
        }
      }
    }
    AkrantiainToken token = AkrantiainToken.new(AkrantiainTokenType.ENVIRONMENT_LITERAL, currentContent.toString(), "@" + currentContent.toString(), $reader)
    return token
  }

  private Boolean skipBlank() {
    Boolean inComment = false
    Boolean nextLine = false
    while (true) {
      if (inComment) {
        Int codePoint = $reader.read()
        if (codePoint == '\n' || codePoint == -1) {
          inComment = false
          nextLine = true
        }
      } else {
        $reader.mark(2)
        Int codePoint = $reader.read()
        if (codePoint == '#') {
          inComment = true
        } else if (codePoint == '\n') {
          nextLine = true
        } else if (!AkrantiainLexer.isWhitespace(codePoint)) {
          $reader.reset()
          break
        }
      }
    }
    return nextLine
  }

  public static Boolean isWhitespace(Int codePoint) {
    return CharacterClass.isWhitespace(codePoint)
  }

  public static Boolean isAllWhitespace(String string) {
    for (Int i = 0 ; i < string.length() ; i ++) {
      if (!CharacterClass.isWhitespace(string.charAt(i))) {
        return false
      }
    }
    return true
  }

  public static Boolean isNumeric(Int codePoint) {
    return codePoint >= 48 && codePoint <= 57
  }

  public static Boolean isLetter(Int codePoint) {
    return (codePoint >= 48 && codePoint <= 57) || (codePoint >= 65 && codePoint <= 90) || (codePoint >= 97 && codePoint <= 122) || codePoint == 95
  }

  public static Boolean isHex(Int codePoint) {
    return (codePoint >= 48 && codePoint <= 57) || (codePoint >= 65 && codePoint <= 70) || (codePoint >= 97 && codePoint <= 102)
  }

  public void close() {
    $reader.close()
  }

}