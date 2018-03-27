package ziphil.dictionary.slime

import com.fasterxml.jackson.annotation.JsonGetter
import groovy.transform.CompileStatic
import ziphil.dictionary.DetailedSearchParameter
import ziphil.dictionary.Dictionary
import ziphil.dictionary.SearchType
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class SlimeSearchParameter implements DetailedSearchParameter<SlimeWord> {

  private Int $id
  private String $name
  private SearchType $nameSearchType
  private String $equivalentName
  private String $equivalentTitle
  private SearchType $equivalentSearchType
  private String $informationText
  private String $informationTitle
  private SearchType $informationSearchType
  private String $tag
  private Boolean $hasId = false
  private Boolean $hasName = false
  private Boolean $hasEquivalent = false
  private Boolean $hasInformation = false
  private Boolean $hasTag = false

  public SlimeSearchParameter(Int id) {
    $id = id
    $hasId = true
  }

  public SlimeSearchParameter(String name) {
    $name = name
    $hasName = true
  }

  public SlimeSearchParameter() {
  }

  public void preprocess(Dictionary dictionary) {
  }

  public Boolean matches(SlimeWord word) {
    Boolean predicate = true
    Int id = word.getId()
    String name = word.getName()
    List<SlimeEquivalent> equivalents = word.getRawEquivalents()
    List<SlimeInformation> informations = word.getInformations()
    List<String> tags = word.getTags()
    if ($hasId) {
      if (id != $id) {
        predicate = false
      }
    }
    if ($hasName) {
      if (!$nameSearchType.matches(name, $name)) {
        predicate = false
      }
    }
    if ($hasEquivalent) {
      Boolean equivalentPredicate = false
      for (SlimeEquivalent equivalent : equivalents) {
        String equivalentTitle = equivalent.getTitle()
        for (String equivalentName : equivalent.getNames()) {
          if ($equivalentSearchType.matches(equivalentName, $equivalentName ?: "") && ($equivalentTitle == null || equivalentTitle == $equivalentTitle)) {
            equivalentPredicate = true
          }
        }
      }
      if (!equivalentPredicate) {
        predicate = false
      }
    }
    if ($hasInformation) {
      Boolean informationPredicate = false
      for (SlimeInformation information : informations) {
        String informationText = information.getText()
        String informationTitle = information.getTitle()
        if ($informationSearchType.matches(informationText, $informationText ?: "") && ($informationTitle == null || informationTitle == $informationTitle)) {
          informationPredicate = true
        }
      }
      if (!informationPredicate) {
        predicate = false
      }
    }
    if ($hasTag) {
      Boolean tagPredicate = false
      for (String tag : tags) {
        if (tag == $tag) {
          tagPredicate = true
        }
      }
      if (!tagPredicate) {
        predicate = false
      }
    }
    return predicate
  }

  public String toString() {
    StringBuilder string = StringBuilder.new()
    if ($hasId) {
      string.append("ID[")
      string.append($id)
      string.append("], ")
    }
    if ($hasName) {
      string.append("単語[")
      string.append($name)
      string.append("], ")
    }
    if ($hasEquivalent) {
      string.append("訳語[")
      string.append($equivalentTitle ?: "")
      string.append(":")
      string.append($equivalentName ?: "")
      string.append("], ")
    }
    if ($hasInformation) {
      string.append("内容[")
      string.append($informationTitle ?: "")
      string.append(":")
      string.append($informationText ?: "")
      string.append("], ")
    }
    if ($hasTag) {
      string.append("タグ[")
      string.append($tag)
      string.append("], ")
    }
    if (string.length() >= 2) {
      string.setLength(string.length() - 2)
    }
    return string.toString()
  }

  public Int getId() {
    return $id
  }

  public void setId(Int id) {
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

  @JsonGetter("hasId")
  public Boolean hasId() {
    return $hasId
  }

  public void setHasId(Boolean hasId) {
    $hasId = hasId
  }

  @JsonGetter("hasName")
  public Boolean hasName() {
    return $hasName
  }

  public void setHasName(Boolean hasName) {
    $hasName = hasName
  }

  @JsonGetter("hasEquivalent")
  public Boolean hasEquivalent() {
    return $hasEquivalent
  }

  public void setHasEquivalent(Boolean hasEquivalent) {
    $hasEquivalent = hasEquivalent
  }

  @JsonGetter("hasInformation")
  public Boolean hasInformation() {
    return $hasInformation
  }

  public void setHasInformation(Boolean hasInformation) {
    $hasInformation = hasInformation
  }

  @JsonGetter("hasTag")
  public Boolean hasTag() {
    return $hasTag
  }

  public void setHasTag(Boolean hasTag) {
    $hasTag = hasTag
  }

}