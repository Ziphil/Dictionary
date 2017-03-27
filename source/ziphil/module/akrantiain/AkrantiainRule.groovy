package ziphil.module.akrantiain

import groovy.transform.CompileStatic
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class AkrantiainRule {

  private List<AkrantiainRuleGroup> $selections = ArrayList.new()
  private AkrantiainRuleGroup $leftCondition = null
  private AkrantiainRuleGroup $rightCondition = null
  private List<AkrantiainToken> $phonemes = ArrayList.new()

  public String toString() {
    StringBuilder string = StringBuilder.new()
    string.append($leftCondition)
    string.append(" [")
    for (Integer i : 0 ..< $selections.size()) {
      string.append($selections[i])
      if (i < $selections.size() - 1) {
        string.append(", ")
      }
    }
    string.append("] ")
    string.append($rightCondition)
    string.append(" -> ")
    string.append(" [")
    for (Integer i : 0 ..< $phonemes.size()) {
      string.append($phonemes[i])
      if (i < $phonemes.size() - 1) {
        string.append(", ")
      }
    }
    string.append("]")
    return string.toString()
  }

  public Boolean isSizeValid() {
    Integer phonemeSize = $phonemes.size()
    Integer concreteSelectionSize = 0
    for (AkrantiainRuleGroup selection : $selections) {
      if (!selection.isSingleton() || selection.getToken().getType() != AkrantiainTokenType.CIRCUMFLEX) {
        concreteSelectionSize ++
      }
    }
    return phonemeSize == concreteSelectionSize
  }

  public Boolean hasSelection() {
    return !$selections.isEmpty()
  }

  public Boolean hasLeftCondition() {
    return $leftCondition != null
  }

  public Boolean hasRightCondition() {
    return $rightCondition != null
  }

  public List<AkrantiainRuleGroup> getSelections() {
    return $selections
  }

  public void setSelections(List<AkrantiainRuleGroup> selections) {
    $selections = selections
  }

  public AkrantiainRuleGroup getLeftCondition() {
    return $leftCondition
  }

  public void setLeftCondition(AkrantiainRuleGroup leftCondition) {
    $leftCondition = leftCondition
  }

  public AkrantiainRuleGroup getRightCondition() {
    return $rightCondition
  }

  public void setRightCondition(AkrantiainRuleGroup rightCondition) {
    $rightCondition = rightCondition
  }

  public List<AkrantiainToken> getPhonemes() {
    return $phonemes
  }

  public void setPhonemes(List<AkrantiainToken> phonemes) {
    $phonemes = phonemes
  } 

}