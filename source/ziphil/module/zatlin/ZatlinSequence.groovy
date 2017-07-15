package ziphil.module.zatlin

import groovy.transform.CompileStatic
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class ZatlinSequence implements ZatlinGeneratable {

  public static final ZatlinSequence EMPTY_SEQUENCE = ZatlinSequence.new()

  private List<ZatlinGeneratable> $generatables = ArrayList.new()

  public String generate(ZatlinRoot root) {
    StringBuilder output = StringBuilder.new()
    for (ZatlinGeneratable generatable : $generatables) {
      output.append(generatable.generate(root))
    }
    return output.toString()
  }

  public String toString() {
    StringBuilder string = StringBuilder.new()
    for (Int i = 0 ; i < $generatables.size() ; i ++) {
      string.append($generatables[i])
      if (i < $generatables.size() - 1) {
        string.append(" ")
      }
    }
    return string.toString()
  }

  public Boolean hasGeneratable() {
    return !$generatables.isEmpty()
  }

  public List<ZatlinGeneratable> getGeneratables() {
    return $generatables
  }

  public void setGeneratables(List<ZatlinGeneratable> generatables) {
    $generatables = generatables
  }

}