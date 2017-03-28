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

  public Boolean isValid(AkrantiainSetting setting) {
    for (AkrantiainElement elements : $elements) {
      if (!elements.isValid(setting)) {
        return false
      }
    }
    return true
  }

  // 各要素の変換後の文字列を連結し、出力文字列を作成します。
  // 変換がなされていない要素が含まれていた場合は、代わりに変換前の文字列を連結します。
  // したがって、このメソッドを実行する前に、全ての要素が変換されているかどうかを isValid() で確認してください。
  public String createOutput() {
    StringBuilder output = StringBuilder.new()
    for (AkrantiainElement element : $elements) {
      if (element.getResult() != null) {
        output.append(element.getResult())
      } else {
        output.append(element.getPart())
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