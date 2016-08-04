package ziphil.dictionary

import groovy.transform.CompileStatic


@CompileStatic @Newify
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