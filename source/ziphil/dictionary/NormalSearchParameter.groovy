package ziphil.dictionary

import groovy.transform.CompileStatic
import java.util.regex.Matcher
import java.util.regex.Pattern
import java.util.regex.PatternSyntaxException
import ziphil.module.Setting
import ziphil.module.Strings
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class NormalSearchParameter implements SearchParameter<Word> {

  private String $search = ""
  private SearchMode $searchMode = SearchMode.NAME
  private Boolean $strict = false
  private Boolean $reallyStrict = false
  private String $convertedSearch
  private Pattern $pattern

  // 与えられた引数から通常検索用のパラメータオブジェクトを作成します。
  // strict に true が指定された場合は環境設定に応じて完全一致もしくは前方一致で検索を行うことを意味し、false が指定された場合は正規表現で検索を行うことを意味します。
  // また、reallyStrict に true が指定されると、環境設定に関わらず完全一致検索を行うことを意味します。
  // reallyStrict の設定は、strict が true の場合のみ効果を発揮します。
  public NormalSearchParameter(String search, SearchMode searchMode, Boolean strict, Boolean reallyStrict) {
    $search = search
    $searchMode = searchMode
    $strict = strict
    $reallyStrict = reallyStrict
  }

  public NormalSearchParameter() {
  }

  public void prepare(Dictionary dictionary) {
    Setting setting = Setting.getInstance()
    Boolean ignoresAccent = ($reallyStrict) ? false : setting.getIgnoresAccent()
    Boolean ignoresCase = ($reallyStrict) ? false : setting.getIgnoresCase()
    $convertedSearch = Strings.convert($search, ignoresAccent, ignoresCase)
    try {
      $pattern = ($searchMode != SearchMode.CONTENT && $strict) ? null : Pattern.compile($search)
    } catch (PatternSyntaxException exception) {
      $pattern = null
    }
  }

  public Boolean matches(Word word) {
    if ($searchMode == SearchMode.NAME) {
      return matchesByName(word)
    } else if ($searchMode == SearchMode.EQUIVALENT) {
      return matchesByEquivalent(word)
    } else if ($searchMode == SearchMode.CONTENT) {
      return matchesByContent(word)
    } else {
      return false
    }
  }

  private Boolean matchesByName(Word word) {
    Setting setting = Setting.getInstance()
    Boolean ignoresAccent = ($reallyStrict) ? false : setting.getIgnoresAccent()
    Boolean ignoresCase = ($reallyStrict) ? false : setting.getIgnoresCase()
    Boolean searchesPrefix = ($reallyStrict) ? false : setting.getSearchesPrefix()
    if ($strict) {
      String name = word.getName()
      String convertedName = Strings.convert(name, ignoresAccent, ignoresCase)
      if ($convertedSearch != "") {
        if (searchesPrefix) {
          return convertedName.startsWith($convertedSearch)
        } else {
          return convertedName == $convertedSearch
        }
      } else {
        return true
      }
    } else {
      if ($pattern != null) {
        Matcher matcher = $pattern.matcher(word.getName())
        return matcher.find()
      } else {
        return false
      }
    }
  }

  private Boolean matchesByEquivalent(Word word) {
    Setting setting = Setting.getInstance()
    Boolean ignoresAccent = setting.getIgnoresAccent()
    Boolean ignoresCase = setting.getIgnoresCase()
    Boolean searchesPrefix = setting.getSearchesPrefix()
    if ($strict) {
      if ($search != "") {
        return word.getEquivalents().any() { String equivalent ->
          if (searchesPrefix) {
            return equivalent.startsWith($search)
          } else {
            return equivalent == $search
          }
        }
      } else {
        return true
      }
    } else {
      if ($pattern != null) {
        return word.getEquivalents().any() { String equivalent ->
          Matcher matcher = $pattern.matcher(equivalent)
          return matcher.find()
        }
      } else {
        return false
      }
    }
  }

  private Boolean matchesByContent(Word word) {
    if ($pattern != null) {
      Matcher matcher = $pattern.matcher(word.getContent())
      return matcher.find()
    } else {
      return false
    }
  }

  public String getSearch() {
    return $search
  }

  public void setSearch(String search) {
    $search = search
  }

  public SearchMode getSearchMode() {
    return $searchMode
  }

  public void setSearchMode(SearchMode searchMode) {
    $searchMode = searchMode
  }

  public Boolean isStrict() {
    return $strict
  }

  public void setStrict(Boolean strict) {
    $strict = strict
  }

  public Boolean isReallyStrict() {
    return $reallyStrict
  }

  public void setReallyStrict(Boolean reallyStrict) {
    $reallyStrict = reallyStrict
  }

}