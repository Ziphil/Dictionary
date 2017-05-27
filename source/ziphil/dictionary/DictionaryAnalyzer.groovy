package ziphil.dictionary

import groovy.transform.CompileStatic
import ziphil.module.CharacterFrequencyAnalyzer
import ziphil.module.CharacterStatus
import ziphilib.transform.Ziphilify
import ziphilib.type.PrimDouble
import ziphilib.type.PrimInt


@CompileStatic @Ziphilify
public class DictionaryAnalyzer {

  private Dictionary $dictionary
  private PrimInt $wordNameLength = 0
  private PrimInt $contentLength = 0
  private CharacterFrequencyAnalyzer $frequencyAnalyzer = CharacterFrequencyAnalyzer.new()

  public DictionaryAnalyzer(Dictionary dictionary) {
    $dictionary = dictionary
    calculateLengths()
    calculateCharacterFrequency()
  }

  private void calculateLengths() {
    PrimInt wordNameLength = 0
    PrimInt contentLength = 0
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

  public PrimInt wordSize() {
    return $dictionary.totalWordSize()
  }

  public PrimDouble tokipona() {
    return wordSize() / 120
  }

  public PrimDouble logTokipona() {
    return Math.log10(tokipona())
  }

  public PrimDouble averageWordNameLength() {
    PrimInt wordSize = wordSize()
    return (wordSize > 0) ? (PrimDouble)($wordNameLength / wordSize) : 0
  }

  public PrimInt contentLength() {
    return $contentLength
  }

  public PrimDouble richness() {
    PrimInt wordSize = wordSize()
    return (wordSize > 0) ? (PrimDouble)($contentLength / wordSize) : 0
  }

  public List<CharacterStatus> characterStatuses() {
    return $frequencyAnalyzer.characterStatuses()
  }

}