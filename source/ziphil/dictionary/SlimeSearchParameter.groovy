package ziphil.dictionary

import groovy.transform.CompileStatic


@CompileStatic @Newify
public class SlimeSearchParameter {

  private Integer $id
  private String $name
  private SearchType $nameSearchType
  private String $equivalentName
  private String $equivalentTitle
  private SearchType $equivalentSearchType
  private String $informationText
  private String $informationTitle
  private SearchType $informationSearchType
  private String $tag

  public SlimeSearchParameter(Integer id, String name, SearchType nameSearchType, String equivalentName, String equivalentTitle, SearchType equivalentSearchType, String informationText,
                              String informationTitle, SearchType informationSearchType, String tag) {
    $id = id
    $name = name
    $nameSearchType = nameSearchType
    $equivalentName = equivalentName
    $equivalentTitle = equivalentTitle
    $equivalentSearchType = equivalentSearchType
    $informationText = informationText
    $informationTitle = informationTitle
    $informationSearchType = informationSearchType
    $tag = tag
  }

  public SlimeSearchParameter() {
  }

  public Integer getId() {
    return $id
  }

  public void setId(Integer id) {
    $id = id
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

  public String getEquivalentName() {
    return $equivalentName
  }

  public void setEquivalentName(String equivalentName) {
    $equivalentName = equivalentName
  }

  public String getEquivalentTitle() {
    return $equivalentTitle
  }

  public void setEquivalentTitle(String equivalentTitle) {
    $equivalentTitle = equivalentTitle
  }

  public SearchType getEquivalentSearchType() {
    return $equivalentSearchType
  }

  public void setEquivalentSearchType(SearchType equivalentSearchType) {
    $equivalentSearchType = equivalentSearchType
  }

  public String getInformationText() {
    return $informationText
  }

  public void setInformationText(String informationText) {
    $informationText = informationText
  }

  public String getInformationTitle() {
    return $informationTitle
  }

  public void setInformationTitle(String informationTitle) {
    $informationTitle = informationTitle
  }

  public SearchType getInformationSearchType() {
    return $informationSearchType
  }

  public void setInformationSearchType(SearchType informationSearchType) {
    $informationSearchType = informationSearchType
  }

  public String getTag() {
    return $tag
  }

  public void setTag(String tag) {
    $tag = tag
  }

}