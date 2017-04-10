package ziphil.module.akrantiain

import groovy.transform.CompileStatic
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public enum AkrantiainTokenType {

  IDENTIFIER,
  QUOTE_LITERAL,
  SLASH_LITERAL,
  ENVIRONMENT_LITERAL,
  EQUAL,
  ARROW,
  BOLD_ARROW,
  ADVANCE,
  VERTICAL,
  CIRCUMFLEX,
  DOLLAR,
  EXCLAMATION,
  PERCENT,
  DOUBLE_PERCENT,
  OPEN_PAREN,
  CLOSE_PAREN,
  OPEN_CURLY,
  CLOSE_CURLY,
  SEMICOLON

}