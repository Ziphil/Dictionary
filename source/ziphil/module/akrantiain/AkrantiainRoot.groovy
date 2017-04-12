package ziphil.module.akrantiain

import groovy.transform.CompileStatic
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class AkrantiainRoot {

  private List<AkrantiainModule> $modules = Collections.synchronizedList(ArrayList.new())
  private AkrantiainModule $defaultModule = AkrantiainModule.new()

  public String convert(String input) {
    return $defaultModule.convert(input, this)
  }

  public AkrantiainModule findModuleOf(AkrantiainModuleName moduleName) {
    for (AkrantiainModule module : $modules) {
      if (module.getName() == moduleName) {
        return module
      }
    }
    return null
  }

  public Boolean containsModuleOf(AkrantiainModuleName moduleName) {
    for (AkrantiainModule module : $modules) {
      if (module.getName() == moduleName) {
        return true
      }
    }
    return false
  }

  public String toString() {
    StringBuilder string = StringBuilder.new()
    for (Integer i : 0 ..< $modules.size()) {
      string.append($modules[i])
      string.append("\n")
    }
    string.append($defaultModule)
    return string.toString()
  }

  public List<AkrantiainModule> getModules() {
    return $modules
  }

  public void setModules(List<AkrantiainModule> modules) {
    $modules = modules
  }

  public AkrantiainModule getDefaultModule() {
    return $defaultModule
  }

  public void setDefaultModule(AkrantiainModule defaultModule) {
    $defaultModule = defaultModule
  }

}