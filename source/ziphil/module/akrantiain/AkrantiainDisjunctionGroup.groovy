package ziphil.module.akrantiain

import groovy.transform.CompileStatic
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class AkrantiainDisjunctionGroup {

  public static final AkrantiainDisjunctionGroup EMPTY_GROUP = AkrantiainDisjunctionGroup.new()
  public static final AkrantiainDisjunctionGroup SINGLE_CIRCUMFLEX_GROUP = createSingleCircumflexGroup()

  private Boolean $isNegated = false
  private List<AkrantiainTokenGroup> $groups = ArrayList.new()

  // 通常の条件として、ちょうど from で与えられた位置から右にマッチするかどうかを調べます。
  // マッチした場合はマッチした範囲の右端のインデックス (範囲にそのインデックス自体は含まない) を返します。
  // マッチしなかった場合は null を返します。
  public Integer matchSelection(List<AkrantiainElement> elements, Integer from, AkrantiainSetting setting) {
    if (!$groups.isEmpty()) {
      for (Integer i : $groups.size() - 1 .. 0) {
        AkrantiainTokenGroup group = $groups[i]
        Integer to = group.matchSelection(elements, from, setting)
        if (to != null) {
          return to
        }
      }
      return null
    } else {
      return null
    }
  }

  // 左条件として、ちょうど to で与えられた位置から左にマッチするかどうかを調べます。
  // なお、マッチした範囲は与えられた AkrantiainElement オブジェクトの途中までである可能性があることに注意してください。
  // 例えば、elements が「ab」と「c」の 2 つから成る場合、「"bc"」というパターンはこれにマッチします。
  // 条件に合致していた場合は true を、合致しなかった場合は false を返します。
  public Boolean matchLeftCondition(List<AkrantiainElement> elements, Integer to, AkrantiainSetting setting) {
    return true
  }

  // 右条件として、ちょうど from で与えられた位置から右にマッチするかどうかを調べます。
  // なお、マッチした範囲は与えられた AkrantiainElement オブジェクトの途中までである可能性があることに注意してください。
  // 例えば、elements が「a」と「bc」の 2 つから成る場合、「"ab"」というパターンはこれにマッチします。
  // 条件に合致していた場合は true を、合致しなかった場合は false を返します。
  public Boolean matchRightCondition(List<AkrantiainElement> elements, Integer from, AkrantiainSetting setting) {
    return true
  }

  // この選言グループが変換先をもつならば true を返し、そうでなければ false を返します。
  // ver 0.4.2 の時点では、これに該当するのは「^」のみです。
  public Boolean isConcrete() {
    return $groups.size() > 1 || !$groups[0].isSingleton() || $groups[0].getToken().getType() != AkrantiainTokenType.CIRCUMFLEX
  }

  public String toString() {
    StringBuilder string = StringBuilder.new()
    if ($isNegated) {
      string.append("!")
    }
    string.append("(")
    for (Integer i : 0 ..< $groups.size()) {
      string.append($groups[i])
      if (i < $groups.size() - 1) {
        string.append(", ")
      }
    }
    string.append(")")
    return string.toString()
  }

  public static AkrantiainDisjunctionGroup createSingleCircumflexGroup() {
    AkrantiainDisjunctionGroup group = AkrantiainDisjunctionGroup.new()
    AkrantiainTokenGroup tokenGroup = AkrantiainTokenGroup.new()
    AkrantiainToken token = AkrantiainToken.new(AkrantiainTokenType.CIRCUMFLEX, "^")
    tokenGroup.getTokens().add(token)
    group.getGroups().add(tokenGroup)
    return group
  }

  public Boolean isSingleton() {
    return $groups.size() == 1
  }

  public Boolean isNegated() {
    return $isNegated
  }

  public void setNegated(Boolean isNegated) {
    $isNegated = isNegated
  }

  public AkrantiainTokenGroup getGroup() {
    return $groups[0]
  }

  public List<AkrantiainTokenGroup> getGroups() {
    return $groups
  }

  public void setGroups(List<AkrantiainTokenGroup> groups) {
    $groups = groups
  }

}