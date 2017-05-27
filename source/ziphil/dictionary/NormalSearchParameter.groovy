package ziphil.dictionary

import groovy.transform.CompileStatic
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class NormalSearchParameter extends SearchParameter {

  private String $search
  private SearchMode $searchMode
  private Boolean $strict

  public NormalSearchParameter(String search, SearchMode searchMode, Boolean strict) {
    $search = search
    $searchMode = searchMode
    $strict = strict
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

}