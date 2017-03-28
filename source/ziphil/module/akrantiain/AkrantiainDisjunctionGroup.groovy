package ziphil.module.akrantiain

import groovy.transform.CompileStatic
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class AkrantiainDisjunctionGroup {

  public static final AkrantiainDisjunctionGroup EMPTY_GROUP = AkrantiainDisjunctionGroup.new()

  private Boolean $isNegated = false
  private List<AkrantiainTokenGroup> $tokenGroups = ArrayList.new()

  // 通常の条件として、ちょうど from で与えられた位置から右にマッチするかどうかを調べます。
  // マッチした場合はマッチした範囲の右端のインデックス (範囲にそのインデックス自体は含まない) を返します。
  // マッチしなかった場合は null を返します。
  public Integer matchSelection(AkrantiainElementGroup group, Integer from, AkrantiainSetting setting) {
    if (!$tokenGroups.isEmpty()) {
      for (Integer i : $tokenGroups.size() - 1 .. 0) {
        AkrantiainTokenGroup tokenGroup = $tokenGroups[i]
        Integer to = tokenGroup.matchSelection(group, from, setting)
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
  // 例えば、group が「ab」と「c」の 2 つから成る場合、「"bc"」というパターンはこれにマッチします。
  // 条件に合致していた場合は true を、合致しなかった場合は false を返します。
  public Boolean matchLeftCondition(AkrantiainElementGroup group, Integer to, AkrantiainSetting setting) {
    return true
  }

  // 右条件として、ちょうど from で与えられた位置から右にマッチするかどうかを調べます。
  // なお、マッチした範囲は与えられた AkrantiainElement オブジェクトの途中までである可能性があることに注意してください。
  // 例えば、group が「a」と「bc」の 2 つから成る場合、「"ab"」というパターンはこれにマッチします。
  // 条件に合致していた場合は true を、合致しなかった場合は false を返します。
  public Boolean matchRightCondition(AkrantiainElementGroup group, Integer from, AkrantiainSetting setting) {
    return true
  }

  // この選言グループが変換先をもつならば true を返し、そうでなければ false を返します。
  // ver 0.4.2 の時点では、これに該当するのは「^」のみです。
  public Boolean isConcrete() {
    return $tokenGroups.size() > 1 || !$tokenGroups[0].isSingleton() || $tokenGroups[0].getToken().getType() != AkrantiainTokenType.CIRCUMFLEX
  }

  public String toString() {
    StringBuilder string = StringBuilder.new()
    if ($isNegated) {
      string.append("!")
    }
    string.append("(")
    for (Integer i : 0 ..< $tokenGroups.size()) {
      string.append($tokenGroups[i])
      if (i < $tokenGroups.size() - 1) {
        string.append(", ")
      }
    }
    string.append(")")
    return string.toString()
  }

  public Boolean isSingleton() {
    return $tokenGroups.size() == 1
  }

  public Boolean isNegated() {
    return $isNegated
  }

  public void setNegated(Boolean isNegated) {
    $isNegated = isNegated
  }

  public AkrantiainTokenGroup getTokenGroup() {
    return $tokenGroups[0]
  }

  public List<AkrantiainTokenGroup> getTokenGroups() {
    return $tokenGroups
  }

  public void setTokenGroups(List<AkrantiainTokenGroup> tokenGroups) {
    $tokenGroups = tokenGroups
  }

}