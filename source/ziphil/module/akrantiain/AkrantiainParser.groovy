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

  public AkrantiainRoot readRoot() {
    AkrantiainModule currentModule = $root.getDefaultModule()
    AkrantiainSentenceParser sentenceParser = AkrantiainSentenceParser.new() 
    Boolean isInModule = false
    for (AkrantiainToken token ; (token = $lexer.nextToken()) != null ;) {
      AkrantiainTokenType tokenType = token.getType()
      if (tokenType == AkrantiainTokenType.PERCENT) {
        currentModule = nextModule()
        isInModule = true
      } else if (tokenType == AkrantiainTokenType.CLOSE_CURLY) {
        if (isInModule) {
          currentModule = $root.getDefaultModule()
          isInModule = false
        } else {
          throw AkrantiainParseException.new("Unexpected close curly bracket", token)
        }
      } else if (tokenType == AkrantiainTokenType.SEMICOLON) {
        sentenceParser.addToken(token)
        if (sentenceParser.isEnvironment()) {
          AkrantiainEnvironment environment = sentenceParser.readEnvironment()
          currentModule.getEnvironments().add(environment)
        } else if (sentenceParser.isDefinition()) {
          AkrantiainDefinition definition = sentenceParser.readDefinition()
          AkrantiainToken identifier = definition.getIdentifier()
          if (!currentModule.containsDefinitionOf(identifier)) {
            currentModule.getDefinitions().add(definition)
          } else {
            throw AkrantiainParseException.new("Duplicate identifier", identifier)
          }
        } else if (sentenceParser.isRule()) {
          AkrantiainRule rule = sentenceParser.readRule()
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

  public AkrantiainModule nextModule() {
    List<AkrantiainToken> moduleName = nextModuleName()
    if (!$root.containsModuleOf(moduleName)) {
      AkrantiainModule module = AkrantiainModule.new()
      module.setName(moduleName)
      return module
    } else {
      throw AkrantiainParseException.new("Duplicate module name", moduleName.last())
    }
  }

  public List<AkrantiainToken> nextModuleName() {
    List<AkrantiainToken> moduleName = ArrayList.new()
    for (AkrantiainToken token ; (token = $lexer.nextToken()) != null ;) {
      AkrantiainTokenType tokenType = token.getType()
      if (tokenType == AkrantiainTokenType.OPEN_CURLY) {
        if (!moduleName.isEmpty()) {
          break
        } else {
          throw AkrantiainParseException.new("Module must have a name", token)
        }
      } else {
        Integer moduleNameSize = moduleName.size()
        if (moduleNameSize == 0 || moduleNameSize == 2) {
          if (tokenType == AkrantiainTokenType.IDENTIFIER) {
            moduleName.add(token)
          } else {
            throw AkrantiainParseException.new("Invalid module name", token)
          }
        } else if (moduleNameSize == 1) {
          if (tokenType == AkrantiainTokenType.BOLD_ARROW) {
            moduleName.add(token)
          } else {
            throw AkrantiainParseException.new("Invalid module name", token)
          }
        } else {
          throw AkrantiainParseException.new("Invalid module name", token)
        }
      }
    }
    return moduleName
  }

}