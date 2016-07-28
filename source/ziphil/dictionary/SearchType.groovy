package ziphil.dictionary

import groovy.transform.CompileStatic
import java.util.regex.Matcher
import java.util.regex.PatternSyntaxException


@CompileStatic @Newify
public enum SearchType {

  EXACT, PREFIX, SUFFIX, PART, REGULAR_EXPRESSION, MINIMAL_PAIR

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

  public static Boolean matches(SearchType type, String data, String search) {
    try {
      if (type == EXACT) {
        return data == search
      } else if (type == PREFIX) {
        return data.startsWith(search)
      } else if (type == SUFFIX) {
        return data.endsWith(search)
      } else if (type == PART) {
        return data.contains(search)
      } else if (type == REGULAR_EXPRESSION) {
        Matcher matcher = data =~ search
        return matcher.find()
      } else if (type == MINIMAL_PAIR) {
        Boolean predicate = false
        (0 ..< search.length()).each() { Integer i ->
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

}