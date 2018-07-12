package ziphil.dictionary

import groovy.transform.CompileStatic
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class BadgeUtils {

  public static Boolean contains(Map<BadgeType, Set<String>> identifiers, BadgeType type, String identifier) {
    if (identifier != null) {
      Set<String> relevantIdentifiers = identifiers[type]
      if (relevantIdentifiers != null) {
        return relevantIdentifiers.contains(identifier)
      } else {
        return false
      }
    } else {
      return false
    }
  }

  public static void toggle(Map<BadgeType, Set<String>> identifiers, BadgeType type, String identifier) {
    if (identifier != null) {
      Set<String> relevantIdentifiers = identifiers[type]
      if (relevantIdentifiers == null) {
        relevantIdentifiers = HashSet.new()
        identifiers[type] = relevantIdentifiers
      }
      if (relevantIdentifiers.contains(identifier)) {
        relevantIdentifiers.remove(identifier)
      } else {
        relevantIdentifiers.add(identifier)
      }
    }
  }

  public static void removeFromAllTypes(Map<BadgeType, Set<String>> identifiers, String identifier) {
    if (identifier != null) {
      for (BadgeType type : BadgeType.values()) {
        Set<String> relevantIdentifiers = identifiers[type]
        if (relevantIdentifiers != null) {
          relevantIdentifiers.remove(identifier)
        }
      }
    }
  }

}