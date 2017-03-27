package ziphil.module.akrantiain

import groovy.transform.CompileStatic
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class AkrantiainRule {

  private List<AkrantiainDisjunctionGroup> $selections = ArrayList.new()
  private AkrantiainDisjunctionGroup $leftCondition = null
  private AkrantiainDisjunctionGroup $rightCondition = null
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
    string.append(" -> [")
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
    for (AkrantiainDisjunctionGroup selection : $selections) {
      if (!selection.isSingleton() || !selection.getGroup().isSingleton() || selection.getGroup().getToken().getType() != AkrantiainTokenType.CIRCUMFLEX) {
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

  public List<AkrantiainDisjunctionGroup> getSelections() {
    return $selections
  }

  public void setSelections(List<AkrantiainDisjunctionGroup> selections) {
    $selections = selections
  }

  public AkrantiainDisjunctionGroup getLeftCondition() {
    return $leftCondition
  }

  public void setLeftCondition(AkrantiainDisjunctionGroup leftCondition) {
    $leftCondition = leftCondition
  }

  public AkrantiainDisjunctionGroup getRightCondition() {
    return $rightCondition
  }

  public void setRightCondition(AkrantiainDisjunctionGroup rightCondition) {
    $rightCondition = rightCondition
  }

  public List<AkrantiainToken> getPhonemes() {
    return $phonemes
  }

  public void setPhonemes(List<AkrantiainToken> phonemes) {
    $phonemes = phonemes
  } 

}