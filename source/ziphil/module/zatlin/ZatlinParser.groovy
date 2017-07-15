package ziphil.module.zatlin

import groovy.transform.CompileStatic
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class ZatlinParser {

  private ZatlinRoot $root = ZatlinRoot.new()
  private ZatlinLexer $lexer

  public ZatlinParser(Reader reader) {
    $lexer = ZatlinLexer.new(reader)
  }

  public ZatlinRoot readRoot() {
    ZatlinSentenceParser sentenceParser = ZatlinSentenceParser.new() 
    for (ZatlinToken token ; (token = $lexer.nextToken()) != null ;) {
      ZatlinTokenType tokenType = token.getType()
      if (tokenType == ZatlinTokenType.SEMICOLON) {
        sentenceParser.addToken(token)
        if (sentenceParser.isDefinition()) {
          ZatlinDefinition definition = sentenceParser.readDefinition()
          ZatlinToken identifier = definition.getIdentifier()
          if (!$root.containsDefinitionOf(identifier.getText())) {
            $root.getDefinitions().add(definition)
          } else {
            throw ZatlinParseException.new("Duplicate definition", identifier)
          }
        } else if (sentenceParser.isMainGeneratable()) {
          ZatlinGeneratable mainGeneratable = sentenceParser.readMainGeneratable()
          if (!$root.hasMainGeneratable()) {
            $root.setMainGeneratable(mainGeneratable)
          } else {
            throw ZatlinParseException.new("Duplicate definition of the main pattern", sentenceParser.getTokens().first())
          }
        } else {
          throw ZatlinParseException.new("Invalid sentence", token)
        }
        sentenceParser.clear()
      } else {
        sentenceParser.addToken(token)
      }
    }
    ensureSafety()
    $lexer.close()
    return $root
  }

  private void ensureSafety() {
    if (!$root.hasMainGeneratable()) {
      throw ZatlinParseException.new("No main pattern", (ZatlinToken)null)
    }
  }

}