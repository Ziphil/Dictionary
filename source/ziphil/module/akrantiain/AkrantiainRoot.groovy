package ziphil.module.akrantiain

import groovy.transform.CompileStatic
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class AkrantiainRoot {

  private List<AkrantiainModule> $modules = Collections.synchronizedList(ArrayList.new())
  private AkrantiainModule $defaultModule = AkrantiainModule.new()
  private List<AkrantiainWarning> $warnings = Collections.synchronizedList(ArrayList.new())

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

  public AkrantiainToken findUnknownIdentifier() {
    for (AkrantiainModule module : $modules) {
      AkrantiainToken unknownIdentifier = module.findUnknownIdentifier()
      if (unknownIdentifier != null) {
        return unknownIdentifier
      }
    }
    AkrantiainToken unknownIdentifier = $defaultModule.findUnknownIdentifier()
    if (unknownIdentifier != null) {
      return unknownIdentifier
    } else {
      return null
    }
  }

  public AkrantiainToken findCircularIdentifier() {
    for (AkrantiainModule module : $modules) {
      AkrantiainToken circularIdentifier = module.findCircularIdentifier()
      if (circularIdentifier != null) {
        return circularIdentifier
      }
    }
    AkrantiainToken circularIdentifier = $defaultModule.findCircularIdentifier()
    if (circularIdentifier != null) {
      return circularIdentifier
    } else {
      return null
    }
  }

  public AkrantiainModuleName findUnknownModuleName() {
    for (AkrantiainModule module : $modules) {
      AkrantiainModuleName unknownModuleName = module.findUnknownModuleName(this)
      if (unknownModuleName != null) {
        return unknownModuleName
      }
    }
    AkrantiainModuleName unknownModuleName = $defaultModule.findUnknownModuleName(this)
    if (unknownModuleName != null) {
      return unknownModuleName
    } else {
      return null
    }
  }

  // モジュールの定義に循環参照がないかを調べ、循環が見つかった場合は循環の最初のモジュール名を返し、見つからなければ null を返します。
  // このメソッドはデフォルトモジュールから参照されているもののみを調べるので、参照されていないモジュールの中での循環参照は検査しません。
  public AkrantiainModuleName findCircularModuleName() {
    AkrantiainModuleName circularModuleName = $defaultModule.findCircularModuleName(this)
    if (circularModuleName != null) {
      return circularModuleName
    } else {
      return null
    }
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

  public List<AkrantiainWarning> getWarnings() {
    return $warnings
  }

  public void setWarnings(List<AkrantiainWarning> warnings) {
    $warnings = warnings
  }

}