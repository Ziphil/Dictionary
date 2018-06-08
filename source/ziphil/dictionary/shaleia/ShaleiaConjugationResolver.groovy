package ziphil.dictionary.shaleia

import groovy.transform.CompileStatic
import ziphil.dictionary.ConjugationResolver
import ziphil.dictionary.NormalSearchParameter
import ziphil.module.Setting
import ziphil.module.Strings
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class ShaleiaConjugationResolver extends ConjugationResolver<ShaleiaWord, ShaleiaSuggestion> {

  protected Map<String, List<String>> $changes
  protected String $search
  protected String $convertedSearch
  protected NormalSearchParameter $parameter

  public ShaleiaConjugationResolver(List<ShaleiaSuggestion> suggestions, Map<String, List<String>> changes) {
    super(suggestions)
    $changes = changes
  }

  public void precheck(NormalSearchParameter parameter) {
    Boolean reallyStrict = parameter.isReallyStrict()
    Setting setting = Setting.getInstance()
    Boolean ignoresAccent = !reallyStrict && setting.getIgnoresAccent()
    Boolean ignoresCase = !reallyStrict && setting.getIgnoresCase()
    $search = parameter.getSearch()
    $convertedSearch = Strings.convert(parameter.getSearch(), ignoresAccent, ignoresCase)
    $parameter = parameter
    precheckChange()
  }

  public void check(ShaleiaWord word) {
  }

  public void postcheck() {
  }

  private void precheckChange() {
    if ($changes.containsKey($convertedSearch)) {
      for (String newName : $changes[$convertedSearch]) {
        ShaleiaPossibility possibility = ShaleiaPossibility.new(newName, "変更前")
        $suggestions[1].getPossibilities().add(possibility)
        $suggestions[1].setDisplayed(true)
        $suggestions[1].update()
      }
    }
  }

}