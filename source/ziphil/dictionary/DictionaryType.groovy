package ziphil.dictionary

import groovy.transform.CompileStatic


@CompileStatic @Newify
public enum DictionaryType {

  SHALEIA, PERSONAL,

  public static DictionaryType valueOfPath(String path) {
    if (path.endsWith(".xdc")) {
      return SHALEIA
    } else if (path.endsWith(".csv")) {
      return PERSONAL
    }
  }

}