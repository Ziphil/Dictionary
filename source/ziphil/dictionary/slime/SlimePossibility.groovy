package ziphil.dictionary.slime

import groovy.transform.CompileStatic
import ziphil.dictionary.Possibility
import ziphil.dictionary.SearchParameter
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class SlimePossibility implements Possibility {

  private SlimeWord $word
  private String $title

  public SlimePossibility(SlimeWord word, String title) {
    $word = word
    $title = title
  }

  public SearchParameter createParameter() {
    return SlimeSearchParameter.new($word.getNumber())
  }

  public SlimeWord getWord() {
    return $word
  }

  public void setWord(SlimeWord word) {
    $word = word
  }

  public String getTitle() {
    return $title
  }

  public void setTitle(String title) {
    $title = title
  }

}