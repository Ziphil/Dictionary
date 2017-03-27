package ziphil.module.akrantiain

import groovy.transform.CompileStatic
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class Akrantiain {

  private EnumSet<AkrantiainEnvironment> $environments = EnumSet.noneOf(AkrantiainEnvironment)
  private List<AkrantiainDefinition> $definitions = ArrayList.new()
  private List<AkrantiainRule> $rules = ArrayList.new()

  public void load(File file) {
    BufferedReader reader = file.newReader("UTF-8")
    parse(reader)
  }

  public void load(String string) {
    BufferedReader reader = BufferedReader.new(StringReader.new(string))
    parse(reader)
  }

  private void parse(BufferedReader reader) {
    AkrantiainLexer lexer = AkrantiainLexer.new(reader, null)
    List<AkrantiainToken> currentTokens = ArrayList.new()
    for (AkrantiainToken token ; (token = lexer.nextToken()) != null ;) {
      if (token.getType() != AkrantiainTokenType.SEMICOLON) {
        currentTokens.add(token)
      } else {
        AkrantiainSentenceParser parser = AkrantiainSentenceParser.new(currentTokens)
        if (parser.isEnvironmentSentence()) {
          AkrantiainEnvironment environment = parser.fetchEnvironment()
          $environments.add(environment)
        } else if (parser.isDefinitionSentence()) {
          AkrantiainDefinition definition = parser.fetchDefinition()
          $definitions.add(definition)
        } else if (parser.isRuleSentence()) {
          AkrantiainRule rule = parser.fetchRule()
          $rules.add(rule)
        } else {
          throw AkrantiainParseException.new("Invalid sentence")
        }
        currentTokens.clear()
      }
    }
  }

  public String convert(String input) {
    return input
  }

}