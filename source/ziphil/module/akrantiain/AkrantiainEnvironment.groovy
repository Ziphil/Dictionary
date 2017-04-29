package ziphil.module.akrantiain

import groovy.transform.CompileStatic
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public enum AkrantiainEnvironment {

  CASE_SENSITIVE,
  FALL_THROUGH,
  NORMALIZE

  public static Boolean contains(String name) {
    for (AkrantiainEnvironment value : AkrantiainEnvironment.values()) {
      if (value.name() == name) {
        return true
      }
    }
    return false
  }

}