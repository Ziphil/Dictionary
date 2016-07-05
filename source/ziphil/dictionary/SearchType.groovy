package ziphil.dictionary

import groovy.transform.CompileStatic


@CompileStatic @Newify
public enum SearchType {

  EXACT, PREFIX, SUFFIX, PART, REGULAR_EXPRESSION, MINIMAL_PAIR

}