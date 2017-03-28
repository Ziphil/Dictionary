package ziphil.module.akrantiain

import groovy.transform.CompileStatic
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class Akrantiain {

  private AkrantiainSetting $setting = AkrantiainSetting.new()

  public void load(File file) {
    BufferedReader reader = file.newReader("UTF-8")
    parse(reader)
  }

  public void load(String string) {
    BufferedReader reader = BufferedReader.new(StringReader.new(string))
    parse(reader)
  }

  private void parse(BufferedReader reader) {
    AkrantiainLexer lexer = AkrantiainLexer.new(reader)
    List<AkrantiainToken> currentTokens = ArrayList.new()
    for (AkrantiainToken token ; (token = lexer.nextToken()) != null ;) {
      if (token.getType() != AkrantiainTokenType.SEMICOLON) {
        currentTokens.add(token)
      } else {
        AkrantiainSentenceParser parser = AkrantiainSentenceParser.new(currentTokens)
        if (parser.isEnvironmentSentence()) {
          AkrantiainEnvironment environment = parser.parseEnvironment()
          $setting.getEnvironments().add(environment)
        } else if (parser.isDefinitionSentence()) {
          AkrantiainDefinition definition = parser.parseDefinition()
          if (!$setting.containsIdentifier(definition.getIdentifier())) {
            $setting.getDefinitions().add(definition)
          } else {
            throw AkrantiainParseException.new("Duplicate identifier")
          }
        } else if (parser.isRuleSentence()) {
          AkrantiainRule rule = parser.parseRule()
          $setting.getRules().add(rule)
        } else {
          throw AkrantiainParseException.new("Invalid sentence")
        }
        currentTokens.clear()
      }
    }
    reader.close()
    lexer.close()
  }

  public String convert(String input) {
    AkrantiainElementGroup currentElements = AkrantiainElementGroup.create(input)
    for (AkrantiainRule rule : $setting.getRules()) {
      currentElements = rule.apply(currentElements, $setting)
    }
    return input
  }

}