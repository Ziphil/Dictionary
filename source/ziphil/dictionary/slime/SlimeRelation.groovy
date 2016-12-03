package ziphil.dictionary.slime

import groovy.transform.CompileStatic
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class SlimeRelation {

  private String $title = ""
  private Integer $id = -1
  private String $name = ""

  public SlimeRelation(String title, Integer id, String name) {
    $title = title
    $id = id
    $name = name
  }

  public SlimeRelation() {
  }

  public String getTitle() {
    return $title
  }

  public void setTitle(String title) {
    $title = title
  }

  public Integer getId() {
    return $id
  }

  public void setId(Integer id) {
    $id = id
  }

  public String getName() {
    return $name
  }

  public void setName(String name) {
    $name = name
  }

}