package ziphil.module.akrantiain

import groovy.transform.CompileStatic
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class AkrantiainToken {

  private AkrantiainTokenType $type
  private String $text
  private Integer $lineNumber

  public AkrantiainToken(AkrantiainTokenType type, String text, Integer lineNumber) {
    $type = type
    $text = text
    $lineNumber = lineNumber
  }

  public Integer matchSelection(AkrantiainElementGroup group, Integer from, AkrantiainSetting setting) {
    Integer to = null
    if ($type == AkrantiainTokenType.QUOTE_LITERAL) {
      to = matchQuoteLiteralSelection(group, from, setting)
    } else if ($type == AkrantiainTokenType.CIRCUMFLEX) {
      to = matchCircumflexSelection(group, from, setting)
    } else if ($type == AkrantiainTokenType.IDENTIFIER) {
      to = matchIdentifierSelection(group, from, setting)
    }
    return to
  }

  private Integer matchQuoteLiteralSelection(AkrantiainElementGroup group, Integer from, AkrantiainSetting setting) {
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

  private Integer matchCircumflexSelection(AkrantiainElementGroup group, Integer from, AkrantiainSetting setting) {
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
        } else if ((punctuationTo = setting.findPunctuationRight().matchSelection(group, pointer, setting)) != null) {
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

  private Integer matchIdentifierSelection(AkrantiainElementGroup group, Integer from, AkrantiainSetting setting) {
    Integer to = setting.findRightOf($text).matchSelection(group, from, setting)
    return to
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