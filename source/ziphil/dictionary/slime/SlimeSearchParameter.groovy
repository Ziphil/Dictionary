package ziphil.dictionary.slime

import groovy.transform.CompileStatic
import ziphil.dictionary.DetailSearchParameter
import ziphil.dictionary.SearchType
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class SlimeSearchParameter extends DetailSearchParameter {

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

  public String toString() {
    StringBuilder string = StringBuilder.new()
    if ($id != null) {
      string.append("ID[")
      string.append($id)
      string.append("], ")
    }
    if ($name != null) {
      string.append("単語[")
      string.append($name)
      string.append("], ")
    }
    if ($equivalentName != null || $equivalentTitle != null) {
      string.append("訳語[")
      string.append($equivalentTitle ?: "")
      string.append(":")
      string.append($equivalentName ?: "")
      string.append("], ")
    }
    if ($informationText != null || $informationTitle != null) {
      string.append("内容[")
      string.append($informationTitle ?: "")
      string.append(":")
      string.append($informationText ?: "")
      string.append("], ")
    }
    if ($tag != null) {
      string.append("タグ[")
      string.append($tag)
      string.append("], ")
    }
    if (string.length() >= 2) {
      string.setLength(string.length() - 2)
    }
    return string.toString()
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