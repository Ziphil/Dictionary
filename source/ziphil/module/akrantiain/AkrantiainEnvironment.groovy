package ziphil.module.akrantiain

import groovy.transform.CompileStatic
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public enum AkrantiainEnvironment {

  CASE_SENSITIVE("case_sensitive"),
  PRESERVE_CASE("preserve_case"),
  FALL_THROUGH("fall_through", "fallthrough", "fall_thru", "fallthru"),
  USE_NFD("use_nfd"),
  CUSTOM("\b")

  private List<String> $names

  private AkrantiainEnvironment(String... names) {
    $names = Arrays.asList(names)
  }

  public static AkrantiainEnvironment valueOfName(String name) {
    String modifiedName = name.toLowerCase()
    for (AkrantiainEnvironment value : AkrantiainEnvironment.values()) {
      if (value.getNames().contains(modifiedName)) {
        return value
      }
    }
    throw IllegalArgumentException.new()
  }

  public static Boolean contains(String name) {
    String modifiedName = name.toLowerCase()
    for (AkrantiainEnvironment value : AkrantiainEnvironment.values()) {
      if (value.getNames().contains(modifiedName)) {
        return true
      }
    }
    return false
  }

  private List<String> getNames() {
    return $names
  }

}