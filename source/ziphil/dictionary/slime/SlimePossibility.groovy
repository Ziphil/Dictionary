package ziphil.dictionary.slime

import groovy.transform.CompileStatic
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class SlimePossibility {

  private SlimeWord $word
  private String $possibilityName

  public SlimePossibility(SlimeWord word, String possibilityName) {
    $word = word
    $possibilityName = possibilityName
  }

  public SlimeWord getWord() {
    return $word
  }

  public void setWord(SlimeWord word) {
    $word = word
  }

  public String getPossibilityName() {
    return $possibilityName
  }

  public void setPossibilityName(String possibilityName) {
    $possibilityName = possibilityName
  }

}