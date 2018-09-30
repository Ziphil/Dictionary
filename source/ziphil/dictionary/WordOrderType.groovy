package ziphil.dictionary

import groovy.transform.CompileStatic
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public enum WordOrderType {

  CUSTOM,
  IDENTIFIER,
  UNICODE

}