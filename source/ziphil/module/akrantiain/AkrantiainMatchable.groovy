package ziphil.module.akrantiain

import groovy.transform.CompileStatic


@CompileStatic
public interface AkrantiainMatchable {

  // ちょうど from で与えられた位置から右向きにマッチするかどうかを調べます。
  // マッチした場合はマッチした範囲の右端のインデックス (範囲にそのインデックス自体は含まない) を返します。
  // マッチしなかった場合は null を返します。
  public Integer matchRight(AkrantiainElementGroup group, Integer from, AkrantiainSetting setting)

  // ちょうど to で与えられた位置から左向きにマッチするかどうかを調べます。
  // マッチした場合はマッチした範囲の左端のインデックス (範囲にそのインデックス自体を含む) を返します。
  // マッチしなかった場合は null を返します。
  public Integer matchLeft(AkrantiainElementGroup group, Integer to, AkrantiainSetting setting)

  // 変換先をもつならば true を返し、そうでなければ false を返します。
  public Boolean isConcrete()

}