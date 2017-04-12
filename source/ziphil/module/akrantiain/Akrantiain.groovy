package ziphil.module.akrantiain

import groovy.transform.CompileStatic
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class Akrantiain {

  public static final String PUNCTUATION_IDENTIIER_NAME = "PUNCTUATION"

  private AkrantiainRoot $root = AkrantiainRoot.new()

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
          $root.getDefaultModule().getEnvironments().add(environment)
        } else if (parser.isDefinition()) {
          AkrantiainDefinition definition = parser.parseDefinition()
          AkrantiainToken identifier = definition.getIdentifier()
          if (!$root.getDefaultModule().containsIdentifier(identifier)) {
            $root.getDefaultModule().getDefinitions().add(definition)
          } else {
            throw AkrantiainParseException.new("Duplicate identifier", identifier)
          }
        } else if (parser.isRule()) {
          AkrantiainRule rule = parser.parseRule()
          $root.getDefaultModule().getRules().add(rule)
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
    for (AkrantiainRule rule : $root.getDefaultModule().getRules()) {
      currentGroup = rule.apply(currentGroup, $root.getDefaultModule())
    }
    List<AkrantiainElement> invalidElements = currentGroup.invalidElements($root.getDefaultModule())
    if (invalidElements.isEmpty()) {
      return currentGroup.createOutput()
    } else {
      throw AkrantiainException.new("No rules that can handle some characters", invalidElements)
    }
  }

}