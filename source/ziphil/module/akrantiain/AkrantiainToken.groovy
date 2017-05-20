package ziphil.module.akrantiain

import groovy.transform.CompileStatic
import java.text.Normalizer
import ziphil.module.ExtendedBufferedReader
import ziphilib.transform.Ziphilify
import ziphilib.type.PrimBoolean


@CompileStatic @Ziphilify
public class AkrantiainToken implements AkrantiainMatchable {

  private AkrantiainTokenType $type
  private String $text
  private String $fullText = ""
  private Integer $lineNumber
  private Integer $columnNumber

  public AkrantiainToken(AkrantiainTokenType type, String text, Integer lineNumber, Integer columnNumber) {
    $type = type
    $text = text
    $lineNumber = lineNumber
    $columnNumber = columnNumber
    makeFullText()
  }

  public AkrantiainToken(AkrantiainTokenType type, String text, ExtendedBufferedReader reader) {
    $type = type
    $text = text
    $lineNumber = reader.getLineNumber()
    $columnNumber = reader.getColumnNumber()
    makeFullText()
  }

  public Integer matchRight(AkrantiainElementGroup group, Integer from, AkrantiainModule module) {
    Integer to = null
    if ($type == AkrantiainTokenType.QUOTE_LITERAL) {
      to = matchRightQuoteLiteral(group, from, module)
    } else if ($type == AkrantiainTokenType.CIRCUMFLEX) {
      to = matchRightCircumflex(group, from, module)
    } else if ($type == AkrantiainTokenType.IDENTIFIER) {
      to = matchRightIdentifier(group, from, module)
    }
    return to
  }

  public Integer matchLeft(AkrantiainElementGroup group, Integer to, AkrantiainModule module) {
    Integer from = null
    if ($type == AkrantiainTokenType.QUOTE_LITERAL) {
      from = matchLeftQuoteLiteral(group, to, module)
    } else if ($type == AkrantiainTokenType.CIRCUMFLEX) {
      from = matchLeftCircumflex(group, to, module)
    } else if ($type == AkrantiainTokenType.IDENTIFIER) {
      from = matchLeftIdentifier(group, to, module)
    }
    return from
  }

  private Integer matchRightQuoteLiteral(AkrantiainElementGroup group, Integer from, AkrantiainModule module) {
    Integer to = null
    Integer matchedLength = 0
    Integer pointer = from
    if ($text != "") {
      String text = (module.containsEnvironment(AkrantiainEnvironment.USE_NFD)) ? Normalizer.normalize($text, Normalizer.Form.NFD) : $text
      while (pointer < group.getElements().size()) {
        AkrantiainElement element = group.getElements()[pointer]
        String elementPart = element.getPart()
        if (matchedLength + elementPart.length() <= text.length()) {
          String textSubstring = text.substring(matchedLength, matchedLength + elementPart.length())
          String adjustedTextSubstring = (module.containsEnvironment(AkrantiainEnvironment.CASE_SENSITIVE)) ? textSubstring : textSubstring.toLowerCase()
          if (adjustedTextSubstring == elementPart) {
            matchedLength += elementPart.length()
            if (matchedLength == text.length()) {
              to = pointer + 1
              break
            }
          } else {
            break
          }
        } else {
          break
        }
        pointer ++
      }
    } else {
      to = from
    }
    return to
  }

  private Integer matchLeftQuoteLiteral(AkrantiainElementGroup group, Integer to, AkrantiainModule module) {
    Integer from = null
    Integer matchedLength = 0
    Integer pointer = to - 1
    if ($text != "") {
      String text = (module.containsEnvironment(AkrantiainEnvironment.USE_NFD)) ? Normalizer.normalize($text, Normalizer.Form.NFD) : $text
      while (pointer >= 0) {
        AkrantiainElement element = group.getElements()[pointer]
        String elementPart = element.getPart()
        if (matchedLength + elementPart.length() <= text.length()) {
          String textSubstring = text.substring(text.length() - elementPart.length() - matchedLength, text.length() - matchedLength)
          String adjustedTextSubstring = (module.containsEnvironment(AkrantiainEnvironment.CASE_SENSITIVE)) ? textSubstring : textSubstring.toLowerCase()
          if (adjustedTextSubstring == elementPart) {
            matchedLength += elementPart.length()
            if (matchedLength == text.length()) {
              from = pointer
              break
            }
          } else {
            break
          }
        } else {
          break
        }
        pointer --
      }
    } else {
      from = to
    }
    return from
  }

