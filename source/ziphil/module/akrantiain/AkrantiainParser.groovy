package ziphil.module.akrantiain

import groovy.transform.CompileStatic
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class AkrantiainParser {

  private AkrantiainRoot $root = AkrantiainRoot.new()
  private AkrantiainLexer $lexer

  public AkrantiainParser(Reader reader) {
    $lexer = AkrantiainLexer.new(reader)
  }

  public AkrantiainRoot parse() {
    AkrantiainModule currentModule = $root.getDefaultModule()
    AkrantiainSentenceParser sentenceParser = AkrantiainSentenceParser.new() 
    for (AkrantiainToken token ; (token = $lexer.nextToken()) != null ;) {
      AkrantiainTokenType tokenType = token.getType()
      if (tokenType == AkrantiainTokenType.PERCENT) {
        throw AkrantiainParseException.new("Not yet implemented", token)
      } else if (tokenType == AkrantiainTokenType.SEMICOLON) {
        sentenceParser.addToken(token)
        if (sentenceParser.isEnvironment()) {
          AkrantiainEnvironment environment = sentenceParser.parseEnvironment()
          currentModule.getEnvironments().add(environment)
        } else if (sentenceParser.isDefinition()) {
          AkrantiainDefinition definition = sentenceParser.parseDefinition()
          AkrantiainToken identifier = definition.getIdentifier()
          if (!currentModule.containsIdentifier(identifier)) {
            currentModule.getDefinitions().add(definition)
          } else {
            throw AkrantiainParseException.new("Duplicate identifier", identifier)
          }
        } else if (sentenceParser.isRule()) {
          AkrantiainRule rule = sentenceParser.parseRule()
          currentModule.getRules().add(rule)
        } else {
          throw AkrantiainParseException.new("Invalid sentence", token)
        }
        sentenceParser.clear()
      } else {
        sentenceParser.addToken(token)
      }
    }
    $lexer.close()
    return $root
  }

}