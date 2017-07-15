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

  public static void parse(String source) {
    ZatlinParser parser = ZatlinParser.new(StringReader.new(source))
    ZatlinRoot root = parser.readRoot()
    println("Definitions:")
    for (ZatlinDefinition definition : root.getDefinitions()) {
      println(definition)
    }
    println("Main Pattern:")
    println(root.getMainGeneratable())
  }

  public static void generate(String source, Int size) {
    ZatlinParser parser = ZatlinParser.new(StringReader.new(source))
    ZatlinRoot root = parser.readRoot()
    for (Int i = 0 ; i < size ; i ++) {
      println(root.generate())
    }
  }

}