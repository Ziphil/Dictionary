package ziphil.dictionary.shaleia

import groovy.transform.CompileStatic
import ziphil.dictionary.Possibility
import ziphil.dictionary.SearchParameter
import ziphil.dictionary.SearchMode
import ziphil.dictionary.SelectionSearchParameter
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class ShaleiaPossibility implements Possibility {

  private List<ShaleiaWord> $words
  private String $explanation

  public ShaleiaPossibility(List<ShaleiaWord> words, String explanation) {
    $words = words
    $explanation = explanation
  }

  public SearchParameter createParameter() {
    return SelectionSearchParameter.new($words)
  }

  public List<ShaleiaWord> getWords() {
    return $words
  }

  public void setWords(List<ShaleiaWord> words) {
    $words = words
  }

  public String getExplanation() {
    return $explanation
  }

  public void setExplanation(String explanation) {
    $explanation = explanation
  }

}