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

  public AkrantiainEnvironment fetchEnvironment() {
    if ($tokens.size() == 1 && $tokens[0].getType() == AkrantiainTokenType.ENVIRONMENT_LITERAL) {
      try {
        AkrantiainEnvironment environment = AkrantiainEnvironment.valueOf($tokens[0].getText())
        return environment
      } catch (IllegalArgumentException exception) {
        throw AkrantiainParseException.new("No such setting identifier")
      }
    } else {
      throw AkrantiainParseException.new("Setting sentence must consist of only one setting identifier")
    }
  }

  public AkrantiainDefinition fetchDefinition() {
    if ($tokens.size() >= 3 && $tokens.size() % 2 == 1 && $tokens[0].getType() == AkrantiainTokenType.IDENTIFIER && $tokens[1].getType() == AkrantiainTokenType.EQUAL) {
      $pointer += 2
      AkrantiainDefinition definition = AkrantiainDefinition.new()
      AkrantiainToken identifier = $tokens[0]
      List<AkrantiainToken> disjunctionTokens = nextLiteralDisjunction()
      definition.setIdentifier(identifier)
      definition.setLiterals(disjunctionTokens)
      if ($tokens[$pointer] == null) {
        return definition
      } else {
        throw AkrantiainParseException.new("Invalid sentence")
      }
    } else {
      throw AkrantiainParseException.new("Invalid identifier definition sentence")
    }
  }

  public AkrantiainRule fetchRule() {
    Boolean isBeforeArrow = true
    AkrantiainRule rule = AkrantiainRule.new()
    for (AkrantiainToken token ; (token = $tokens[$pointer ++]) != null ;) {
      AkrantiainTokenType tokenType = token.getType()
      if (isBeforeArrow) {
        if (tokenType == AkrantiainTokenType.ARROW) {
          isBeforeArrow = false
        } else {
          $pointer --
          AkrantiainRuleGroup selection = nextSelection()
          if (selection.isNegated()) {
            if (!rule.hasSelection() && !rule.hasLeftCondition()) {
              rule.setLeftCondition(selection)
            } else if (!rule.hasRightCondition()) {
              rule.setRightCondition(selection)
            } else {
              throw AkrantiainParseException.new("Condition must be at the beginning or end of the left hand of a rule definition sentence")
            }
          } else {
            if (!rule.hasRightCondition()) {
              rule.getSelections().add(selection)
            } else {
              throw AkrantiainParseException.new("Selection must not be at the right of the right condition")
            }
          }
        }
      } else {
        if (tokenType == AkrantiainTokenType.SLASH_LITERAL || tokenType == AkrantiainTokenType.DOLLAR) {
          rule.getPhonemes().add(token)
        } else {
          throw AkrantiainParseException.new("Only slash literals can be at the right hand of a rule definition sentence")
        }
      }
    }
    if (!rule.hasSelection()) {
      throw AkrantiainParseException.new("No selects")
    }
    if (!rule.isSizeValid()) {
      throw AkrantiainParseException.new("The number of phonemes is not equal to the number of selects excluding ^")
    }
    return rule
  }

  private List<AkrantiainToken> nextLiteralDisjunction() {
    Integer firstPointer = $pointer
    List<AkrantiainToken> disjunctionTokens = ArrayList.new()
    for (AkrantiainToken token ; (token = $tokens[$pointer ++]) != null ;) {
      AkrantiainTokenType tokenType = token.getType()
      if (tokenType == AkrantiainTokenType.QUOTE_LITERAL) {
        if (($pointer - firstPointer) % 2 == 1) {
          disjunctionTokens.add(token)
        } else {
          throw AkrantiainParseException.new("Invalid sentence")
        }
      } else if (tokenType == AkrantiainTokenType.VERTICAL) {
        if (($pointer - firstPointer) % 2 != 0) {
          throw AkrantiainParseException.new("Invalid sentence")
        }
      } else {
        if (($pointer - firstPointer) % 2 == 0) {
          $pointer --
          break
        } else {
          throw AkrantiainParseException.new("Invalid sentence")
        }
      }
    }
    return disjunctionTokens
  }

  private AkrantiainRuleGroup nextSelection() {
    AkrantiainRuleGroup selection = AkrantiainRuleGroup.new()
    for (AkrantiainToken token ; (token = $tokens[$pointer ++]) != null ;) {
      AkrantiainTokenType tokenType = token.getType()
      if (tokenType == AkrantiainTokenType.CIRCUMFLEX || tokenType == AkrantiainTokenType.IDENTIFIER || tokenType == AkrantiainTokenType.QUOTE_LITERAL) {
        selection.getTokens().add(token)
        break
      } else if (tokenType == AkrantiainTokenType.EXCLAMATION) {
        if (!selection.isNegated()) {
          selection.setNegated(true)
        } else {
          throw AkrantiainParseException.new("Duplicate negation")
        }
      } else if (tokenType == AkrantiainTokenType.OPEN_PAREN) {
        List<AkrantiainToken> disjunctionTokens = nextLiteralDisjunction()
        AkrantiainToken nextToken = $tokens[$pointer ++]
        AkrantiainTokenType nextTokenType = nextToken.getType()
        if (nextTokenType == AkrantiainTokenType.CLOSE_PAREN) {
          selection.setTokens(disjunctionTokens)
          break
        } else {
          throw AkrantiainParseException.new("Invalid sentence")
        }
      } else {
        throw AkrantiainParseException.new("Invalid sentence")
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