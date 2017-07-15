package ziphil.module.zatlin

import groovy.transform.CompileStatic
import ziphil.module.ExtendedBufferedReader
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class ZatlinLexer implements Closeable, AutoCloseable {

  private ExtendedBufferedReader $reader
  private Boolean $first = true
  private Boolean $afterSemicolon = false

  public ZatlinLexer(Reader reader) {
    $reader = ExtendedBufferedReader.new(reader)
  }

  // 次のトークンを取得します。
  // ファイルの終わりに達した場合は null を返します。
  // スペースとコメントは無視されます。
  // もともとの入力で改行前のセミコロンが省略されている場合でも、自動的にセミコロンを補って動作します。
  public ZatlinToken nextToken() {
    Boolean nextLine = skipBlank()
    ZatlinToken token = null
    if ($first || $afterSemicolon || !nextLine) {
      $first = false
      $reader.mark(3)
      Int codePoint = $reader.read()
      if (codePoint == '"') {
        $reader.reset()
        token = nextQuoteLiteral()
      } else if (codePoint == '=') {
        token = ZatlinToken.new(ZatlinTokenType.EQUAL, "=", $reader)
      } else if (codePoint == '|') {
        token = ZatlinToken.new(ZatlinTokenType.VERTICAL, "|", $reader)
      } else if (codePoint == '-') {
        token = ZatlinToken.new(ZatlinTokenType.MINUS, "-", $reader)
      } else if (codePoint == '^') {
        token = ZatlinToken.new(ZatlinTokenType.CIRCUMFLEX, "^", $reader)
      } else if (codePoint == '%') {
        token = ZatlinToken.new(ZatlinTokenType.PERCENT, "%", $reader)
      } else if (codePoint == ';') {
        token = ZatlinToken.new(ZatlinTokenType.SEMICOLON, ";", $reader)
      } else if (ZatlinLexer.isNumeric(codePoint)) {
        $reader.reset()
        token = nextNumeric()
      } else if (ZatlinLexer.isLetter(codePoint)) {
        $reader.reset()
        token = nextIdentifier()
      } else if (codePoint == -1) {
        if (!$afterSemicolon) {
          token = ZatlinToken.new(ZatlinTokenType.SEMICOLON, "", $reader)
        } else {
          token = null
        }
      } else {
        throw ZatlinParseException.new("Invalid symbol", codePoint, $reader)
      }
    } else {
      token = ZatlinToken.new(ZatlinTokenType.SEMICOLON, "", $reader)
    }
    $afterSemicolon = false 
    if (token != null) {
      ZatlinTokenType tokenType = token.getType()
      if (tokenType == ZatlinTokenType.SEMICOLON) {
        $afterSemicolon = true
      }
    }
    return token
  }

  private ZatlinToken nextIdentifier() {
    StringBuilder currentName = StringBuilder.new()
    while (true) {
      $reader.mark(1)
      Int codePoint = $reader.read()
      if (ZatlinLexer.isLetter(codePoint)) {
        currentName.appendCodePoint(codePoint)
      } else {
        $reader.reset()
        break
      }
    }
    ZatlinToken token = ZatlinToken.new(ZatlinTokenType.IDENTIFIER, currentName.toString(), $reader)
    return token
  }

  private ZatlinToken nextNumeric() {
    StringBuilder currentNumber = StringBuilder.new()
    while (true) {
      $reader.mark(1)
      Int codePoint = $reader.read()
      if (ZatlinLexer.isLetter(codePoint)) {
        if (ZatlinLexer.isNumeric(codePoint)) {
          currentNumber.appendCodePoint(codePoint)
        } else {
          throw ZatlinParseException.new("Invalid numeric literal", codePoint, $reader)
        }
      } else {
        $reader.reset()
        break
      }
    }
    ZatlinToken token = ZatlinToken.new(ZatlinTokenType.NUMERIC, currentNumber.toString(), $reader)
    return token
  }

  private ZatlinToken nextQuoteLiteral() {
    StringBuilder currentContent = StringBuilder.new()
    Boolean inside = false
    while (true) {
      Int codePoint = $reader.read()
      if (inside) {
        if (codePoint == '\\') {
          Int nextCodePoint = $reader.read()
          if (nextCodePoint == '"' || nextCodePoint == '\\') {
            currentContent.appendCodePoint(nextCodePoint)
          } else if (nextCodePoint == 'u') {
            StringBuilder escapeCodePointString = StringBuilder.new()
            for (Int i = 0 ; i < 4 ; i ++) {
              Int escapeCodePoint = $reader.read()
              if (ZatlinLexer.isHex(escapeCodePoint)) {
                escapeCodePointString.appendCodePoint(escapeCodePoint)
              } else {
                throw ZatlinParseException.new("Invalid escape sequence", codePoint, $reader)
              }
            }
            currentContent.appendCodePoint(IntegerClass.parseInt(escapeCodePointString.toString(), 16))
          } else {
            throw ZatlinParseException.new("Invalid escape sequence", codePoint, $reader)
          }
        } else if (codePoint == '\n') {
          throw ZatlinParseException.new("The line ended before a string literal is closed", -1, $reader)
        } else if (codePoint == -1) {
          throw ZatlinParseException.new("The line ended before a string literal is closed", -1, $reader)
        } else if (codePoint == '"') {
          break
        } else {
          currentContent.appendCodePoint(codePoint)
        }
      } else {
        if (codePoint == '"') {
          inside = true
        } else if (codePoint == -1) {
          break
        }
      }
    }
    String text = currentContent.toString()
    ZatlinToken token = ZatlinToken.new(ZatlinTokenType.QUOTE_LITERAL, text, "\"" + text + "\"", $reader)
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
        } else if (!ZatlinLexer.isWhitespace(codePoint)) {
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