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

  public Boolean isConverted() {
    return $result != null
  }

  // この要素が変換されていれば true を返し、そうでなければ false を返します。
  // ただし、変換前の文字列が句読点かスペースのみで構成されている場合は、変換されいてるかどうかにかかわらず true を返します。
  public Boolean isValid(AkrantiainSetting setting) {
    if ($result == null) {
      AkrantiainToken token = AkrantiainToken.new(AkrantiainTokenType.CIRCUMFLEX, "^")
      AkrantiainElementGroup group = AkrantiainElementGroup.new()
      group.getElements().add(this)
      return token.matchSelection(group, 0, setting) != null
    } else {
      return true
    }
  }

  // 与えられた配列 group において、インデックスが from から to まで (to は含まない) のオブジェクトを 1 つに合成したオブジェクトを返します。
  // 返されるオブジェクトの part の値は、合成前の part をインデックス順に繋げたものになります。
  // 返されるオブジェクトの result の値は、合成前の各オブジェクトの result の値に関わらず null になります。
  public static AkrantiainElement merge(AkrantiainElementGroup group, Integer from, Integer to) {
    StringBuilder mergedPart = StringBuilder.new()
    for (Integer i : from ..< to) {
      mergedPart.append(group.getElements()[i].getPart())
    }
    return AkrantiainElement.new(mergedPart.toString())
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