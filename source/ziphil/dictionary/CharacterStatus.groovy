package ziphil.dictionary

import groovy.transform.CompileStatic
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class CharacterStatus {

  private String $character
  private Integer $frequency

  public String getCharacter() {
    return $character
  }

  public void setCharacter(String character) {
    $character = character
  }

  public Integer getFrequency() {
    return $frequency
  }

  public void setFrequency(Integer frequency) {
    $frequency = frequency
  }

}