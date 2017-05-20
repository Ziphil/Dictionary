package ziphil.dictionary

import groovy.transform.CompileStatic
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class CharacterStatus {

  private String $character
  private Integer $frequency
  private Double $frequencyPercentage
  private Integer $usingWordSize
  private Double $usingWordSizePercentage

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

  public Double getFrequencyPercentage() {
    return $frequencyPercentage
  }

  public void setFrequencyPercentage(Double frequencyPercentage) {
    $frequencyPercentage = frequencyPercentage
  }

  public Integer getUsingWordSize() {
    return $usingWordSize
  }

  public void setUsingWordSize(Integer usingWordSize) {
    $usingWordSize = usingWordSize
  }

  public Double getUsingWordSizePercentage() {
    return $usingWordSizePercentage
  }

  public void setUsingWordSizePercentage(Double usingWordSizePercentage) {
    $usingWordSizePercentage = usingWordSizePercentage
  }

}