package ziphil.dictionary

import groovy.transform.CompileStatic
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class SearchHistory {

  private static final Integer MAX_SIZE = 20
  private static final Long SEPARATIVE_INTERVAL = 500

  private List<? extends SearchParameter> $parameters = ArrayList.new()
  private Long $interval = null
  private Integer $pointer = 0

  public void add(SearchParameter parameter, Boolean checksTime) {
    for (Integer i : 0 ..< $pointer) {
      $parameters.removeAt(0)
    }
    $pointer = 0
    if (checksTime) {
      Long time = System.currentTimeMillis()
      if ($interval != null && time - $interval < SEPARATIVE_INTERVAL) {
        $parameters.set(0, parameter)
      } else {
        $parameters.add(0, parameter)
      }
      $interval = time
    } else {
      $parameters.add(0, parameter)
      $interval = null
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
    $interval = null
    $pointer = 0
  }

  public SearchParameter previous() {
    $interval = null
    if (hasPrevious()) {
      $pointer ++
      return $parameters[$pointer]
    } else {
      return null
    }
  }

  public SearchParameter next() {
    $interval = null
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

}