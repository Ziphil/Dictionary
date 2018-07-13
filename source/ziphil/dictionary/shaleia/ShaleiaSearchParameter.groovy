package ziphil.dictionary.shaleia

import groovy.transform.CompileStatic
import ziphil.dictionary.Badge
import ziphil.dictionary.BadgeUtils
import ziphil.dictionary.DetailedSearchParameter
import ziphil.dictionary.Dictionary
import ziphil.dictionary.SearchType
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class ShaleiaSearchParameter implements DetailedSearchParameter<ShaleiaWord> {

  private String $name
  private SearchType $nameSearchType
  private String $equivalent
  private SearchType $equivalentSearchType
  private String $description
  private SearchType $descriptionSearchType
  private Badge $badge
  private Boolean $hasName = false
  private Boolean $hasEquivalent = false
  private Boolean $hasDescription = false
  private Boolean $hasBadge = false
  private Dictionary $dictionary

  public ShaleiaSearchParameter(String name) {
    $name = name
    $hasName = true
  }

  public ShaleiaSearchParameter() {
  }

  public void preprocess(Dictionary dictionary) {
    $dictionary = dictionary
  }

  public Boolean matches(ShaleiaWord word) {
    Boolean predicate = true
    String name = word.getName()
    List<String> equivalents = word.getEquivalents()
    String description = word.getDescription()
    if ($hasName) {
      if (!$nameSearchType.matches(name, $name)) {
        predicate = false
      }
    }
    if ($hasEquivalent) {
      Boolean equivalentPredicate = false
      for (String equivalent : equivalents) {
        if ($equivalentSearchType.matches(equivalent, $equivalent)) {
          equivalentPredicate = true
        }
      }
      if (!equivalentPredicate) {
        predicate = false
      }
    }
    if ($hasDescription) {
      if (!$descriptionSearchType.matches(description, $description)) {
        predicate = false
      }
    }
    if ($hasBadge) {
      Map<Badge, Set<String>> identifiers = $dictionary.getIndividualSetting().getBadgedIdentifiers()
      String identifier = word.getIdentifier()
      if (!BadgeUtils.contains(identifiers, $badge, identifier)) {
        predicate = false
      }
    }
    return predicate
  }

  public String toString() {
    StringBuilder string = StringBuilder.new()
    if ($hasName) {
      string.append("単語[")
      string.append($name)
      string.append("], ")
    }
    if ($hasEquivalent) {
      string.append("訳語[")
      string.append($equivalent)
      string.append("], ")
    }
    if ($hasDescription) {
      string.append("内容[")
      string.append($description)
      string.append("], ")
    }
    if ($hasBadge) {
      string.append("マーカー[")
      string.append($badge)
      string.append("], ")
    }
    if (string.length() >= 2) {
      string.setLength(string.length() - 2)
    }
    return string.toString()
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

  public Badge getBadge() {
    return $badge
  }

  public void setBadge(Badge badge) {
    $badge = badge
  }

  public Boolean hasName() {
    return $hasName
  }

  public void setHasName(Boolean hasName) {
    $hasName = hasName
  }

  public Boolean hasEquivalent() {
    return $hasEquivalent
  }

  public void setHasEquivalent(Boolean hasEquivalent) {
    $hasEquivalent = hasEquivalent
  }

  public Boolean hasDescription() {
    return $hasDescription
  }

  public void setHasDescription(Boolean hasDescription) {
    $hasDescription = hasDescription
  }

  public Boolean hasBadge() {
    return $hasBadge
  }

  public void setHasBadge(Boolean hasBadge) {
    $hasBadge = hasBadge
  }

}