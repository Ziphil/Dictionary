package ziphil.dictionary

import groovy.transform.CompileStatic
import ziphil.module.CharacterFrequencyAnalyzer
import ziphil.module.CharacterStatus
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class DictionaryAnalyzer {

  private Dictionary $dictionary
  private Int $wordNameLength = 0
  private Int $contentLength = 0
  private CharacterFrequencyAnalyzer $frequencyAnalyzer = CharacterFrequencyAnalyzer.new()

  public DictionaryAnalyzer(Dictionary dictionary) {
    $dictionary = dictionary
    calculateLengths()
    calculateCharacterFrequency()
  }

  private void calculateLengths() {
    Int wordNameLength = 0
    Int contentLength = 0
    for (Word word : $dictionary.getRawWords()) {
      wordNameLength += word.getName().length()
      contentLength += word.getContent().length()
    }
    $wordNameLength = wordNameLength
    $contentLength = contentLength
  }

  private void calculateCharacterFrequency() {
    for (Word word : $dictionary.getRawWords()) {
      $frequencyAnalyzer.addWordName(word.getName())
    }
  }

  public Int wordSize() {
    return $dictionary.totalWordSize()
  }

  public Double tokipona() {
    return wordSize() / 120
  }

  public Double logTokipona() {
    return Math.log10(tokipona())
  }

  public Double averageWordNameLength() {
    Int wordSize = wordSize()
    return (wordSize > 0) ? (Double)($wordNameLength / wordSize) : 0
  }

  public Int contentLength() {
    return $contentLength
  }

  public Double richness() {
    Int wordSize = wordSize()
    return (wordSize > 0) ? (Double)($contentLength / wordSize) : 0
  }

  public List<CharacterStatus> characterStatuses() {
    return $frequencyAnalyzer.characterStatuses()
  }

}