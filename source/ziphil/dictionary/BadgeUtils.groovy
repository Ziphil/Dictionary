package ziphil.dictionary

import groovy.transform.CompileStatic
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class BadgeUtils {

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

}