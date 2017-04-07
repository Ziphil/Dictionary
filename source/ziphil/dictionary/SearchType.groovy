package ziphil.dictionary

import groovy.transform.CompileStatic
import java.util.regex.Matcher
import java.util.regex.PatternSyntaxException
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public enum SearchType {

  EXACT("完全一致"), PREFIX("前方一致"), SUFFIX("後方一致"), PART("部分一致"), REGULAR_EXPRESSION("正規表現"), MINIMAL_PAIR("最小対語")

  private String $string = ""

  private SearchType(String string) {
    $string = string
  }

  public Boolean matches(String data, String search) {
    try {
      if (this == EXACT) {
        return data == search
      } else if (this == PREFIX) {
        return data.startsWith(search)
      } else if (this == SUFFIX) {
        return data.endsWith(search)
      } else if (this == PART) {
        return data.contains(search)
      } else if (this == REGULAR_EXPRESSION) {
        Matcher matcher = data =~ search
        return matcher.find()
      } else if (this == MINIMAL_PAIR) {
        Boolean predicate = false
        for (Integer i : 0 ..< search.length()) {
          String beforeSearchName = (i == 0) ? "" : search[0 .. i - 1]
          String afterSearchName = (i == search.length() - 1) ? "" : search[i + 1 .. -1]
          String searchRegex = "^" + beforeSearchName + "." + afterSearchName + "\$"
          Matcher matcher = data =~ searchRegex
          if (matcher.find()) {
            predicate = true
          }
        }
        return predicate
      }
    } catch (PatternSyntaxException exception) {
      return false
    }
  }

  public static SearchType valueOfExplanation(String explanation) {
    if (explanation == "完全一致") {
      return SearchType.EXACT
    } else if (explanation == "前方一致") {
      return SearchType.PREFIX
    } else if (explanation == "後方一致") {
      return SearchType.SUFFIX
    } else if (explanation == "部分一致") {
      return SearchType.PART
    } else if (explanation == "正規表現") {
      return SearchType.REGULAR_EXPRESSION
    } else if (explanation == "最小対語") {
      return SearchType.MINIMAL_PAIR
    } else {
      return null
    }
  }

  public String toString() {
    return $string
  }

}