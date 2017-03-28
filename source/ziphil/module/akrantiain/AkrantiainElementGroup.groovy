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

  public List<AkrantiainElement> getElements() {
    return $elements
  }

  public void setElements(List<AkrantiainElement> elements) {
    $elements = elements
  }

}