package ziphil.module.zatlin

import groovy.transform.CompileStatic
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public enum ZatlinTokenType {

  IDENTIFIER,
  QUOTE_LITERAL,
  NUMERIC,
  EQUAL,
  VERTICAL,
  CIRCUMFLEX,
  PERCENT,
  SEMICOLON

}