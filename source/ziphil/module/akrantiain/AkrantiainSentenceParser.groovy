package ziphil.module.akrantiain

import groovy.transform.CompileStatic
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class AkrantiainSentenceParser {

  private List<AkrantiainToken> $tokens
  private Integer $pointer = 0

  public AkrantiainSentenceParser(List<AkrantiainToken> tokens) {
    $tokens = tokens
  }

  public AkrantiainEnvironment parseEnvironment() {
    if ($tokens.size() == 2 && $tokens[0].getType() == AkrantiainTokenType.ENVIRONMENT_LITERAL && $tokens[1].getType() == AkrantiainTokenType.SEMICOLON) {
      AkrantiainToken token = $tokens[0]
      try {
        AkrantiainEnvironment environment = AkrantiainEnvironment.valueOf(token.getText())
        return environment
      } catch (IllegalArgumentException exception) {
        throw AkrantiainParseException.new("No such setting identifier", token)
      }
    } else {
      throw AkrantiainParseException.new("Setting sentence must consist of only one setting identifier", $tokens[-1])
    }
  }

  public AkrantiainDefinition parseDefinition() {
    if ($tokens.size() >= 4 && $tokens[0].getType() == AkrantiainTokenType.IDENTIFIER && $tokens[1].getType() == AkrantiainTokenType.EQUAL) {
      $pointer += 2
      AkrantiainDefinition definition = AkrantiainDefinition.new()
      AkrantiainToken identifier = $tokens[0]
      AkrantiainDisjunction content = nextDisjunction()
      definition.setIdentifier(identifier)
      definition.setContent(content)
      AkrantiainToken token = $tokens[$pointer ++]
      AkrantiainTokenType tokenType = (token != null) ? token.getType() : null
      if (tokenType == AkrantiainTokenType.SEMICOLON) {
        return definition
      } else {
        throw AkrantiainParseException.new("Invalid identifier definition sentence", token)
      }
    } else {
      throw AkrantiainParseException.new("Invalid identifier definition sentence", $tokens[-1])
    }
  }

  public AkrantiainRule parseRule() {
    Boolean isBeforeArrow = true
    AkrantiainRule rule = AkrantiainRule.new()
    while (true) {
      AkrantiainToken token = $tokens[$pointer ++]
      AkrantiainTokenType tokenType = (token != null) ? token.getType() : null
      if (isBeforeArrow) {
        if (tokenType == AkrantiainTokenType.ARROW) {
          isBeforeArrow = false
        } else {
          $pointer --
          AkrantiainDisjunction selection = nextSelection()
          if (selection.isNegated()) {
            if (!rule.hasSelection() && !rule.hasLeftCondition()) {
              rule.setLeftCondition(selection)
            } else if (!rule.hasRightCondition()) {
              rule.setRightCondition(selection)
            } else {
              throw AkrantiainParseException.new("Condition must be at the beginning or end of the left hand of a rule definition sentence", token)
            }
          } else {
            if (!rule.hasRightCondition()) {
              rule.getSelections().add(selection)
            } else {
              throw AkrantiainParseException.new("Selection must not be at the right of the right condition", token)
            }
          }
        }
      } else {
        if (tokenType == AkrantiainTokenType.SLASH_LITERAL || tokenType == AkrantiainTokenType.DOLLAR) {
          rule.getPhonemes().add(token)
        } else if (tokenType == AkrantiainTokenType.SEMICOLON) {
          break
        } else {
          throw AkrantiainParseException.new("Only slash literals can be at the right hand of a rule definition sentence", token)
        }
      }
    }
    if (!rule.hasSelection()) {
      throw AkrantiainParseException.new("No selects", $tokens[-1])
    }
    if (!rule.isSizeValid()) {
      throw AkrantiainParseException.new("The number of phonemes is not equal to the number of selects excluding \"^\"", $tokens[-1])
    }
    return rule
  }

  private AkrantiainDisjunction nextDisjunction() {
    Integer firstPointer = $pointer
    AkrantiainDisjunction disjunction = AkrantiainDisjunction.new()
    AkrantiainSequence sequence = AkrantiainSequence.new()
    while (true) {
      AkrantiainToken token = $tokens[$pointer ++]
      AkrantiainTokenType tokenType = (token != null) ? token.getType() : null
      if (tokenType == AkrantiainTokenType.QUOTE_LITERAL || tokenType == AkrantiainTokenType.IDENTIFIER) {
        sequence.getMatchables().add(token)
      } else if (tokenType == AkrantiainTokenType.VERTICAL) {
        if (sequence.hasToken()) {
          disjunction.getMatchables().add(sequence)
          sequence = AkrantiainSequence.new()
        } else {
          throw AkrantiainParseException.new("Invalid disjunction expression", token)
        }
      } else {
        if (sequence.hasToken()) {
          $pointer --
          disjunction.getMatchables().add(sequence)
          break
        } else {
          throw AkrantiainParseException.new("Invalid disjunction expression", token)
        }
      }
    }
    return disjunction
  }

  private AkrantiainDisjunction nextSelection() {
    AkrantiainDisjunction selection = AkrantiainDisjunction.new()
    while (true) {
      AkrantiainToken token = $tokens[$pointer ++]
      AkrantiainTokenType tokenType = (token != null) ? token.getType() : null
      if (tokenType == AkrantiainTokenType.CIRCUMFLEX || tokenType == AkrantiainTokenType.IDENTIFIER || tokenType == AkrantiainTokenType.QUOTE_LITERAL) {
        AkrantiainSequence sequence = AkrantiainSequence.new()
        sequence.getMatchables().add(token)
        selection.getMatchables().add(sequence)
        break
      } else if (tokenType == AkrantiainTokenType.EXCLAMATION) {
        if (!selection.isNegated()) {
          selection.setNegated(true)
        } else {
          throw AkrantiainParseException.new("Duplicate negation", token)
        }
      } else if (tokenType == AkrantiainTokenType.OPEN_PAREN) {
        AkrantiainDisjunction disjunction = nextDisjunction()
        AkrantiainToken nextToken = $tokens[$pointer ++]
        AkrantiainTokenType nextTokenType = (nextToken != null) ? nextToken.getType() : null
        if (nextTokenType == AkrantiainTokenType.CLOSE_PAREN) {
          selection.setMatchables(disjunction.getMatchables())
          break
        } else {
          throw AkrantiainParseException.new("Invalid disjunction expression", token)
        }
      } else {
        throw AkrantiainParseException.new("Invalid condition or select", token)
      }
    }
    return selection
  }

  public Boolean isEnvironmentSentence() {
    for (AkrantiainToken token : $tokens) {
      if (token.getType() == AkrantiainTokenType.ENVIRONMENT_LITERAL) {
        return true
      }
    }
    return false
  }

  public Boolean isDefinitionSentence() {
    for (AkrantiainToken token : $tokens) {
      if (token.getType() == AkrantiainTokenType.EQUAL) {
        return true
      }
    }
    return false
  }

  public Boolean isRuleSentence() {
    for (AkrantiainToken token : $tokens) {
      if (token.getType() == AkrantiainTokenType.ARROW) {
        return true
      }
    }
    return false
  }

}