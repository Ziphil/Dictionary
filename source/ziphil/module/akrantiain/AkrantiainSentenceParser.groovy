package ziphil.module.akrantiain

import groovy.transform.CompileStatic
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class AkrantiainSentenceParser {

  private List<AkrantiainToken> $tokens
  private Integer $pointer = 0
  private Integer $lineNumber

  public AkrantiainSentenceParser(List<AkrantiainToken> tokens) {
    $tokens = tokens
    $lineNumber = (!tokens.isEmpty()) ? tokens[0].getLineNumber() : null
  }

  public AkrantiainEnvironment parseEnvironment() {
    if ($tokens.size() == 1 && $tokens[0].getType() == AkrantiainTokenType.ENVIRONMENT_LITERAL) {
      try {
        AkrantiainEnvironment environment = AkrantiainEnvironment.valueOf($tokens[0].getText())
        return environment
      } catch (IllegalArgumentException exception) {
        throw AkrantiainParseException.new("No such setting identifier", $lineNumber)
      }
    } else {
      throw AkrantiainParseException.new("Setting sentence must consist of only one setting identifier", $lineNumber)
    }
  }

  public AkrantiainDefinition parseDefinition() {
    if ($tokens.size() >= 3 && $tokens[0].getType() == AkrantiainTokenType.IDENTIFIER && $tokens[1].getType() == AkrantiainTokenType.EQUAL) {
      $pointer += 2
      AkrantiainDefinition definition = AkrantiainDefinition.new()
      AkrantiainToken identifier = $tokens[0]
      AkrantiainDisjunctionGroup content = nextDisjunctionGroup()
      definition.setIdentifier(identifier)
      definition.setContent(content)
      if ($tokens[$pointer] == null) {
        return definition
      } else {
        throw AkrantiainParseException.new("Invalid identifier definition sentence", $lineNumber)
      }
    } else {
      throw AkrantiainParseException.new("Invalid identifier definition sentence", $lineNumber)
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
          AkrantiainDisjunctionGroup selection = nextSelection()
          if (selection.isNegated()) {
            if (!rule.hasSelection() && !rule.hasLeftCondition()) {
              rule.setLeftCondition(selection)
            } else if (!rule.hasRightCondition()) {
              rule.setRightCondition(selection)
            } else {
              throw AkrantiainParseException.new("Condition must be at the beginning or end of the left hand of a rule definition sentence", $lineNumber)
            }
          } else {
            if (!rule.hasRightCondition()) {
              rule.getSelections().add(selection)
            } else {
              throw AkrantiainParseException.new("Selection must not be at the right of the right condition", $lineNumber)
            }
          }
        }
      } else {
        if (tokenType == AkrantiainTokenType.SLASH_LITERAL || tokenType == AkrantiainTokenType.DOLLAR) {
          rule.getPhonemes().add(token)
        } else if (tokenType == null) {
          break
        } else {
          throw AkrantiainParseException.new("Only slash literals can be at the right hand of a rule definition sentence", $lineNumber)
        }
      }
    }
    if (!rule.hasSelection()) {
      throw AkrantiainParseException.new("No selects", $lineNumber)
    }
    if (!rule.isSizeValid()) {
      throw AkrantiainParseException.new("The number of phonemes is not equal to the number of selects excluding ^", $lineNumber)
    }
    return rule
  }

  private AkrantiainDisjunctionGroup nextDisjunctionGroup() {
    Integer firstPointer = $pointer
    AkrantiainDisjunctionGroup disjunctionGroup = AkrantiainDisjunctionGroup.new()
    AkrantiainTokenGroup currentTokenGroup = AkrantiainTokenGroup.new()
    while (true) {
      AkrantiainToken token = $tokens[$pointer ++]
      AkrantiainTokenType tokenType = (token != null) ? token.getType() : null
      if (tokenType == AkrantiainTokenType.QUOTE_LITERAL) {
        currentTokenGroup.getTokens().add(token)
      } else if (tokenType == AkrantiainTokenType.VERTICAL) {
        if (currentTokenGroup.hasToken()) {
          disjunctionGroup.getTokenGroups().add(currentTokenGroup)
          currentTokenGroup = AkrantiainTokenGroup.new()
        } else {
          throw AkrantiainParseException.new("Invalid disjunction expression", $lineNumber)
        }
      } else {
        if (currentTokenGroup.hasToken()) {
          $pointer --
          disjunctionGroup.getTokenGroups().add(currentTokenGroup)
          break
        } else {
          throw AkrantiainParseException.new("Invalid disjunction expression", $lineNumber)
        }
      }
    }
    return disjunctionGroup
  }

  private AkrantiainDisjunctionGroup nextSelection() {
    AkrantiainDisjunctionGroup selection = AkrantiainDisjunctionGroup.new()
    while (true) {
      AkrantiainToken token = $tokens[$pointer ++]
      AkrantiainTokenType tokenType = (token != null) ? token.getType() : null
      if (tokenType == AkrantiainTokenType.CIRCUMFLEX || tokenType == AkrantiainTokenType.IDENTIFIER || tokenType == AkrantiainTokenType.QUOTE_LITERAL) {
        AkrantiainTokenGroup tokenGroup = AkrantiainTokenGroup.new()
        tokenGroup.getTokens().add(token)
        selection.getTokenGroups().add(tokenGroup)
        break
      } else if (tokenType == AkrantiainTokenType.EXCLAMATION) {
        if (!selection.isNegated()) {
          selection.setNegated(true)
        } else {
          throw AkrantiainParseException.new("Duplicate negation", $lineNumber)
        }
      } else if (tokenType == AkrantiainTokenType.OPEN_PAREN) {
        AkrantiainDisjunctionGroup disjunctionGroup = nextDisjunctionGroup()
        AkrantiainToken nextToken = $tokens[$pointer ++]
        AkrantiainTokenType nextTokenType = (nextToken != null) ? nextToken.getType() : null
        if (nextTokenType == AkrantiainTokenType.CLOSE_PAREN) {
          selection.setTokenGroups(disjunctionGroup.getTokenGroups())
          break
        } else {
          throw AkrantiainParseException.new("Invalid disjunction expression", $lineNumber)
        }
      } else {
        throw AkrantiainParseException.new("Invalid condition or select", $lineNumber)
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