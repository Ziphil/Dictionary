package ziphil.dictionary.shaleia

import groovy.transform.CompileStatic
import ziphil.dictionary.NormalSearchParameter
import ziphil.dictionary.Possibility
import ziphil.dictionary.SearchParameter
import ziphil.dictionary.SearchMode
import ziphil.dictionary.SelectionSearchParameter
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class ShaleiaPossibility implements Possibility {

  private List<ShaleiaWord> $words
  private String $name
  private String $explanation

  public ShaleiaPossibility(List<ShaleiaWord> words, String explanation) {
    $words = words
    $explanation = explanation
  }

  public ShaleiaPossibility(String name, String explanation) {
    $name = name
    $explanation = explanation
  }

  public SearchParameter createParameter() {
    if ($words != null) {
      return SelectionSearchParameter.new($words)
    } else {
      return NormalSearchParameter.new($name, SearchMode.NAME, true, true)
    }
  }

  public List<ShaleiaWord> getWords() {
    return $words
  }

  public void setWords(List<ShaleiaWord> words) {
    $words = words
  }

  public String getName() {
    return $name
  }

  public void setName(String name) {
    $name = name
  }

  public String getExplanation() {
    return $explanation
  }

  public void setExplanation(String explanation) {
    $explanation = explanation
  }

}