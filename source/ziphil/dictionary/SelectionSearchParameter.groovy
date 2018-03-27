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

  public List<? extends Word> getCandidates() {
    return $candidates
  }

  public void setCandidates(List<? extends Word> candidates) {
    $candidates = candidates
  }

}