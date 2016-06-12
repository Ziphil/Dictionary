package ziphil.module

import groovy.transform.CompileStatic


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

}