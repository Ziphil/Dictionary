package ziphil.dictionary.shaleia

import groovy.transform.CompileStatic
import ziphil.dictionary.ConjugationResolver
import ziphil.module.Setting
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class ShaleiaConjugationResolver extends ConjugationResolver<ShaleiaWord, ShaleiaSuggestion> {

  private Map<String, List<String>> $changes

  public ShaleiaConjugationResolver(List<ShaleiaSuggestion> suggestions, Map<String, List<String>> changes) {
    super(suggestions)
    $changes = changes
  }

  public void precheck(String search, String convertedSearch) {
    Setting setting = Setting.getInstance()
    Boolean ignoresAccent = setting.getIgnoresAccent()
    Boolean ignoresCase = setting.getIgnoresCase()
    if ($changes.containsKey(convertedSearch)) {
      for (String newName : $changes[convertedSearch]) {
        ShaleiaPossibility possibility = ShaleiaPossibility.new(newName, "変更前")
        $suggestions[0].getPossibilities().add(possibility)
        $suggestions[0].setDisplayed(true)
        $suggestions[0].update()
      }
    }
  }

}