package ziphil.dictionary

import groovy.transform.CompileStatic
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public abstract class ConjugationResolver<W extends Word, S extends Suggestion> {

  protected List<S> $suggestions

  public ConjugationResolver(List<S> suggestions) {
    $suggestions = suggestions
  }

  public abstract void precheck(String search, String convertedSearch)

  public abstract void check(W word, String search, String convertedSearch)

}