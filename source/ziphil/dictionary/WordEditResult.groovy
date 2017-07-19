package ziphil.dictionary

import groovy.transform.CompileStatic
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class WordEditResult {

  private Word $word
  private Word $removedWord

  public WordEditResult(Word word) {
    $word = word
  }

  public WordEditResult(Word word, Word removedWord) {
    $word = word
    $removedWord = removedWord
  }

  public Word getWord() {
    return $word
  }

  public Word getRemovedWord() {
    return $removedWord
  }

}