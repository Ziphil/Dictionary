package ziphil.module.akrantiain

import groovy.transform.CompileStatic
import java.text.Normalizer
import ziphil.module.ExtendedBufferedReader
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class AkrantiainToken implements AkrantiainMatchable {

  private AkrantiainTokenType $type
  private String $text
  private String $fullText = ""
  private Int $lineNumber
  private Int $columnNumber

  public AkrantiainToken(AkrantiainTokenType type, String text, Int lineNumber, Int columnNumber) {
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

  public Int matchRight(AkrantiainElementGroup group, Int from, AkrantiainModule module) {
    Int to = -1
    if ($type == AkrantiainTokenType.QUOTE_LITERAL) {
      to = matchRightQuoteLiteral(group, from, module)
    } else if ($type == AkrantiainTokenType.CIRCUMFLEX) {
      to = matchRightCircumflex(group, from, module)
    } else if ($type == AkrantiainTokenType.IDENTIFIER) {
      to = matchRightIdentifier(group, from, module)
    }
    return to
  }

  public Int matchLeft(AkrantiainElementGroup group, Int to, AkrantiainModule module) {
    Int from = -1
    if ($type == AkrantiainTokenType.QUOTE_LITERAL) {
      from = matchLeftQuoteLiteral(group, to, module)
    } else if ($type == AkrantiainTokenType.CIRCUMFLEX) {
      from = matchLeftCircumflex(group, to, module)
    } else if ($type == AkrantiainTokenType.IDENTIFIER) {
      from = matchLeftIdentifier(group, to, module)
    }
    return from
  }

  private Int matchRightQuoteLiteral(AkrantiainElementGroup group, Int from, AkrantiainModule module) {
    Int to = -1
    Int matchedLength = 0
    Int pointer = from
    if ($text != "") {
      String text = (module.containsEnvironment(AkrantiainEnvironment.USE_NFD)) ? Normalizer.normalize($text, Normalizer.Form.NFD) : $text
      while (pointer < group.getElements().size()) {
        AkrantiainElement element = group.getElements()[pointer]
        String elementPart = element.getPart()
        if (matchedLength + elementPart.length() <= text.length()) {
          String textSubstring = text.substring(matchedLength, matchedLength + elementPart.length())
          String adjustedTextSubstring = textSubstring
          String adjustedElementPart = elementPart
          if (!module.containsEnvironment(AkrantiainEnvironment.CASE_SENSITIVE)) {
            adjustedTextSubstring = adjustedTextSubstring.toLowerCase()
          }
          if (!module.containsEnvironment(AkrantiainEnvironment.CASE_SENSITIVE) && module.containsEnvironment(AkrantiainEnvironment.PRESERVE_CASE)) {
            adjustedElementPart = adjustedElementPart.toLowerCase()
          }
          if (adjustedTextSubstring == adjustedElementPart) {
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

  private Int matchLeftQuoteLiteral(AkrantiainElementGroup group, Int to, AkrantiainModule module) {
    Int from = -1
    Int matchedLength = 0
    Int pointer = to - 1
    if ($text != "") {
      String text = (module.containsEnvironment(AkrantiainEnvironment.USE_NFD)) ? Normalizer.normalize($text, Normalizer.Form.NFD) : $text
      while (pointer >= 0) {
        AkrantiainElement element = group.getElements()[pointer]
        String elementPart = element.getPart()
        if (matchedLength + elementPart.length() <= text.length()) {
          String textSubstring = text.substring(text.length() - elementPart.length() - matchedLength, text.length() - matchedLength)
          String adjustedTextSubstring = textSubstring
          String adjustedElementPart = elementPart
          if (!module.containsEnvironment(AkrantiainEnvironment.CASE_SENSITIVE)) {
            adjustedTextSubstring = adjustedTextSubstring.toLowerCase()
          }
          if (!module.containsEnvironment(AkrantiainEnvironment.CASE_SENSITIVE) && module.containsEnvironment(AkrantiainEnvironment.PRESERVE_CASE)) {
            adjustedElementPart = adjustedElementPart.toLowerCase()
          }
          if (adjustedTextSubstring == adjustedElementPart) {
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

  private Int matchRightCircumflex(AkrantiainElementGroup group, Int from, AkrantiainModule module) {
    Int to = -1
    Boolean matched = false
    Int pointer = from
    while (pointer <= group.getElements().size()) {
      AkrantiainElement element = group.getElements()[pointer]
      if (element != null) {
        String elementPart = element.getPart()
        Int punctuationTo = -1
        if (AkrantiainLexer.isAllWhitespace(elementPart)) {
          matched = true
          pointer ++
        } else if ((punctuationTo = module.findPunctuationContent().matchRight(group, pointer, module)) >= 0) {
          matched = true
          pointer = punctuationTo
        } else {
          if (matched || from == 0) {
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

  private Int matchLeftCircumflex(AkrantiainElementGroup group, Int to, AkrantiainModule module) {
    Int from = -1
    Boolean matched = false
    Int pointer = to - 1
    while (pointer >= -1) {
      AkrantiainElement element = (pointer >= 0) ? group.getElements()[pointer] : null
      if (element != null) {
        String elementPart = element.getPart()
        Int punctuationFrom = -1
        if (AkrantiainLexer.isAllWhitespace(elementPart)) {
          matched = true
          pointer --
        } else if ((punctuationFrom = module.findPunctuationContent().matchLeft(group, pointer, module)) >= 0) {
          matched = true
          pointer = punctuationFrom
        } else {
          if (matched || to == group.getElements().size()) {
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

  private Int matchRightIdentifier(AkrantiainElementGroup group, Int from, AkrantiainModule module) {
    AkrantiainMatchable content = module.findContentOf($text)
    if (content != null) {
      Int to = content.matchRight(group, from, module)
      return to
    } else {
      throw AkrantiainException.new("This cannot happen")
    }
  }

  private Int matchLeftIdentifier(AkrantiainElementGroup group, Int to, AkrantiainModule module) {
    AkrantiainMatchable content = module.findContentOf($text)
    if (content != null) {
      Int from = content.matchLeft(group, to, module)
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

  public Boolean equals(Object object) {
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

  public Int getLineNumber() {
    return $lineNumber
  }

  public Int getColumnNumber() {
    return $columnNumber
  }

}