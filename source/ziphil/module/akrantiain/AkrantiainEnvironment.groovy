package ziphil.module.akrantiain

import groovy.transform.CompileStatic
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public enum AkrantiainEnvironment {

  CASE_SENSITIVE,
  CONSERVE_CASE,
  FALL_THROUGH,
  USE_NFD,
  CUSTOM

  public static Boolean contains(String name) {
    for (AkrantiainEnvironment value : AkrantiainEnvironment.values()) {
      if (value.name() == name) {
        return true
      }
    }
    return false
  }

}