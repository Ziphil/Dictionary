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
  private String $description
  private SearchType $descriptionSearchType

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

  public String getDescription() {
    return $description
  }

  public void setDescription(String description) {
    $description = description
  }

  public SearchType getDescriptionSearchType() {
    return $descriptionSearchType
  }

  public void setDescriptionSearchType(SearchType descriptionSearchType) {
    $descriptionSearchType = descriptionSearchType
  }

}