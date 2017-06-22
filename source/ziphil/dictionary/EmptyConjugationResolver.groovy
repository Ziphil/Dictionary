package ziphil.dictionary

import groovy.transform.CompileStatic
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class EmptyConjugationResolver<W extends Word, S extends Suggestion> extends ConjugationResolver<W, S> {

  public EmptyConjugationResolver(List<S> suggestions) {
    super(suggestions)
  }

  public void prepare(NormalSearchParameter parameter) {
  }

  public void check(W word) {
  }

}