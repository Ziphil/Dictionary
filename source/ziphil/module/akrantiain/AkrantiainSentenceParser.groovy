package ziphil.module.akrantiain

import groovy.transform.CompileStatic
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class AkrantiainSentenceParser {

  private List<AkrantiainToken> $tokens = ArrayList.new()
  private Int $pointer = 0

  public AkrantiainSentenceParser(List<AkrantiainToken> tokens) {
    $tokens = tokens
  }

  public AkrantiainSentenceParser() {
  }

  public AkrantiainEnvironment readEnvironment() {
    if ($tokens.size() == 2 && $tokens[0].getType() == AkrantiainTokenType.ENVIRONMENT_LITERAL && $tokens[1].getType() == AkrantiainTokenType.SEMICOLON) {
      AkrantiainToken token = $tokens[0]
      if (AkrantiainEnvironment.contains(token.getText())) {
        AkrantiainEnvironment environment = AkrantiainEnvironment.valueOfName(token.getText())
        return environment
      } else {
        return null
      }
    } else {
      throw AkrantiainParseException.new("Invalid definition sentence of setting specifier", $tokens.last())
    }
  }

  public AkrantiainDefinition readDefinition() {
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
        throw AkrantiainParseException.new("Invalid definition sentence of identifier", token)
      }
    } else {
      throw AkrantiainParseException.new("Invalid definition sentence of identifier", $tokens.last())
    }
  }

  public AkrantiainRule readRule() {
    Boolean beforeArrow = true
    AkrantiainRule rule = AkrantiainRule.new()
    while (true) {
      AkrantiainToken token = $tokens[$pointer ++]
      AkrantiainTokenType tokenType = (token != null) ? token.getType() : null
      if (beforeArrow) {
        if (tokenType == AkrantiainTokenType.ARROW) {
          beforeArrow = false
        } else {
          $pointer --
          AkrantiainDisjunction selection = nextSelection()
          if (selection.isNegated()) {
            if (!rule.hasSelection() && !rule.hasLeftCondition()) {
              rule.setLeftCondition(selection)
            } else if (!rule.hasRightCondition()) {
              rule.setRightCondition(selection)
            } else {
              throw AkrantiainParseException.new("Invalid definition sentence of rule", token)
            }
          } else {
            if (!rule.hasRightCondition()) {
              rule.getSelections().add(selection)
            } else {
              throw AkrantiainParseException.new("Invalid definition sentence of rule", token)
            }
          }
        }
      } else {
        if (tokenType == AkrantiainTokenType.SLASH_LITERAL || tokenType == AkrantiainTokenType.DOLLAR) {
          rule.getPhonemes().add(token)
        } else if (tokenType == AkrantiainTokenType.SEMICOLON) {
          break
        } else {
          throw AkrantiainParseException.new("Invalid definition sentence of rule", token)
        }
      }
    }
    if (!rule.hasSelection()) {
      throw AkrantiainParseException.new("No selects", $tokens.last())
    }
    if (!rule.isSizeValid()) {
      throw AkrantiainParseException.new("Mismatched number of concrete terms", $tokens.last())
    }
    if (!rule.isConcrete()) {
      throw AkrantiainParseException.new("Right side of a sentence consists solely of dollars", $tokens.last())
    }
    modifyConditions(rule)
    return rule
  }

  public List<AkrantiainModuleName> readModuleChain() {
    List<AkrantiainModuleName> moduleChain = ArrayList.new()
    Boolean afterComponent = false
    if ($tokens[0].getType() == AkrantiainTokenType.DOUBLE_PERCENT) {
      $pointer += 1
      while (true) {
        AkrantiainToken token = $tokens[$pointer ++]
        AkrantiainTokenType tokenType = (token != null) ? token.getType() : null
        if (tokenType == AkrantiainTokenType.ADVANCE) {
          if (afterComponent) {
            afterComponent = false
          } else {
            throw AkrantiainParseException.new("Invalid module chain", token)
          }
        } else if (tokenType == AkrantiainTokenType.SEMICOLON) {
          if (afterComponent) {
            break
          } else {
            throw AkrantiainParseException.new("Invalid module chain", token)
          }
        } else {
          if (!afterComponent) {
            $pointer --
            List<AkrantiainModuleName> moduleChainComponent = nextModuleChainComponent()
            moduleChain.addAll(moduleChainComponent)
            afterComponent = true
          }
        }
      }
    } else {
      throw AkrantiainParseException.new("Invalid module chain", $tokens.last())
    }
    return moduleChain
  }

  private void modifyConditions(AkrantiainRule rule) {
    modifyLeftCondition(rule)
    modifyRightCondition(rule)
  }

  private void modifyLeftCondition(AkrantiainRule rule) {
    AkrantiainCondition leftCondition = AkrantiainCondition.new()
    if (rule.hasLeftCondition()) {
      leftCondition.getMatchables().add(rule.getLeftCondition())
    }
    Int selectionIndex = 0
    for (Int i = 0 ; i < rule.getPhonemes().size() ; i ++) {
      if (rule.getPhonemes()[i].getType() == AkrantiainTokenType.DOLLAR) {
        Boolean first = true
        while (true) {
          AkrantiainMatchable selection = rule.getSelections()[selectionIndex]
          if (first || !selection.isConcrete()) {
            leftCondition.getMatchables().add(selection)
            selectionIndex ++
            first = false
          } else {
            break
          }
        }
      } else {
        for (Int j = 0 ; j < selectionIndex ; j ++) {
          rule.getSelections().removeAt(0)
        }
        for (Int j = 0 ; j < i ; j ++) {
          rule.getPhonemes().removeAt(0)
        }
        break
      }
    }
    if (!leftCondition.getMatchables().isEmpty()) {
      rule.setLeftCondition(leftCondition)
    } else {
      rule.setLeftCondition(null)
    }
  }

  private void modifyRightCondition(AkrantiainRule rule) {
    AkrantiainCondition rightCondition = AkrantiainCondition.new()
    if (rule.hasRightCondition()) {
      rightCondition.getMatchables().add(rule.getRightCondition())
    }
    Int selectionIndex = rule.getSelections().size() - 1
    for (Int i = rule.getPhonemes().size() - 1 ; i >= 0 ; i --) {
      if (rule.getPhonemes()[i].getType() == AkrantiainTokenType.DOLLAR) {
        Boolean first = true
        while (true) {
          AkrantiainMatchable selection = rule.getSelections()[selectionIndex]
          if (first || !selection.isConcrete()) {
            rightCondition.getMatchables().add(0, selection)
            selectionIndex --
            first = false
          } else {
            break
          }
        }
      } else {
        for (Int j = rule.getSelections().size() - 1 ; j > selectionIndex ; j --) {
          rule.getSelections().removeAt(rule.getSelections().size() - 1)
        }
        for (Int j = rule.getPhonemes().size() - 1 ; j > i ; j --) {
          rule.getPhonemes().removeAt(rule.getPhonemes().size() - 1)
        }
        break
      }
    }
    if (!rightCondition.getMatchables().isEmpty()) {
      rule.setRightCondition(rightCondition)
    } else {
      rule.setRightCondition(null)
    }
  }

  private AkrantiainDisjunction nextDisjunction() {
    Int firstPointer = $pointer
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

  private List<AkrantiainModuleName> nextModuleChainComponent() {
    List<AkrantiainModuleName> moduleChainComponent = ArrayList.new()
    AkrantiainToken token = $tokens[$pointer ++]
    AkrantiainTokenType tokenType = (token != null) ? token.getType() : null
    if (tokenType == AkrantiainTokenType.OPEN_PAREN) {
      List<AkrantiainModuleName> partialModuleChainComponent = nextPartialModuleChainComponent()
      AkrantiainToken nextToken = $tokens[$pointer ++]
      AkrantiainTokenType nextTokenType = (nextToken != null) ? nextToken.getType() : null
      if (nextTokenType == AkrantiainTokenType.CLOSE_PAREN) {
        moduleChainComponent = partialModuleChainComponent
      } else {
        throw AkrantiainParseException.new("Invalid module chain component", token)
      }
    } else {
      $pointer --
      List<AkrantiainModuleName> partialModuleChainComponent = nextPartialModuleChainComponent()
      moduleChainComponent = partialModuleChainComponent
    }
    return moduleChainComponent
  }

  private List<AkrantiainModuleName> nextPartialModuleChainComponent() {
    List<AkrantiainModuleName> moduleChainComponent = ArrayList.new()
    AkrantiainModuleName currentModuleName = AkrantiainModuleName.new()
    Boolean afterIdentifier = false
    Boolean compound = false
    while (true) {
      AkrantiainToken token = $tokens[$pointer ++]
      AkrantiainTokenType tokenType = (token != null) ? token.getType() : null
      if (tokenType == AkrantiainTokenType.IDENTIFIER) {
        if (!afterIdentifier) {
          currentModuleName.getTokens().add(token)
          afterIdentifier = true
          if (compound) {
            moduleChainComponent.add(currentModuleName)
            currentModuleName = AkrantiainModuleName.new()
            currentModuleName.getTokens().add(token)
          }
        } else {
          throw AkrantiainParseException.new("Invalid module chain component", token)
        }
      } else if (tokenType == AkrantiainTokenType.BOLD_ARROW) {
        if (afterIdentifier) {
          currentModuleName.getTokens().add(token)
          afterIdentifier = false
          compound = true
        } else {
          throw AkrantiainParseException.new("Invalid module chain component", token)
        }
      } else {
        if (afterIdentifier) {
          if (!compound && !currentModuleName.getTokens().isEmpty()) {
            moduleChainComponent.add(currentModuleName)
          }
          if (!moduleChainComponent.isEmpty()) {
            $pointer --
            break
          } else {
            throw AkrantiainParseException.new("Empty module chain component", token)
          }
        } else {
          throw AkrantiainParseException.new("Invalid module chain component", token)
        }
      }
    }
    return moduleChainComponent
  }

  public Boolean isEnvironment() {
    for (AkrantiainToken token : $tokens) {
      if (token.getType() == AkrantiainTokenType.ENVIRONMENT_LITERAL) {
        return true
      }
    }
    return false
  }

  public Boolean isDefinition() {
    for (AkrantiainToken token : $tokens) {
      if (token.getType() == AkrantiainTokenType.EQUAL) {
        return true
      }
    }
    return false
  }

  public Boolean isRule() {
    for (AkrantiainToken token : $tokens) {
      if (token.getType() == AkrantiainTokenType.ARROW) {
        return true
      }
    }
    return false
  }

  public Boolean isModuleChain() {
    return !$tokens.isEmpty() && $tokens[0].getType() == AkrantiainTokenType.DOUBLE_PERCENT
  }

  public void clear() {
    $tokens.clear()
    $pointer = 0
  }

  public void addToken(AkrantiainToken token) {
    $tokens.add(token)
  }

  public List<AkrantiainToken> getTokens() {
    return $tokens
  }

  public void setTokens(List<AkrantiainToken> tokens) {
    $tokens = tokens
  }

}