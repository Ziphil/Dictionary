package ziphil.module

import groovy.transform.CompileStatic
import ziphil.dictionary.DictionaryType


@CompileStatic @Newify
public class DictionarySetting {

  private String $name
  private DictionaryType $type
  private String $typeName
  private String $path

  public DictionarySetting(String name, String typeName, String path) {
    $name = name
    $type = DictionaryType.valueOf(typeName)
    $typeName = typeName
    $path = path
  }

  public String getName() {
    return $name
  }

  public void setName(String name) {
    $name = name
  }

  public String getTypeName() {
    return $typeName
  }

  public String setTypeName(String typeName) {
    $type = DictionaryType.valueOf(typeName)
    $typeName = typeName
  }

  public DictionaryType getType() {
    return $type
  }

  public DictionaryType setType(DictionaryType type) {
    $type = type
    $typeName = type.name()
  }

  public String getPath() {
    return $path
  }

  public void setPath(String path) {
    $path = path
  }

}