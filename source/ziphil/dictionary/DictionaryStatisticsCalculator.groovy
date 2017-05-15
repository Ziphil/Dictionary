package ziphil.dictionary

import groovy.transform.CompileStatic
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class DictionaryStatisticsCalculator {

  private Dictionary $dictionary
  private Integer $wordNameLength = 0
  private Integer $contentLength = 0
  private List<CharacterStatus> $characterStatuses = ArrayList.new()

  public DictionaryStatisticsCalculator(Dictionary dictionary) {
    $dictionary = dictionary
    calculateLengths()
    calculateCharacterStatuses()
  }

  private void calculateLengths() {
    Integer wordNameLength = 0
    Integer contentLength = 0
    for (Word word : $dictionary.getRawWords()) {
      wordNameLength += word.getName().length()
      contentLength += word.getContent().length()
    }
    $wordNameLength = wordNameLength
    $contentLength = contentLength
  }

  private void calculateCharacterStatuses() {
    List<CharacterStatus> characterStatuses = ArrayList.new()
    Integer totalFrequency = 0
    for (Word word : $dictionary.getRawWords()) {
      for (String character : word.getName()) {
        CharacterStatus status = characterStatuses.find{it.getCharacter() == character}
        if (status != null) {
          status.setFrequency(status.getFrequency() + 1)
        } else {
          CharacterStatus nextStatus = CharacterStatus.new()
          nextStatus.setCharacter(character)
          nextStatus.setFrequency(1)
          characterStatuses.add(nextStatus)
        }
        totalFrequency ++
      }
    }
    characterStatuses.sort() { CharacterStatus firstStatus, CharacterStatus secondStatus ->
      return secondStatus.getFrequency() <=> firstStatus.getFrequency()
    }
    $characterStatuses = characterStatuses
  }

  public Integer wordSize() {
    return $dictionary.totalWordSize()
  }

  public Double tokipona() {
    return (Double)(wordSize() / 120)
  }

  public Double logTokipona() {
    return Math.log10(tokipona())
  }

  public Double averageWordNameLength() {
    Integer wordSize = wordSize()
    return (wordSize > 0) ? (Double)($wordNameLength / wordSize) : 0D
  }

  public Integer contentLength() {
    return $contentLength
  }

  public Double richness() {
    Integer wordSize = wordSize()
    return (wordSize > 0) ? (Double)($contentLength / wordSize) : 0D
  }

  public List<CharacterStatus> characterStatuses() {
    return $characterStatuses
  }

}


@InnerClass(DictionaryStatisticsCalculator)
public static class CharacterStatus {

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