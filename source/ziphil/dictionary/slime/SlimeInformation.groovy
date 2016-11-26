package ziphil.dictionary.slime

import groovy.transform.CompileStatic
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class SlimeInformation {

  private String $title = ""
  private String $text = ""

  public SlimeInformation(String title, String text) {
    $title = title
    $text = text
  }

  public SlimeInformation() {
  }

  public String getTitle() {
    return $title
  }

  public void setTitle(String title) {
    $title = title
  }

  public String getText() {
    return $text
  }

  public void setText(String text) {
    $text = text
  }

}