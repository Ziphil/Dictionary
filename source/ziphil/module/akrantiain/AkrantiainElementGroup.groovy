package ziphil.module.akrantiain

import groovy.transform.CompileStatic
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class AkrantiainElementGroup {

  private List<AkrantiainElement> $elements = ArrayList.new()

  public static AkrantiainElementGroup create(String input) {
    AkrantiainElementGroup group = AkrantiainElementGroup.new()
    for (String character : input) {
      AkrantiainElement element = AkrantiainElement.new(character)
      group.getElements().add(element)
    }
    return group
  }

  // インデックスが from から to まで (to は含まない) の要素を 1 つに合成した要素を返します。
  // 返される要素の part の値は、合成前の part をインデックス順に繋げたものになります。
  // 返される要素の result の値は、合成前の各要素の result の値に関わらず null になります。
  public AkrantiainElement merge(Integer from, Integer to) {
    StringBuilder mergedPart = StringBuilder.new()
    for (Integer i : from ..< to) {
      mergedPart.append($elements[i].getPart())
    }
    return AkrantiainElement.new(mergedPart.toString())
  }

  // インデックスが from から to まで (to は含まない) の要素を 1 文字ごとに分割した要素グループを返します。
  // 返される要素グループに含まれる全ての要素の result の値は、常に null になります。
  public AkrantiainElementGroup devide(Integer from, Integer to) {
    AkrantiainElementGroup group = AkrantiainElementGroup.new()
    for (Integer i : from ..< to) {
      group.getElements().addAll($elements[i].devide().getElements())
    }
    return group
  }

  public AkrantiainElement firstInvalidElement(AkrantiainSetting setting) {
    for (AkrantiainElement element : $elements) {
      if (!element.isValid(setting)) {
        return element
      }
    }
    return null
  }

  // 各要素の変換後の文字列を連結し、出力文字列を作成します。
  // 変換がなされていない要素が含まれていた場合は、代わりにスペース 1 つを連結します。
  // したがって、このメソッドを実行する前に、全ての要素が変換されているかどうかを firstInvalidElement() などで確認してください。
  public String createOutput() {
    StringBuilder output = StringBuilder.new()
    for (AkrantiainElement element : $elements) {
      if (element.getResult() != null) {
        output.append(element.getResult())
      } else {
        output.append(" ")
      }
    }
    return output.toString()
  }

  public String toString() {
    StringBuilder string = StringBuilder.new()
    string.append("[")
    for (Integer i : 0 ..< $elements.size()) {
      string.append($elements[i])
      if (i < $elements.size() - 1) {
        string.append(", ")
      }
    }
    string.append("]")
    return string.toString()
  }

  public List<AkrantiainElement> getElements() {
    return $elements
  }

  public void setElements(List<AkrantiainElement> elements) {
    $elements = elements
  }

}