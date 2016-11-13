package ziphil.custom

import groovy.transform.CompileStatic
import javafx.util.StringConverter


@CompileStatic @Newify
public class IntegerStringConverter extends StringConverter<Integer> {

  public Integer fromString(String string) {
    StringBuilder result = StringBuilder.new()
    Boolean isSignAppended = false
    for (Integer i : 0 ..< string.length()) {
      String character = string[i]
      if (!isSignAppended && character == "-") {
        isSignAppended = true
        result.append("-")
      } else if (character >= "0" && character <= "9") {
        isSignAppended = true
        result.append(character)
      }
    }
    String resultString = result.toString()
    if (resultString == "" || resultString == "-") {
      return 0
    } else {
      return resultString.toInteger()
    }
  }

  public String toString(Integer integer) {
    return integer.toString()
  }

}