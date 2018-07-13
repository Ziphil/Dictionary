package ziphil.dictionary

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import groovy.transform.CompileStatic
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class BadgePreference {

  private Map<String, Set<Badge>> $badges = HashMap.new()

  @JsonCreator
  public BadgePreference(Map<String, Set<Badge>> badges) {
    $badges = badges
  }

  public BadgePreference() {
  }

  public void toggle(Word word, Badge badge) {
    String identifier = word.getIdentifier()
    if (identifier != null) {
      Set<Badge> relevantBadges = $badges[identifier]
      if (relevantBadges == null) {
        relevantBadges = EnumSet.noneOf(Badge)
        $badges[identifier] = relevantBadges
      }
      if (relevantBadges.contains(badge)) {
        relevantBadges.remove(badge)
      } else {
        relevantBadges.add(badge)
      }
    }
  }

  public void removeAllBadges(Word word) {
    String identifier = word.getIdentifier()
    if (identifier != null) {
      $badges.remove(identifier)
    }
  }

  public Boolean contains(Word word, Badge badge) {
    String identifier = word.getIdentifier()
    if (identifier != null) {
      Set<Badge> relevantBadges = $badges[identifier]
      if (relevantBadges != null) {
        return relevantBadges.contains(badge)
      } else {
        return false
      }
    } else {
      return false
    }
  }

  @JsonValue
  public Map<String, Set<Badge>> toMap() {
    return $badges
  }

}