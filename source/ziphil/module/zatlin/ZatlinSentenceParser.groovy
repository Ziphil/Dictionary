package ziphil.module.zatlin

import groovy.transform.CompileStatic
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class ZatlinSentenceParser {

  private List<ZatlinToken> $tokens = ArrayList.new()
  private Int $pointer = 0

  public ZatlinSentenceParser(List<ZatlinToken> tokens) {
    $tokens = tokens
  }

  public ZatlinSentenceParser() {
  }

  public ZatlinDefinition readDefinition() {
    if ($tokens.size() >= 4 && $tokens[0].getType() == ZatlinTokenType.IDENTIFIER && $tokens[1].getType() == ZatlinTokenType.EQUAL) {
      $pointer += 2
      ZatlinDefinition definition = ZatlinDefinition.new()
      ZatlinToken identifier = $tokens[0]
      ZatlinGeneratable content = nextCompound()
      definition.setIdentifier(identifier)
      definition.setContent(content)
      ZatlinToken token = $tokens[$pointer ++]
      ZatlinTokenType tokenType = (token != null) ? token.getType() : null
      if (tokenType == ZatlinTokenType.SEMICOLON) {
        return definition
      } else {
        throw ZatlinParseException.new("Invalid definition sentence", token)
      }
    } else {
      throw ZatlinParseException.new("Invalid definition sentence", $tokens.last())
    }
  }

  public ZatlinGeneratable readMainGeneratable() {
    if ($tokens.size() >= 3 && $tokens[0].getType() == ZatlinTokenType.PERCENT) {
      $pointer += 1
      ZatlinGeneratable mainGeneratable = nextCompound()
      ZatlinToken token = $tokens[$pointer ++]
      ZatlinTokenType tokenType = (token != null) ? token.getType() : null
      if (tokenType == ZatlinTokenType.SEMICOLON) {
        return mainGeneratable
      } else {
        throw ZatlinParseException.new("Invalid definition sentence", token)
      }
    } else {
      throw ZatlinParseException.new("Invalid definition sentence", $tokens.last())
    }
  }

  private ZatlinCompound nextCompound() {
    ZatlinCompound compound = ZatlinCompound.new()
    ZatlinGeneratable generatable = nextSelection()
    ZatlinToken token = $tokens[$pointer ++]
    ZatlinTokenType tokenType = (token != null) ? token.getType() : null
    if (tokenType == ZatlinTokenType.MINUS) {
      ZatlinMatchable matchable = nextDisjunction()
      compound.setGeneratable(generatable)
      compound.setMatchable(matchable)
    } else if (tokenType == ZatlinTokenType.SEMICOLON) {
      $pointer --
      compound.setGeneratable(generatable)
    } else {
      throw ZatlinParseException.new("Invalid definition sentence", token)
    }
    return compound
  }

  private ZatlinSelection nextSelection() {
    ZatlinSelection selection = ZatlinSelection.new()
    ZatlinSequence sequence = ZatlinSequence.new()
    Int weight = 1
    Boolean hasWeight = false
    while (true) {
      ZatlinToken token = $tokens[$pointer ++]
      ZatlinTokenType tokenType = (token != null) ? token.getType() : null
      if (tokenType == ZatlinTokenType.QUOTE_LITERAL || tokenType == ZatlinTokenType.IDENTIFIER) {
        if (!hasWeight) {
          sequence.getGeneratables().add(token)
        } else {
          throw ZatlinParseException.new("Weight is not at the rightmost", token)
        }
      } else if (tokenType == ZatlinTokenType.NUMERIC) {
        weight = IntegerClass.parseInt(token.getText())
        hasWeight = true
      } else if (tokenType == ZatlinTokenType.VERTICAL) {
        if (sequence.hasGeneratable()) {
          selection.getGeneratables().add(sequence)
          selection.getWeights().add(weight)
          sequence = ZatlinSequence.new()
          weight = 1
          hasWeight = false
        } else {
          throw ZatlinParseException.new("Invalid selection expression", token)
        }
      } else {
        if (sequence.hasGeneratable()) {
          $pointer --
          selection.getGeneratables().add(sequence)
          selection.getWeights().add(weight)
          break
        } else {
          throw ZatlinParseException.new("Invalid selection expression", token)
        }
      }
    }
    return selection
  }

  private ZatlinDisjunction nextDisjunction() {
    ZatlinDisjunction disjunction = ZatlinDisjunction.new()
    ZatlinPattern pattern = ZatlinPattern.new()
    while (true) {
      ZatlinToken token = $tokens[$pointer ++]
      ZatlinTokenType tokenType = (token != null) ? token.getType() : null
      if (tokenType == ZatlinTokenType.QUOTE_LITERAL) {
        if (!pattern.hasToken()) {
          pattern.setToken(token)
        } else {
          throw ZatlinParseException.new("Two or more quote literals in the single expression", token)
        }
      } else if (tokenType == ZatlinTokenType.CIRCUMFLEX) {
        if (pattern.hasToken()) {
          if (!pattern.isTrailing()) {
            pattern.setTrailing(true)
          } else {
            throw ZatlinParseException.new("Duplicate circumflex", token)
          }
        } else {
          if (!pattern.isLeading()) {
            pattern.setLeading(true)
          } else {
            throw ZatlinParseException.new("Duplicate circumflex", token)
          }
        }
      } else if (tokenType == ZatlinTokenType.VERTICAL) {
        if (pattern.hasToken()) {
          disjunction.getMatchables().add(pattern)
          pattern = ZatlinPattern.new()
        } else {
          throw ZatlinParseException.new("Invalid disjunction expression", token)
        }
      } else {
        if (pattern.hasToken()) {
          $pointer --
          disjunction.getMatchables().add(pattern)
          break
        } else {
          throw ZatlinParseException.new("Invalid disjunction expression", token)
        }
      }
    }
    return disjunction
  }

  public Boolean isDefinition() {
    return !$tokens.isEmpty() && $tokens[0].getType() != ZatlinTokenType.PERCENT
  }

  public Boolean isMainGeneratable() {
    return !$tokens.isEmpty() && $tokens[0].getType() == ZatlinTokenType.PERCENT
  }

  public void clear() {
    $tokens.clear()
    $pointer = 0
  }

  public void addToken(ZatlinToken token) {
    $tokens.add(token)
  }

  public List<ZatlinToken> getTokens() {
    return $tokens
  }

  public void setTokens(List<ZatlinToken> tokens) {
    $tokens = tokens
  }

}