package ziphil.module.zatlin

import groovy.transform.CompileStatic
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class ZatlinRoot {

  private List<ZatlinDefinition> $definitions = ArrayList.new()
  private ZatlinGeneratable $mainGeneratable = null

  public String generate() {
    return null
  }

  public List<ZatlinDefinition> getDefinitions() {
    return $definitions
  }

  public void setDefinitions(List<ZatlinDefinition> definitions) {
    $definitions = definitions
  }

  public ZatlinGeneratable getMainGeneratable() {
    return $mainGeneratable
  }

  public void setMainGeneratable(ZatlinGeneratable mainGeneratable) {
    $mainGeneratable = mainGeneratable
  }

}