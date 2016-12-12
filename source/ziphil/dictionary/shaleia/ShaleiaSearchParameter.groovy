package ziphil.dictionary.shaleia

import groovy.transform.CompileStatic
import ziphil.dictionary.DetailSearchParameter
import ziphil.dictionary.SearchType
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class ShaleiaSearchParameter extends DetailSearchParameter {

  private String $name
  private SearchType $nameSearchType
  private String $equivalent
  private SearchType $equivalentSearchType
  private String $data
  private SearchType $dataSearchType

  public ShaleiaSearchParameter(String name, SearchType nameSearchType, String equivalent, SearchType equivalentSearchType, String data, SearchType dataSearchType) {
    $name = name
    $nameSearchType = nameSearchType
    $equivalent = equivalent
    $equivalentSearchType = equivalentSearchType
    $data = data
    $dataSearchType = dataSearchType
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

  public String getEquivalent() {
    return $equivalent
  }

  public void setEquivalent(String equivalent) {
    $equivalent = equivalent
  }

  public SearchType getEquivalentSearchType() {
    return $equivalentSearchType
  }

  public void setEquivalentSearchType(SearchType equivalentSearchType) {
    $equivalentSearchType = equivalentSearchType
  }

  public String getData() {
    return $data
  }

  public void setData(String data) {
    $data = data
  }

  public SearchType getDataSearchType() {
    return $dataSearchType
  }

  public void setDataSearchType(SearchType dataSearchType) {
    $dataSearchType = dataSearchType
  }

}