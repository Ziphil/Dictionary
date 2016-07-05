package ziphil.dictionary

import groovy.transform.CompileStatic


@CompileStatic @Newify
public class ShaleiaSearchParameter {

  private String $name
  private SearchType $searchType

  public ShaleiaSearchParameter(String name, SearchType searchType) {
    $name = name
    $searchType = searchType
  }

  public String getName() {
    return $name
  }

  public void setName(String name) {
    $name = name
  }

  public SearchType getSearchType() {
    return $searchType
  }

  public void setSearchType(SearchType searchType) {
    $searchType = searchType
  }

}