package ziphil.module.akrantiain

import groovy.transform.CompileStatic
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class AkrantiainElement {

  private String $part
  private String $result

  public AkrantiainElement(String part, String result) {
    $part = part
    $result = result
  }

  public AkrantiainElement(String part) {
    $part = part
    $result = null
  }

  public AkrantiainElementGroup devide() {
    AkrantiainElementGroup group = AkrantiainElementGroup.new()
    for (String character : $part) {
      AkrantiainElement element = AkrantiainElement.new(character, null)
      group.getElements().add(element)
    }
    return group
  }

  public Boolean isConverted() {
    return $result != null
  }

  // この要素が変換されていれば true を返し、そうでなければ false を返します。
  // ただし、変換前の文字列が句読点かスペースのみで構成されている場合は、変換されいてるかどうかにかかわらず true を返します。
  public Boolean isValid(AkrantiainSetting setting) {
    if ($result == null) {
      if (AkrantiainLexer.isAllWhitespace($part)) {
        return true
      } else {
        AkrantiainElementGroup group = AkrantiainElementGroup.new()
        group.getElements().add(this)
        if (setting.findPunctuationContent().matchRight(group, 0, setting) != null) {
          return true
        } else {
          return false
        }
      }
    } else {
      return true
    }
  }

  public String toString() {
    StringBuilder string = StringBuilder.new()
    string.append("(")
    string.append($part)
    string.append(" ~> ")
    string.append($result)
    string.append(")")
    return string.toString()
  }

  public String getPart() {
    return $part
  }

  public void setPart(String part) {
    $part = part
  }

  public String getResult() {
    return $result
  }

  public void setResult(String result) {
    $result = result
  }

}