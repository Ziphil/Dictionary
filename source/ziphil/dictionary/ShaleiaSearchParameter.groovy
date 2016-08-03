package ziphil.dictionary

import groovy.transform.CompileStatic


@CompileStatic @Newify
public class ShaleiaSearchParameter {

  private String $name
  private SearchType $nameSearchType

  public ShaleiaSearchParameter(String name, SearchType nameSearchType) {
    $name = name
    $nameSearchType = nameSearchType
  }

  public ShaleiaSearchParameter() {
  }

  public String getName() {
    return $name
  }

  public void setName(String name) {
    $name = name
  }

  public SearchType getNameSearchType() {
    return $nameSearchType
  }

  public void setNameSearchType(SearchType nameSearchType) {
    $nameSearchType = nameSearchType
  }

}