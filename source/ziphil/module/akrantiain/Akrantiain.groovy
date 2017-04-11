package ziphil.module.akrantiain

import groovy.transform.CompileStatic
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class Akrantiain {

  public static final String PUNCTUATION_IDENTIIER_NAME = "PUNCTUATION"

  private AkrantiainSetting $setting = AkrantiainSetting.new()

  public void load(File file) {
    Reader reader = InputStreamReader.new(FileInputStream.new(file), "UTF-8")
    parse(reader)
  }

  public void load(String source) {
    Reader reader = StringReader.new(source)
    parse(reader)
  }

  private void parse(Reader reader) {
    AkrantiainLexer lexer = AkrantiainLexer.new(reader)
    List<AkrantiainToken> currentTokens = ArrayList.new()
    for (AkrantiainToken token ; (token = lexer.nextToken()) != null ;) {
      currentTokens.add(token)
      if (token.getType() == AkrantiainTokenType.SEMICOLON) {
        AkrantiainSentenceParser parser = AkrantiainSentenceParser.new(currentTokens)
        if (parser.isEnvironment()) {
          AkrantiainEnvironment environment = parser.parseEnvironment()
          $setting.getEnvironments().add(environment)
        } else if (parser.isDefinition()) {
          AkrantiainDefinition definition = parser.parseDefinition()
          AkrantiainToken identifier = definition.getIdentifier()
          if (!$setting.containsIdentifier(identifier)) {
            $setting.getDefinitions().add(definition)
          } else {
            throw AkrantiainParseException.new("Duplicate identifier", identifier)
          }
        } else if (parser.isRule()) {
          AkrantiainRule rule = parser.parseRule()
          $setting.getRules().add(rule)
        } else {
          throw AkrantiainParseException.new("Invalid sentence", currentTokens[-1])
        }
        currentTokens.clear()
      }
    }
    reader.close()
    lexer.close()
  }

  public String convert(String input) {
    AkrantiainElementGroup currentGroup = AkrantiainElementGroup.create(input)
    for (AkrantiainRule rule : $setting.getRules()) {
      currentGroup = rule.apply(currentGroup, $setting)
    }
    List<AkrantiainElement> invalidElements = currentGroup.invalidElements($setting)
    if (invalidElements.isEmpty()) {
      return currentGroup.createOutput()
    } else {
      throw AkrantiainException.new("No rules that can handle some characters", invalidElements)
    }
  }

}