  private Integer matchRightCircumflex(AkrantiainElementGroup group, Integer from, AkrantiainModule module) {
    Integer to = null
    Boolean isMatched = false
    Integer pointer = from
    while (pointer <= group.getElements().size()) {
      AkrantiainElement element = group.getElements()[pointer]
      if (element != null) {
        String elementPart = element.getPart()
        Integer punctuationTo = null
        if (AkrantiainLexer.isAllWhitespace(elementPart)) {
          isMatched = true
          pointer ++
        } else if ((punctuationTo = module.findPunctuationContent().matchRight(group, pointer, module)) != null) {
          isMatched = true
          pointer = punctuationTo
        } else {
          if (isMatched || from == 0) {
            to = pointer
          }
          break
        }
      } else {
        to = pointer
        break
      }
    }
    return to
  }

  private Integer matchLeftCircumflex(AkrantiainElementGroup group, Integer to, AkrantiainModule module) {
    Integer from = null
    Boolean isMatched = false
    Integer pointer = to - 1
    while (pointer >= -1) {
      AkrantiainElement element = (pointer >= 0) ? group.getElements()[pointer] : null
      if (element != null) {
        String elementPart = element.getPart()
        Integer punctuationFrom = null
        if (AkrantiainLexer.isAllWhitespace(elementPart)) {
          isMatched = true
          pointer --
        } else if ((punctuationFrom = module.findPunctuationContent().matchLeft(group, pointer, module)) != null) {
          isMatched = true
          pointer = punctuationFrom
        } else {
          if (isMatched || to == group.getElements().size()) {
            from = pointer + 1
          }
          break
        }
      } else {
        from = pointer + 1
        break
      }
    }
    return from
  }

  private Integer matchRightIdentifier(AkrantiainElementGroup group, Integer from, AkrantiainModule module) {
    AkrantiainMatchable content = module.findContentOf($text)
    if (content != null) {
      Integer to = content.matchRight(group, from, module)
      return to
    } else {
      throw AkrantiainException.new("This cannot happen")
    }
  }

  private Integer matchLeftIdentifier(AkrantiainElementGroup group, Integer to, AkrantiainModule module) {
    AkrantiainMatchable content = module.findContentOf($text)
    if (content != null) {
      Integer from = content.matchLeft(group, to, module)
      return from
    } else {
      throw AkrantiainException.new("This cannot happen")
    }
  }

  private void makeFullText() {
    if ($type == AkrantiainTokenType.QUOTE_LITERAL) {
      $fullText = "\"" + $text + "\""
    } else if ($type == AkrantiainTokenType.SLASH_LITERAL) {
      $fullText = "/" + $text + "/"
    } else if ($type == AkrantiainTokenType.ENVIRONMENT_LITERAL) {
      $fullText = "@" + $text
    } else {
      $fullText = $text
    }
  }

  public AkrantiainToken findUnknownIdentifier(AkrantiainModule module) {
    if ($type == AkrantiainTokenType.IDENTIFIER) {
      if (!module.containsDefinitionOf($text)) {
        return this
      } else {
        return null
      }
    } else {
      return null
    }
  }

  public AkrantiainToken findCircularIdentifier(List<AkrantiainToken> identifiers, AkrantiainModule module) {
    if ($type == AkrantiainTokenType.IDENTIFIER) {
      AkrantiainToken containedIdentifier = null
      for (AkrantiainToken identifier : identifiers) {
        if (this == identifier) {
          containedIdentifier = identifier
          break
        }
      }
      if (containedIdentifier != null) {
        return containedIdentifier
      } else {
        AkrantiainDefinition definition = module.findDefinitionOf($text)
        if (definition != null) {
          return definition.findCircularIdentifier(identifiers, module)
        } else {
          return null
        }
      }
    } else {
      return null
    }
  }

  public Boolean isConcrete() {
    return $type != AkrantiainTokenType.CIRCUMFLEX
  }

  public PrimBoolean equals(Object object) {
    if (object instanceof AkrantiainToken) {
      return $type == object.getType() && $text == object.getText()
    } else {
      return false
    }
  }

  public String toString() {
    StringBuilder string = StringBuilder.new()
    string.append("<")
    string.append($type)
    string.append(": '")
    string.append($fullText)
    string.append("'>")
    return string.toString()
  }

  public AkrantiainTokenType getType() {
    return $type
  }

  public String getText() {
    return $text
  }

  public String getFullText() {
    return $fullText
  }

  public Integer getLineNumber() {
    return $lineNumber
  }

  public Integer getColumnNumber() {
    return $columnNumber
  }

}