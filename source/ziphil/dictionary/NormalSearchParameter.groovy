package ziphil.dictionary

import groovy.transform.CompileStatic
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class NormalSearchParameter extends SearchParameter {

  private String $search
  private SearchMode $searchMode
  private Boolean $isStrict

  public NormalSearchParameter(String search, SearchMode searchMode, Boolean isStrict) {
    $search = search
    $searchMode = searchMode
    $isStrict = isStrict
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
    return $isStrict
  }

  public void setStrict(Boolean isStrict) {
    $isStrict = isStrict
  }

}