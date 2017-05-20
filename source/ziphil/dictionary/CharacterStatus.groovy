package ziphil.dictionary

import groovy.transform.CompileStatic
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class CharacterStatus {

  private String $character
  private Integer $frequency
  private Double $frequencyPercent
  private Integer $usingWordSize
  private Double $usingWordSizePercent

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

  public Double getFrequencyPercent() {
    return $frequencyPercent
  }

  public void setFrequencyPercent(Double frequencyPercent) {
    $frequencyPercent = frequencyPercent
  }

  public Integer getUsingWordSize() {
    return $usingWordSize
  }

  public void setUsingWordSize(Integer usingWordSize) {
    $usingWordSize = usingWordSize
  }

  public Double getUsingWordSizePercent() {
    return $usingWordSizePercent
  }

  public void setUsingWordSizePercent(Double usingWordSizePercent) {
    $usingWordSizePercent = usingWordSizePercent
  }

}