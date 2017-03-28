package ziphil.module.akrantiain

import groovy.transform.CompileStatic
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class AkrantiainRule {

  private List<AkrantiainDisjunctionGroup> $selections = ArrayList.new()
  private AkrantiainDisjunctionGroup $leftCondition = null
  private AkrantiainDisjunctionGroup $rightCondition = null
  private List<AkrantiainToken> $phonemes = ArrayList.new()

  public AkrantiainElementGroup apply(AkrantiainElementGroup group, AkrantiainSetting setting) {
    AkrantiainElementGroup appliedGroup = AkrantiainElementGroup.new()
    Integer pointer = 0
    while (pointer < group.getElements().size()) {
      pointer = applySingle(group, appliedGroup, pointer, setting)
    }
    return appliedGroup
  }

  // ちょうど from で与えられた位置から規則を適用し、適用した結果を appliedGroup に追加します。
  // 規則がマッチして適用できた場合はマッチした範囲の右側のインデックス (範囲にそのインデックス自体は含まない) を返します。
  // そもそも規則にマッチせず適用できなかった場合は from の次の位置を返します。
  private Integer applySingle(AkrantiainElementGroup group, AkrantiainElementGroup appliedGroup, Integer from, AkrantiainSetting setting) {
    List<AkrantiainElement> addedElements = ArrayList.new()
    Integer pointer = from
    if ($leftCondition != null && !$leftCondition.matchLeftCondition(group, pointer, setting)) {
      appliedGroup.getElements().add(group.getElements()[from])
      return from + 1
    }
    Integer phonemeIndex = 0
    for (AkrantiainDisjunctionGroup selection : $selections) {
      Integer to = selection.matchSelection(group, pointer, setting)
      if (to != null) {
        if (selection.isConcrete()) {
          AkrantiainToken phoneme = $phonemes[phonemeIndex]
          AkrantiainTokenType phonemeType = phoneme.getType()
          if (phonemeType == AkrantiainTokenType.SLASH_LITERAL) {
            Boolean isNoneConverted = true
            for (Integer i : pointer ..< to) {
              if (group.getElements()[i].isConverted()) {
                isNoneConverted = false
              }
            }
            if (isNoneConverted) {
              AkrantiainElement addedElement = group.merge(pointer, to)
              addedElement.setResult(phoneme.getText())
              addedElements.add(addedElement)
            } else {
              appliedGroup.getElements().add(group.getElements()[from])
              return from + 1
            }
          } else if (phonemeType == AkrantiainTokenType.DOLLAR) {
            for (Integer i : pointer ..< to) {
              addedElements.add(group.getElements()[i])
            }
          }
          phonemeIndex ++
        } else {
          for (Integer i : pointer ..< to) {
            addedElements.add(group.getElements()[i])
          }
        }
        pointer = to
      } else {
        appliedGroup.getElements().add(group.getElements()[from])
        return from + 1
      }
    }
    if ($rightCondition != null && !$rightCondition.matchRightCondition(group, pointer, setting)) {
      appliedGroup.getElements().add(group.getElements()[from])
      return from + 1
    }
    appliedGroup.getElements().addAll(addedElements)
    return pointer
  }

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
      if (selection.isConcrete()) {
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