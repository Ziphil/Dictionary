package ziphil.custom

import groovy.transform.CompileStatic
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public enum DialogStatus {

  COMMITTED, NEGATED, CANCELLED

}