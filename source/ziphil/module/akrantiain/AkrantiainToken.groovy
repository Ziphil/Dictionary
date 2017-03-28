package ziphil.module.akrantiain

import groovy.transform.CompileStatic
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class AkrantiainToken {

  private AkrantiainTokenType $type
  private String $text

  public AkrantiainToken(AkrantiainTokenType type, String text) {
    $type = type
    $text = text
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
    for (Integer i = from ; i < group.getElements().size() ; i ++) {
      AkrantiainElement element = group.getElements()[i]
      String elementPart = element.getPart()
      if (!element.isConverted() && matchedLength + elementPart.length() <= $text.length()) {
        if ($text.substring(matchedLength, matchedLength + elementPart.length()) == elementPart) {
          matchedLength += elementPart.length()
          if (matchedLength == $text.length()) {
            to = i + 1
            break
          }
        } else {
          break
        }
      } else {
        break
      }
    }
    return to
  }

  private Integer matchCircumflexSelection(AkrantiainElementGroup group, Integer from, AkrantiainSetting setting)  {
    Integer to = null
    Boolean isMatched = false
    for (Integer i = from ; i <= group.getElements().size() ;) {
      AkrantiainElement element = group.getElements()[i]
      if (element != null && !element.isConverted()) {
        String elementPart = element.getPart()
        Integer punctuationTo
        if (AkrantiainLexer.isAllWhitespace(elementPart)) {
          isMatched = true
          i += 1
        } else if ((punctuationTo = setting.findPunctuationRight().matchSelection(group, i, setting)) != null) {
          isMatched = true
          i = punctuationTo
        } else {
          if (isMatched) {
            to = i
          }
          break
        }
      } else {
        if (isMatched) {
          to = i
        }
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

}