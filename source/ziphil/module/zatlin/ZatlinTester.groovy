package ziphil.module.zatlin

import groovy.transform.CompileStatic
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class ZatlinTester {

  public static void lex(String source) {
    ZatlinLexer lexer = ZatlinLexer.new(StringReader.new(source))
    for (ZatlinToken token ; (token = lexer.nextToken()) != null ;) {
      println(token)
    }
  }

}