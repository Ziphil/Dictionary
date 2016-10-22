package ziphil.dictionary

import com.fasterxml.jackson.annotation.JsonIgnore
import groovy.transform.CompileStatic


@CompileStatic @Newify
public class SlimeRelation {

  private String $title = ""
  private Integer $id = -1
  private String $name = ""

  public SlimeRelation() {
  }

  public SlimeRelation(String title, Integer id, String name) {
    $title = title
    $id = id
    $name = name
  }

  public String getTitle() {
    return $title
  }

  public void setTitle(String title) {
    $title = title
  }

  @JsonIgnore
  public Integer getId() {
    return $id
  }

  public void setId(Integer id) {
    $id = id
  }

  @JsonIgnore
  public String getName() {
    return $name
  }

  public void setName(String name) {
    $name = name
  }

  public Map<String, Object> getEntry() {
    return [("id"): (Object)$id, ("form"): (Object)$name]
  }

  public void setEntry(Map<String, Object> entry) {
    $id = (Integer)entry["id"]
    $name = (String)entry["form"]
  }

}