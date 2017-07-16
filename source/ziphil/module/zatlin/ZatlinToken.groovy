package ziphil.module.zatlin

import groovy.transform.CompileStatic
import ziphil.module.ExtendedBufferedReader
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class ZatlinToken implements ZatlinGeneratable {

  private ZatlinTokenType $type
  private String $text
  private String $fullText
  private Int $lineNumber
  private Int $columnNumber

  public ZatlinToken(ZatlinTokenType type, String text, String fullText, Int lineNumber, Int columnNumber) {
    $type = type
    $text = text
    $fullText = fullText
    $lineNumber = lineNumber
    $columnNumber = columnNumber
  }

  public ZatlinToken(ZatlinTokenType type, String text, Int lineNumber, Int columnNumber) {
    $type = type
    $text = text
    $fullText = fullText
    $lineNumber = lineNumber
    $columnNumber = columnNumber
  }

  public ZatlinToken(ZatlinTokenType type, String text, String fullText, ExtendedBufferedReader reader) {
    $type = type
    $text = text
    $fullText = fullText
    $lineNumber = reader.getLineNumber()
    $columnNumber = reader.getColumnNumber()
  }

  public ZatlinToken(ZatlinTokenType type, String text, ExtendedBufferedReader reader) {
    $type = type
    $text = text
    $fullText = text
    $lineNumber = reader.getLineNumber()
    $columnNumber = reader.getColumnNumber()
  }

  public String generate(ZatlinRoot root) {
    String output = null
    if ($type == ZatlinTokenType.QUOTE_LITERAL) {
      output = generateByQuoteLiteral(root)
    } else if ($type == ZatlinTokenType.IDENTIFIER) {
      output = generateByIdentifier(root)
    }
    return output
  }

  private String generateByQuoteLiteral(ZatlinRoot root) {
    return $text
  }

  private String generateByIdentifier(ZatlinRoot root) {
    ZatlinGeneratable content = root.findContentOf($text)
    if (content != null) {
      String output = content.generate(root)
      return output
    } else {
      throw ZatlinException.new("This cannot happen")
    }
  }

  public ZatlinToken findUnknownIdentifier(ZatlinRoot root) {
    if ($type == ZatlinTokenType.IDENTIFIER) {
      if (!root.containsDefinitionOf($text)) {
        return this
      } else {
        return null
      }
    } else {
      return null
    }
  }

  public ZatlinToken findCircularIdentifier(List<ZatlinToken> identifiers, ZatlinRoot root) {
    if ($type == ZatlinTokenType.IDENTIFIER) {
      ZatlinToken containedIdentifier = null
      for (ZatlinToken identifier : identifiers) {
        if (this == identifier) {
          containedIdentifier = identifier
          break
        }
      }
      if (containedIdentifier != null) {
        return containedIdentifier
      } else {
        ZatlinDefinition definition = root.findDefinitionOf($text)
        if (definition != null) {
          return definition.findCircularIdentifier(identifiers, root)
        } else {
          return null
        }
      }
    } else {
      return null
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

  public ZatlinTokenType getType() {
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