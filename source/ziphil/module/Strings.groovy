package ziphil.module

import groovy.transform.CompileStatic
import java.text.Normalizer


@CompileStatic @Newify
public class Strings {

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

  public static String unaccent(String string) {
    String normalizedString = Normalizer.normalize(string, Normalizer.Form.NFD)
    String result = normalizedString.replaceAll(/\p{InCombiningDiacriticalMarks}+/, "")
    return result
  }

  public static String toLowerCase(String string) {
    String result = string.toLowerCase()
    return result
  }

}