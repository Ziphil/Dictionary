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
    Boolean inModule = false
    for (AkrantiainToken token ; (token = $lexer.nextToken()) != null ;) {
      AkrantiainTokenType tokenType = token.getType()
      if (tokenType == AkrantiainTokenType.PERCENT) {
        if (!inModule) {
          currentModule = nextModule()
          $root.getModules().add(currentModule)
          inModule = true
        } else {
          throw AkrantiainParseException.new("Module definition cannot be nested", token)
        }
      } else if (tokenType == AkrantiainTokenType.OPEN_CURLY) {
        throw AkrantiainParseException.new("Unexpected left curly bracket", token)
      } else if (tokenType == AkrantiainTokenType.CLOSE_CURLY) {
        if (inModule) {
          currentModule = $root.getDefaultModule()
          inModule = false
        } else {
          throw AkrantiainParseException.new("Unexpected right curly bracket", token)
        }
      } else if (tokenType == AkrantiainTokenType.SEMICOLON) {
        sentenceParser.addToken(token)
        if (sentenceParser.isEnvironment()) {
          AkrantiainEnvironment environment = sentenceParser.readEnvironment()
          if (environment != null) {
            currentModule.getEnvironments().add(environment)
          } else {
            AkrantiainWarning warning = AkrantiainWarning.new("No such setting identifier", sentenceParser.getTokens().first())
            $root.getWarnings().add(warning)
          }
        } else if (sentenceParser.isDefinition()) {
          AkrantiainDefinition definition = sentenceParser.readDefinition()
          AkrantiainToken identifier = definition.getIdentifier()
          if (!currentModule.containsDefinitionOf(identifier.getText())) {
            currentModule.getDefinitions().add(definition)
          } else {
            throw AkrantiainParseException.new("Duplicate identifier", identifier)
          }
        } else if (sentenceParser.isRule()) {
          AkrantiainRule rule = sentenceParser.readRule()
          if (rule.isConcrete()) {
            currentModule.getRules().add(rule)
          } else {
            throw AkrantiainParseException.new("Right side of a sentence consists solely of dollars", token)
          }
        } else if (sentenceParser.isModuleChain()) {
          List<AkrantiainModuleName> moduleChain = sentenceParser.readModuleChain()
          currentModule.setModuleChain(moduleChain)
        } else {
          throw AkrantiainParseException.new("Invalid sentence", token)
        }
        sentenceParser.clear()
      } else {
        sentenceParser.addToken(token)
      }
    }
    if (inModule) {
      throw AkrantiainParseException.new("The file ended before a module is closed")
    }
    ensureSafety()
    $lexer.close()
    return $root
  }

  private void ensureSafety() {
    AkrantiainToken deadIdentifier = $root.findUnknownIdentifier()
    if (deadIdentifier != null) {
      throw AkrantiainParseException.new("No such identifier", deadIdentifier)
    }
    AkrantiainToken circularIdentifier = $root.findCircularIdentifier()
    if (circularIdentifier != null) {
      throw AkrantiainParseException.new("Circular identifier definition", circularIdentifier)
    }
    AkrantiainModuleName deadModuleName = $root.findUnknownModuleName()
    if (deadModuleName != null) {
      throw AkrantiainParseException.new("No such module", deadModuleName.getTokens())
    }
    AkrantiainModuleName circularModuleName = $root.findCircularModuleName()
    if (circularModuleName != null) {
      throw AkrantiainParseException.new("Circular module definition", circularModuleName.getTokens())
    }
    List<AkrantiainModuleName> unusedModuleNames = $root.findUnusedModuleNames()
    for (AkrantiainModuleName unusedModuleName : unusedModuleNames) {
      AkrantiainWarning warning = AkrantiainWarning.new("Unused module", unusedModuleName.getTokens())
      $root.getWarnings().add(warning)
    }
  }

  public AkrantiainModule nextModule() {
    AkrantiainModuleName moduleName = nextModuleName()
    if (!$root.containsModuleOf(moduleName)) {
      AkrantiainModule module = AkrantiainModule.new()
      module.setName(moduleName)
      return module
    } else {
      throw AkrantiainParseException.new("Duplicate module name", moduleName.getTokens())
    }
  }

  public AkrantiainModuleName nextModuleName() {
    AkrantiainModuleName moduleName = AkrantiainModuleName.new()
    for (AkrantiainToken token ; (token = $lexer.nextToken()) != null ;) {
      AkrantiainTokenType tokenType = token.getType()
      if (tokenType == AkrantiainTokenType.OPEN_CURLY) {
        if (!moduleName.getTokens().isEmpty()) {
          break
        } else {
          throw AkrantiainParseException.new("Module must have a name", token)
        }
      } else {
        Integer moduleNameSize = moduleName.getTokens().size()
        if (moduleNameSize == 0 || moduleNameSize == 2) {
          if (tokenType == AkrantiainTokenType.IDENTIFIER) {
            moduleName.getTokens().add(token)
          } else {
            throw AkrantiainParseException.new("Invalid module name", token)
          }
        } else if (moduleNameSize == 1) {
          if (tokenType == AkrantiainTokenType.BOLD_ARROW) {
            moduleName.getTokens().add(token)
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