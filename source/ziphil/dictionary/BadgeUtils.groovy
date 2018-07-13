package ziphil.dictionary

import groovy.transform.CompileStatic
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class BadgeUtils {

  public static Boolean contains(Map<Badge, Set<String>> identifiers, Badge badge, String identifier) {
    if (identifier != null) {
      Set<String> relevantIdentifiers = identifiers[badge]
      if (relevantIdentifiers != null) {
        return relevantIdentifiers.contains(identifier)
      } else {
        return false
      }
    } else {
      return false
    }
  }

  public static void toggle(Map<Badge, Set<String>> identifiers, Badge badge, String identifier) {
    if (identifier != null) {
      Set<String> relevantIdentifiers = identifiers[badge]
      if (relevantIdentifiers == null) {
        relevantIdentifiers = HashSet.new()
        identifiers[badge] = relevantIdentifiers
      }
      if (relevantIdentifiers.contains(identifier)) {
        relevantIdentifiers.remove(identifier)
      } else {
        relevantIdentifiers.add(identifier)
      }
    }
  }

  public static void removeFromAllTypes(Map<Badge, Set<String>> identifiers, String identifier) {
    if (identifier != null) {
      for (Badge badge : Badge.values()) {
        Set<String> relevantIdentifiers = identifiers[badge]
        if (relevantIdentifiers != null) {
          relevantIdentifiers.remove(identifier)
        }
      }
    }
  }

}