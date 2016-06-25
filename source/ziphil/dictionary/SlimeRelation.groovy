package ziphil.dictionary

import groovy.transform.CompileStatic
import net.arnx.jsonic.JSONHint


@CompileStatic @Newify
public class SlimeRelation {

  private String $title = ""
  private Integer $id = -1
  private String $name = ""

  public SlimeInformation(String title, Integer id, String name) {
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

  @JSONHint(ignore=true)
  public Integer getId() {
    return $id
  }

  @JSONHint(ignore=true)
  public void setId(Integer id) {
    $id = id
  }

  @JSONHint(ignore=true)
  public String getName() {
    return $name
  }

  @JSONHint(ignore=true)
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