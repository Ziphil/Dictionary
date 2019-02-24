package ziphil.dictionary.slime

import groovy.transform.CompileStatic
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class SlimeTemporaryRelation extends SlimeRelation {

  private Int $number = -1
  private String $name = ""

  public SlimeTemporaryRelation(String title, Int number, String name) {
    super(title, null)
    $number = number
    $name = name
  }

  public SlimeTemporaryRelation() {
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