package ziphil.module

import groovy.transform.CompileStatic
import ziphilib.transform.Ziphilify
import ziphilib.type.PrimDouble
import ziphilib.type.PrimInt


@CompileStatic @Ziphilify
public class CharacterStatus {

  private String $character
  private PrimInt $frequency
  private PrimDouble $frequencyPercentage
  private PrimInt $usingWordSize
  private PrimDouble $usingWordSizePercentage

  public String getCharacter() {
    return $character
  }

  public void setCharacter(String character) {
    $character = character
  }

  public PrimInt getFrequency() {
    return $frequency
  }

  public void setFrequency(PrimInt frequency) {
    $frequency = frequency
  }

  public PrimDouble getFrequencyPercentage() {
    return $frequencyPercentage
  }

  public void setFrequencyPercentage(PrimDouble frequencyPercentage) {
    $frequencyPercentage = frequencyPercentage
  }

  public PrimInt getUsingWordSize() {
    return $usingWordSize
  }

  public void setUsingWordSize(PrimInt usingWordSize) {
    $usingWordSize = usingWordSize
  }

  public PrimDouble getUsingWordSizePercentage() {
    return $usingWordSizePercentage
  }

  public void setUsingWordSizePercentage(PrimDouble usingWordSizePercentage) {
    $usingWordSizePercentage = usingWordSizePercentage
  }

}