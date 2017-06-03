package ziphil.dictionary

import groovy.transform.CompileStatic
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class SearchHistory {

  private static final Int MAX_SIZE = 20

  private List<SearchParameter> $parameters = ArrayList.new()
  private Int $pointer = 0
  private Long $interval = -1
  private Int $separativeInterval = 0

  public SearchHistory(Int separativeInterval) {
    $separativeInterval = separativeInterval
  }

  public SearchHistory() {
  }

  public void add(SearchParameter parameter, Boolean checksTime) {
    for (Int i = 0 ; i < $pointer ; i ++) {
      $parameters.removeAt(0)
    }
    $pointer = 0
    if (checksTime) {
      Long time = System.currentTimeMillis()
      if ($interval >= 0 && time - $interval < $separativeInterval) {
        $parameters.set(0, parameter)
      } else {
        $parameters.add(0, parameter)
      }
      $interval = time
    } else {
      $parameters.add(0, parameter)
      $interval = -1
    }
    if ($parameters.size() > MAX_SIZE) {
      $parameters.removeAt($parameters.size() - 1)
    }
  }

  public void add(SearchParameter parameter) {
    add(parameter, false)
  }

  public void clear() {
    $parameters.clear()
    $interval = -1
    $pointer = 0
  }

  public SearchParameter previous() {
    $interval = -1
    if (hasPrevious()) {
      $pointer ++
      return $parameters[$pointer]
    } else {
      return null
    }
  }

  public SearchParameter next() {
    $interval = -1
    if (hasNext()) {
      $pointer --
      return $parameters[$pointer]
    } else {
      return null
    }
  }

  public Boolean hasPrevious() {
    return $pointer + 1 < $parameters.size()
  }

  public Boolean hasNext() {
    return $pointer - 1 >= 0
  }

  public Int getSeparativeInterval() {
    return $separativeInterval
  }

  public void setSeparativeInterval(Int separativeInterval) {
    $separativeInterval = separativeInterval
  }

}