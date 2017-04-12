package ziphil.module.akrantiain

import groovy.transform.CompileStatic


@CompileStatic
public interface AkrantiainMatchable {

  // ちょうど from で与えられた位置から右向きにマッチするかどうかを調べます。
  // マッチした場合はマッチした範囲の右端のインデックス (範囲にそのインデックス自体は含まない) を返します。
  // マッチしなかった場合は null を返します。
  public Integer matchRight(AkrantiainElementGroup group, Integer from, AkrantiainModule module)

  // ちょうど to で与えられた位置から左向きにマッチするかどうかを調べます。
  // マッチした場合はマッチした範囲の左端のインデックス (範囲にそのインデックス自体を含む) を返します。
  // マッチしなかった場合は null を返します。
  public Integer matchLeft(AkrantiainElementGroup group, Integer to, AkrantiainModule module)

  // モジュールに存在していない識別子を含んでいればそれを返し、そうでなければ null を返します。
  public AkrantiainToken findDeadIdentifier(AkrantiainModule module)

  // 中身を全て展開したときに identifiers に含まれる識別子トークンが含まれていればそれを返し、そうでなければ null を返します。
  // 識別子の定義に循環参照がないかを調べるのに用いられます。
  public AkrantiainToken findCircularIdentifier(List<AkrantiainToken> identifiers, AkrantiainModule module)

  // 変換先をもつならば true を返し、そうでなければ false を返します。
  public Boolean isConcrete()

}