package ziphil.module.akrantiain

import groovy.transform.CompileStatic
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class AkrantiainToken implements AkrantiainMatchable {

  private AkrantiainTokenType $type
  private String $text
  private Integer $lineNumber

  public AkrantiainToken(AkrantiainTokenType type, String text, Integer lineNumber) {
    $type = type
    $text = text
    $lineNumber = lineNumber
  }

  public Integer matchRight(AkrantiainElementGroup group, Integer from, AkrantiainSetting setting) {
    Integer to = null
    if ($type == AkrantiainTokenType.QUOTE_LITERAL) {
      to = matchRightQuoteLiteral(group, from, setting)
    } else if ($type == AkrantiainTokenType.CIRCUMFLEX) {
      to = matchRightCircumflex(group, from, setting)
    } else if ($type == AkrantiainTokenType.IDENTIFIER) {
      to = matchRightIdentifier(group, from, setting)
    }
    return to
  }

  public Integer matchLeft(AkrantiainElementGroup group, Integer to, AkrantiainSetting setting) {
    Integer from = null
    if ($type == AkrantiainTokenType.QUOTE_LITERAL) {
      from = matchLeftQuoteLiteral(group, to, setting)
    } else if ($type == AkrantiainTokenType.CIRCUMFLEX) {
      from = matchLeftCircumflex(group, to, setting)
    } else if ($type == AkrantiainTokenType.IDENTIFIER) {
      from = matchLeftIdentifier(group, to, setting)
    }
    return from
  }

  private Integer matchRightQuoteLiteral(AkrantiainElementGroup group, Integer from, AkrantiainSetting setting) {
    Integer to = null
    Integer matchedLength = 0
    Integer pointer = from
    while (pointer < group.getElements().size()) {
      AkrantiainElement element = group.getElements()[pointer]
      String elementPart = element.getPart()
      if (matchedLength + elementPart.length() <= $text.length()) {
        String textSubstring = $text.substring(matchedLength, matchedLength + elementPart.length())
        String adjustedTextSubstring = (setting.containsEnvironment(AkrantiainEnvironment.CASE_SENSITIVE)) ? textSubstring : textSubstring.toLowerCase()
        String adjustedElementPart = (setting.containsEnvironment(AkrantiainEnvironment.CASE_SENSITIVE)) ? elementPart : elementPart.toLowerCase()
        if (adjustedTextSubstring == adjustedElementPart) {
          matchedLength += elementPart.length()
          if (matchedLength == $text.length()) {
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
    return to
  }

  private Integer matchLeftQuoteLiteral(AkrantiainElementGroup group, Integer to, AkrantiainSetting setting) {
    Integer from = null
    Integer matchedLength = 0
    Integer pointer = to - 1
    while (pointer >= 0) {
      AkrantiainElement element = group.getElements()[pointer]
      String elementPart = element.getPart()
      if (matchedLength + elementPart.length() <= $text.length()) {
        String textSubstring = $text.substring($text.length() - elementPart.length() - matchedLength, $text.length() - matchedLength)
        String adjustedTextSubstring = (setting.containsEnvironment(AkrantiainEnvironment.CASE_SENSITIVE)) ? textSubstring : textSubstring.toLowerCase()
        String adjustedElementPart = (setting.containsEnvironment(AkrantiainEnvironment.CASE_SENSITIVE)) ? elementPart : elementPart.toLowerCase()
        if (adjustedTextSubstring == adjustedElementPart) {
          matchedLength += elementPart.length()
          if (matchedLength == $text.length()) {
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
    return from
  }

  private Integer matchRightCircumflex(AkrantiainElementGroup group, Integer from, AkrantiainSetting setting) {
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
        } else if ((punctuationTo = setting.findPunctuationContent().matchRight(group, pointer, setting)) != null) {
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

  private Integer matchLeftCircumflex(AkrantiainElementGroup group, Integer to, AkrantiainSetting setting) {
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
        } else if ((punctuationFrom = setting.findPunctuationContent().matchLeft(group, pointer, setting)) != null) {
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

  private Integer matchRightIdentifier(AkrantiainElementGroup group, Integer from, AkrantiainSetting setting) {
    Integer to = setting.findContentOf($text).matchRight(group, from, setting)
    return to
  }

  private Integer matchLeftIdentifier(AkrantiainElementGroup group, Integer to, AkrantiainSetting setting) {
    Integer from = setting.findContentOf($text).matchLeft(group, to, setting)
    return from
  }

  public Boolean isConcrete() {
    return $type != AkrantiainTokenType.CIRCUMFLEX
  }

  public String toString() {
    StringBuilder string = StringBuilder.new()
    string.append($type)
    string.append("<")
    string.append($text)
    string.append(">")
    return string.toString()
  }

  public AkrantiainTokenType getType() {
    return $type
  }

  public String getText() {
    return $text
  }

  public Integer getLineNumber() {
    return $lineNumber
  }

}