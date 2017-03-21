package ziphil.dictionary.slime

import groovy.transform.CompileStatic
import ziphil.dictionary.ConjugationResolver
import ziphil.module.Setting
import ziphil.module.Strings
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class SlimeConjugationResolver extends ConjugationResolver<SlimeWord, SlimeSuggestion> {

  public SlimeConjugationResolver(List<SlimeSuggestion> suggestions) {
    super(suggestions)
  }

  public void check(SlimeWord word, String search, String convertedSearch) {
    Setting setting = Setting.getInstance()
    Boolean ignoresAccent = setting.getIgnoresAccent()
    Boolean ignoresCase = setting.getIgnoresCase()
    for (SlimeVariation variation : word.getVariations()) {
      String variationTitle = variation.getTitle()
      String variationName = variation.getName()
      String convertedVariationName = Strings.convert(variationName, ignoresAccent, ignoresCase)
      if (convertedVariationName == convertedSearch) {
        SlimePossibility possibility = SlimePossibility.new(word, variationTitle)
        $suggestions[0].getPossibilities().add(possibility)
        $suggestions[0].setDisplayed(true)
        $suggestions[0].update()
      }
    }
  }

}