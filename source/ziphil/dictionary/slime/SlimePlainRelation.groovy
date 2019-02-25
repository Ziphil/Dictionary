package ziphil.dictionary.slime

import groovy.transform.CompileStatic
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class SlimePlainRelation {

  private String $title = ""
  private Object $word

  public SlimePlainRelation(String title, Object word) {
    $title = title
    $word = word
  }

  public SlimePlainRelation() {
  }

  public String getTitle() {
    return $title
  }

  public void setTitle(String title) {
    $title = title
  }

  public Object getWord() {
    return $word
  }

  public void setWord(Object word) {
    $word = word
  }

}