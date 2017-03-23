package ziphil.dictionary.slime

import groovy.transform.CompileStatic
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class SlimePossibility {

  private SlimeWord $word
  private String $title

  public SlimePossibility(SlimeWord word, String title) {
    $word = word
    $title = title
  }

  public SlimeWord getWord() {
    return $word
  }

  public void setWord(SlimeWord word) {
    $word = word
  }

  public String getTitle() {
    return $title
  }

  public void setTitle(String title) {
    $title = title
  }

}