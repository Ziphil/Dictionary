package ziphil.dictionary

import groovy.transform.CompileStatic
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class WordEditResult<W extends Word> {

  private W $word
  private W $removedWord

  public WordEditResult(W word) {
    $word = word
  }

  public WordEditResult(W word, W removedWord) {
    $word = word
    $removedWord = removedWord
  }

  public W getWord() {
    return $word
  }

  public W getRemovedWord() {
    return $removedWord
  }

}