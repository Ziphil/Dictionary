package ziphil.dictionary

import groovy.transform.CompileStatic
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class SelectionSearchParameter implements SearchParameter<Word> {

  private List<? extends Word> $candidates

  public SelectionSearchParameter(List<? extends Word> candidates) {
    $candidates = candidates
  }

  public void preprocess(Dictionary dictionary) {
  }

  public Boolean matches(Word word) {
    for (Word candidate : $candidates) {
      if (candidate.is(word)) {
        return true
      }
    }
    return false
  }

  public String toString() {
    StringBuilder string = StringBuilder.new()
    string.append("候補[")
    for (Int i = 0 ; i < $candidates.size() ; i ++) {
      Word candidate = $candidates[i]
      string.append(candidate.getName())
      if (i != $candidates.size() - 1) {
        string.append(" | ")
      }
    }
    string.append("]")
    return string.toString()
  }

  public List<? extends Word> getCandidates() {
    return $candidates
  }

  public void setCandidates(List<? extends Word> candidates) {
    $candidates = candidates
  }

}