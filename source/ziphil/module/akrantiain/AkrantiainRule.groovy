package ziphil.module.akrantiain

import groovy.transform.CompileStatic
import ziphilib.transform.InnerClass
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class AkrantiainRule {

  private List<AkrantiainMatchable> $selections = ArrayList.new()
  private AkrantiainMatchable $leftCondition = null
  private AkrantiainMatchable $rightCondition = null
  private List<AkrantiainToken> $phonemes = ArrayList.new()

  public AkrantiainElementGroup apply(AkrantiainElementGroup group, AkrantiainModule module) {
    AkrantiainElementGroup appliedGroup = AkrantiainElementGroup.new()
    Int pointer = 0
    while (pointer <= group.getElements().size()) {
      ApplicationResult result = applyOnce(group, pointer, module)
      if (result != null) {
        appliedGroup.getElements().addAll(result.getAddedElements())
        if (pointer < result.getTo()) {
          pointer = result.getTo()
        } else {
          if (pointer < group.getElements().size()) {
            appliedGroup.getElements().add(group.getElements()[pointer])
          }
          pointer ++
        }
      } else {
        if (pointer < group.getElements().size()) {
          appliedGroup.getElements().add(group.getElements()[pointer])
        }
        pointer ++
      }
    }
    return appliedGroup
  }

  // ちょうど from で与えられた位置から規則を適用します。
  // 規則がマッチして適用できた場合は、変化後の要素のリストとマッチした範囲の右側のインデックス (範囲にそのインデックス自体は含まない) を返します。
  // そもそも規則にマッチせず適用できなかった場合は null を返します。
  private ApplicationResult applyOnce(AkrantiainElementGroup group, Int from, AkrantiainModule module) {
    if (checkLeftCondition(group, from, module)) {
      ApplicationResult result = applyOnceSelections(group, from, module)
      if (result != null) {
        Int to = result.getTo()
        if (checkRightCondition(group, to, module)) {
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

  private ApplicationResult applyOnceSelections(AkrantiainElementGroup group, Int from, AkrantiainModule module) {
    List<AkrantiainElement> addedElements = ArrayList.new()
    Int pointer = from
    Int phonemeIndex = 0
    for (AkrantiainMatchable selection : $selections) {
      Int to = selection.matchRight(group, pointer, module)
      if (to >= 0) {
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
            for (Int i = pointer ; i < to ; i ++) {
              addedElements.add(group.getElements()[i])
            }
          }
          phonemeIndex ++
        } else {
          for (Int i = pointer ; i < to ; i ++) {
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

  private Boolean checkLeftCondition(AkrantiainElementGroup group, Int to, AkrantiainModule module) {
    AkrantiainElementGroup leftGroup = group.devide(0, to)
    AkrantiainElementGroup rightGroup = group.devide(to, group.getElements().size())
    return $leftCondition == null || $leftCondition.matchLeft(leftGroup + rightGroup, leftGroup.getElements().size(), module) >= 0
  }

  private Boolean checkRightCondition(AkrantiainElementGroup group, Int from, AkrantiainModule module) {
    AkrantiainElementGroup leftGroup = group.devide(0, from)
    AkrantiainElementGroup rightGroup = group.devide(from, group.getElements().size())
    return $rightCondition == null || $rightCondition.matchRight(leftGroup + rightGroup, leftGroup.getElements().size(), module) >= 0
  }

  // 変換先が存在するなら true を返し、そうでなければ false を返します。
  // 現状では、右辺に「$」以外の文字列リテラルが 1 つ以上含まれているときに、変換先が存在すると見なされます。
  public Boolean isConcrete() {
    for (AkrantiainToken phoneme : $phonemes) {
      if (phoneme.getType() != AkrantiainTokenType.DOLLAR) {
        return true
      }
    }
    return false
  }

  public Boolean isSizeValid() {
    Int phonemeSize = $phonemes.size()
    Int concreteSelectionSize = 0
    for (AkrantiainMatchable selection : $selections) {
      if (selection.isConcrete()) {
        concreteSelectionSize ++
      }
    }
    return phonemeSize == concreteSelectionSize
  }

  public String toString() {
    StringBuilder string = StringBuilder.new()
    string.append($leftCondition)
    string.append(" [")
    for (Int i = 0 ; i < $selections.size() ; i ++) {
      string.append($selections[i])
      if (i < $selections.size() - 1) {
        string.append(" ")
      }
    }
    string.append("] ")
    string.append($rightCondition)
    string.append(" -> [")
    for (Int i = 0 ; i < $phonemes.size() ; i ++) {
      string.append($phonemes[i])
      if (i < $phonemes.size() - 1) {
        string.append(" ")
      }
    }
    string.append("]")
    return string.toString()
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

  public List<AkrantiainMatchable> getSelections() {
    return $selections
  }

  public void setSelections(List<AkrantiainMatchable> selections) {
    $selections = selections
  }

  public AkrantiainMatchable getLeftCondition() {
    return $leftCondition
  }

  public void setLeftCondition(AkrantiainMatchable leftCondition) {
    $leftCondition = leftCondition
  }

  public AkrantiainMatchable getRightCondition() {
    return $rightCondition
  }

  public void setRightCondition(AkrantiainMatchable rightCondition) {
    $rightCondition = rightCondition
  }

  public List<AkrantiainToken> getPhonemes() {
    return $phonemes
  }

  public void setPhonemes(List<AkrantiainToken> phonemes) {
    $phonemes = phonemes
  } 


  @InnerClass @Ziphilify
  private static class ApplicationResult {

    private List<AkrantiainElement> $addedElements
    private Int $to = -1

    public ApplicationResult(List<AkrantiainElement> addedElements, Int to) {
      $addedElements = addedElements
      $to = to
    }

    public List<AkrantiainElement> getAddedElements() {
      return $addedElements
    }

    public Int getTo() {
      return $to
    }

  }

}