package ziphil.module

import groovy.transform.CompileStatic
import java.text.Normalizer
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class Strings {

  private static final String ASCII_CHARACTERS = "AaEeIiOoUuAaEeIiOoUuYyAaEeIiOoUuYyAaOoNnAaEeIiOoUuYyAaCcOoUu"
  private static final String UNICODE_CHARACTERS = "ÀàÈèÌìÒòÙùÁáÉéÍíÓóÚúÝýÂâÊêÎîÔôÛûŶŷÃãÕõÑñÄäËëÏïÖöÜüŸÿÅåÇçŐőŰű"

  public static String modifyPunctuation(String string) {
    String result = string
    result = result.replaceAll(/(、|。)/, "\$1 ")
    result = result.replaceAll(/(?<!^)(「|『|〈)/, " \$1")
    result = result.replaceAll(/(」|』|〉)/, "\$1 ")
    result = result.replaceAll(/(、|。)\s+(」|』|〉)/, "\$1\$2")
    result = result.replaceAll(/(」|』|〉)\s+(、|。|,|\.)/, "\$1\$2")
    result = result.replaceAll(/(「|『|〈|\()\s+(「|『|〈)/, "\$1\$2")
    result = result.replaceAll(/  /, " ")
    return result
  }

  public static String escapeUnicode(String string) {
    StringBuilder result = StringBuilder.new()
    for (Int i = 0 ; i < string.length() ; i ++) {
      String character = string[i]
      if (!(character ==~ /^\p{ASCII}$/)) {
        String escapedCharacter = String.format("\\u%04x", string.codePointAt(i))
        result.append(escapedCharacter)
      } else {
        result.append(character)
      }
    }
    return result.toString()
  }

  public static String unaccent(String string) {
    StringBuilder result = StringBuilder.new()
    for (Int i = 0 ; i < string.length() ; i ++) {
      Int position = UNICODE_CHARACTERS.indexOf(string.codePointAt(i))
      if (position >= 0) {
        result.append(ASCII_CHARACTERS.charAt(position))
      } else {
        result.append(string.charAt(i))
      }
    }
    return result.toString()
  }

  public static String toLowerCase(String string) {
    String result = string.toLowerCase()
    return result
  }

  public static String convert(String string, Boolean ignoresAccent, Boolean ignoresCase) {
    String result = string
    if (ignoresAccent) {
      result = Strings.unaccent(result)
    }
    if (ignoresCase) {
      result = Strings.toLowerCase(result)
    }
    return result
  }

}