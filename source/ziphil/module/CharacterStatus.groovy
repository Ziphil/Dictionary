package ziphil.module

import groovy.transform.CompileStatic
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class CharacterStatus {

  private String $character
  private Int $frequency
  private Double $frequencyPercentage
  private Int $usingWordSize
  private Double $usingWordSizePercentage

  public String getCharacter() {
    return $character
  }

  public void setCharacter(String character) {
    $character = character
  }

  public Int getFrequency() {
    return $frequency
  }

  public void setFrequency(Int frequency) {
    $frequency = frequency
  }

  public Double getFrequencyPercentage() {
    return $frequencyPercentage
  }

  public void setFrequencyPercentage(Double frequencyPercentage) {
    $frequencyPercentage = frequencyPercentage
  }

  public Int getUsingWordSize() {
    return $usingWordSize
  }

  public void setUsingWordSize(Int usingWordSize) {
    $usingWordSize = usingWordSize
  }

  public Double getUsingWordSizePercentage() {
    return $usingWordSizePercentage
  }

  public void setUsingWordSizePercentage(Double usingWordSizePercentage) {
    $usingWordSizePercentage = usingWordSizePercentage
  }

}