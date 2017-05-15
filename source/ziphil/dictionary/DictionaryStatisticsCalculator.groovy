package ziphil.dictionary

import groovy.transform.CompileStatic
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class DictionaryStatisticsCalculator {

  private Dictionary $dictionary
  private Integer $wordNameLength = 0
  private Integer $contentLength = 0

  public DictionaryStatisticsCalculator(Dictionary dictionary) {
    $dictionary = dictionary
    calculateLengths()
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

}