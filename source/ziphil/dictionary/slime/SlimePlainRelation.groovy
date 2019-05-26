package ziphil.dictionary.slime

import groovy.transform.CompileStatic
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class SlimePlainRelation {

  private String $title = ""
  private Int $number = -1
  private String $name = ""

  public SlimePlainRelation(String title, Int number, String name) {
    $title = title
    $number = number
    $name = name
  }

  public SlimePlainRelation() {
  }

  public String getTitle() {
    return $title
  }

  public void setTitle(String title) {
    $title = title
  }

  public Int getNumber() {
    return $number
  }

  public void setNumber(Int number) {
    $number = number
  }

  public String getName() {
    return $name
  }

  public void setName(String name) {
    $name = name
  }

}