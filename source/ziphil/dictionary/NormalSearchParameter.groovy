package ziphil.dictionary

import groovy.transform.CompileStatic
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class NormalSearchParameter extends SearchParameter {

  private String $search = ""
  private SearchMode $searchMode = SearchMode.NAME
  private Boolean $strict = false
  private Boolean $reallyStrict = false

  // 与えられた引数から通常検索用のパラメータオブジェクトを作成します。
  // strict に true が指定された場合は環境設定に応じて完全一致もしくは前方一致で検索を行うことを意味し、false が指定された場合は正規表現で検索を行うことを意味します。
  // また、reallyStrict に true が指定されると、環境設定に関わらず完全一致検索を行うことを意味します。
  // reallyStrict の設定は、strict が true の場合のみ効果を発揮します。
  public NormalSearchParameter(String search, SearchMode searchMode, Boolean strict, Boolean reallyStrict) {
    $search = search
    $searchMode = searchMode
    $strict = strict
    $reallyStrict = reallyStrict
  }

  public NormalSearchParameter() {
  }

  public String getSearch() {
    return $search
  }

  public void setSearch(String search) {
    $search = search
  }

  public SearchMode getSearchMode() {
    return $searchMode
  }

  public void setSearchMode(SearchMode searchMode) {
    $searchMode = searchMode
  }

  public Boolean isStrict() {
    return $strict
  }

  public void setStrict(Boolean strict) {
    $strict = strict
  }

  public Boolean isReallyStrict() {
    return $reallyStrict
  }

  public void setReallyStrict(Boolean reallyStrict) {
    $reallyStrict = reallyStrict
  }

}