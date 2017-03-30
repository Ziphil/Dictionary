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
      ApplicationResult result = applyOnce(group, pointer, setting)
      if (result != null) {
        appliedGroup.getElements().addAll(result.getAddedElements())
        pointer = result.getTo()
      } else {
        appliedGroup.getElements().add(group.getElements()[pointer])
        pointer ++
      }
    }
    return appliedGroup
  }

  // ちょうど from で与えられた位置から規則を適用します。
  // 規則がマッチして適用できた場合は、変化後の要素のリストとマッチした範囲の右側のインデックス (範囲にそのインデックス自体は含まない) を返します。
  // そもそも規則にマッチせず適用できなかった場合は null を返します。
  private ApplicationResult applyOnce(AkrantiainElementGroup group, Integer from, AkrantiainSetting setting) {
    if (checkLeftCondition(group, from, setting)) {
      ApplicationResult result = applyOnceSelections(group, from, setting)
      if (result != null) {
        Integer to = result.getTo()
        if (checkRightCondition(group, to, setting)) {
          return result
        } else {
          return null
        }
      } else {
        return null
      }
    } else {
      return null
    }
  }

  private ApplicationResult applyOnceSelections(AkrantiainElementGroup group, Integer from, AkrantiainSetting setting) {
    List<AkrantiainElement> addedElements = ArrayList.new()
    Integer pointer = from
    Integer phonemeIndex = 0
    for (AkrantiainDisjunctionGroup selection : $selections) {
      Integer to = selection.matchRight(group, pointer, setting)
      if (to != null) {
        if (selection.isConcrete()) {
          AkrantiainToken phoneme = $phonemes[phonemeIndex]
          AkrantiainTokenType phonemeType = phoneme.getType()
          if (phonemeType == AkrantiainTokenType.SLASH_LITERAL) {
            if (group.isNoneConverted(pointer, to)) {
              AkrantiainElement addedElement = group.merge(pointer, to)
              addedElement.setResult(phoneme.getText())
              addedElements.add(addedElement)
            } else {
              return null
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
        return null
      }
    }
    return ApplicationResult.new(addedElements, pointer)
  }

  private Boolean checkLeftCondition(AkrantiainElementGroup group, Integer to, AkrantiainSetting setting) {
    AkrantiainElementGroup devidedGroup = group.devide(0, to)
    return $leftCondition == null || $leftCondition.matchLeft(devidedGroup, devidedGroup.getElements().size(), setting) != null
  }

  private Boolean checkRightCondition(AkrantiainElementGroup group, Integer from, AkrantiainSetting setting) {
    AkrantiainElementGroup devidedGroup = group.devide(from, group.getElements().size())
    return $rightCondition == null || $rightCondition.matchRight(devidedGroup, 0, setting) != null
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


@InnerClass(AkrantiainRule)
private static class ApplicationResult {

  private List<AkrantiainElement> $addedElements
  private Integer $to

  public ApplicationResult(List<AkrantiainElement> addedElements, Integer to) {
    $addedElements = addedElements
    $to = to
  }

  public List<AkrantiainElement> getAddedElements() {
    return $addedElements
  }

  public Integer getTo() {
    return $to
  }

}