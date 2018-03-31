package ziphil.dictionary.shaleia

import groovy.transform.CompileStatic
import java.util.regex.Matcher
import ziphil.dictionary.NormalSearchParameter
import ziphil.module.Setting
import ziphil.module.Strings
import ziphilib.transform.InnerClass
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class ShaleiaAlphaConjugationResolver extends ShaleiaConjugationResolver {

  private static final Map<String, String> TENSE_SUFFIXES = [("現在時制"): "a", ("過去時制"): "e", ("未来時制"): "i", ("通時時制"): "o"]
  private static final Map<String, String> ASPECT_SUFFIXES = [("開始相自動詞"): "f", ("開始相他動詞"): "v", ("経過相自動詞"): "c", ("経過相他動詞"): "q", ("完了相自動詞"): "k", ("完了相他動詞"): "g",
                                                              ("継続相自動詞"): "t", ("継続相他動詞"): "d", ("終了相自動詞"): "p", ("終了相他動詞"): "b", ("無相自動詞"): "s", ("無相他動詞"): "z"]
  private static final Map<String, String> VERB_CLASS_PREFIXES = [("形容詞"): "a", ("副詞"): "o"]
  private static final Map<String, String> ADVERB_CLASS_PREFIXES = [("副詞"): "e"]
  private static final Map<String, String> PARTICLE_PREFIXES = [("非動詞修飾"): "i"]
  private static final Map<String, String> NEGATION_PREFIXES = [("否定"): "du"]

  private List<ConjugationCandidate> $conjugationCandidates = ArrayList.new()
  private List<AbbreviationCandidate> $abbreviationCandidates = ArrayList.new()

  public ShaleiaAlphaConjugationResolver(List<ShaleiaSuggestion> suggestions, Map<String, List<String>> changes) {
    super(suggestions, changes)
  }

  public void precheck(NormalSearchParameter parameter) {
    super.precheck(parameter)
    precheckConjugation()
    precheckAbbreviation()
  }

  public void check(ShaleiaWord word) {
    super.check(word)
    checkConjugation(word)
    checkAbbreviation(word)
  }

  public void postcheck() {
    super.postcheck()
    postcheckAbbreviation()
  }

  private void precheckConjugation() {
    for (Map.Entry<String, String> tenseEntry : TENSE_SUFFIXES) {
      for (Map.Entry<String, String> aspectEntry : ASPECT_SUFFIXES) {
        String suffix = tenseEntry.getValue() + aspectEntry.getValue()
        for (Map.Entry<String, String> negationEntry : NEGATION_PREFIXES) {
          String prefix = negationEntry.getValue()
          if ($convertedSearch.endsWith(suffix) && $convertedSearch.startsWith(prefix)) {
            String explanation = tenseEntry.getKey() + aspectEntry.getKey() + negationEntry.getKey()
            String name = $convertedSearch.replaceAll(/^${prefix}|${suffix}$/, "")
            ConjugationCandidate conjugationCandidate = ConjugationCandidate.new(ConjugationType.VERB, explanation, name)
            $conjugationCandidates.add(conjugationCandidate)
          }
        }
        if ($convertedSearch.endsWith(suffix)) {
          String explanation = tenseEntry.getKey() + aspectEntry.getKey()
          String name = $convertedSearch.replaceAll(/${suffix}$/, "")
          ConjugationCandidate conjugationCandidate = ConjugationCandidate.new(ConjugationType.VERB, explanation, name)
          $conjugationCandidates.add(conjugationCandidate)
        }
      }
    }
    for (Map.Entry<String, String> verbClassEntry : VERB_CLASS_PREFIXES) {
      String prefix = verbClassEntry.getValue()
      for (Map.Entry<String, String> negationEntry : NEGATION_PREFIXES) {
        String doublePrefix = prefix + negationEntry.getValue()
        if ($convertedSearch.startsWith(doublePrefix)) {
          String explanation = verbClassEntry.getKey() + negationEntry.getKey()
          String name = $convertedSearch.replaceAll(/^${doublePrefix}/, "")
          ConjugationCandidate conjugationCandidate = ConjugationCandidate.new(ConjugationType.VERB, explanation, name)
          $conjugationCandidates.add(conjugationCandidate)
        }
      }
      if ($convertedSearch.startsWith(prefix)) {
        String explanation = verbClassEntry.getKey()
        String name = $convertedSearch.replaceAll(/^${prefix}/, "")
        ConjugationCandidate conjugationCandidate = ConjugationCandidate.new(ConjugationType.VERB, explanation, name)
        $conjugationCandidates.add(conjugationCandidate)
      }
    }
    for (Map.Entry<String, String> adverbClassEntry : ADVERB_CLASS_PREFIXES) {
      String prefix = adverbClassEntry.getValue()
      for (Map.Entry<String, String> negationEntry : NEGATION_PREFIXES) {
        String doublePrefix = prefix + negationEntry.getValue()
        if ($convertedSearch.startsWith(doublePrefix)) {
          String explanation = adverbClassEntry.getKey() + negationEntry.getKey()
          String name = $convertedSearch.replaceAll(/^${doublePrefix}/, "")
          ConjugationCandidate conjugationCandidate = ConjugationCandidate.new(ConjugationType.ADVERB, explanation, name)
          $conjugationCandidates.add(conjugationCandidate)
        }
      }
      if ($convertedSearch.startsWith(prefix)) {
        String explanation = adverbClassEntry.getKey()
        String name = $convertedSearch.replaceAll(/^${prefix}/, "")
        ConjugationCandidate conjugationCandidate = ConjugationCandidate.new(ConjugationType.ADVERB, explanation, name)
        $conjugationCandidates.add(conjugationCandidate)
      }
    }
    for (Map.Entry<String, String> prepositionEntry : PARTICLE_PREFIXES) {
      String prefix = prepositionEntry.getValue()
      if ($convertedSearch.startsWith(prefix)) {
        String explanation = prepositionEntry.getKey()
        String name = $convertedSearch.replaceAll(/^${prefix}/, "")
        ConjugationCandidate conjugationCandidate = ConjugationCandidate.new(ConjugationType.PARTICLE, explanation, name)
        $conjugationCandidates.add(conjugationCandidate)
      }
    }
    for (Map.Entry<String, String> negationEntry : NEGATION_PREFIXES) {
      String prefix = negationEntry.getValue()
      if ($convertedSearch.startsWith(prefix)) {
        String explanation = negationEntry.getKey()
        String name = $convertedSearch.replaceAll(/^${prefix}/, "")
        ConjugationCandidate conjugationCandidate = ConjugationCandidate.new(ConjugationType.NOUN, explanation, name)
        $conjugationCandidates.add(conjugationCandidate)
      }
    }
  }

  private void precheckAbbreviation() {
    Matcher matcher = $convertedSearch =~ /^(.+)'(.+)$/
    if (matcher.find()) {
      AbbreviationCandidate beforeAbbreviationCandidate = AbbreviationCandidate.new(matcher.group(1) + "'", matcher.group(2))
      AbbreviationCandidate afterAbbreviationCandidate = AbbreviationCandidate.new(matcher.group(1), "'" + matcher.group(2))
      $abbreviationCandidates.addAll(beforeAbbreviationCandidate, afterAbbreviationCandidate)
    }
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

  private void checkConjugation(ShaleiaWord word) {
    if (!$conjugationCandidates.isEmpty()) {
      Boolean reallyStrict = $parameter.isReallyStrict()
      Setting setting = Setting.getInstance()
      Boolean ignoresAccent = !reallyStrict && setting.getIgnoresAccent()
      Boolean ignoresCase = !reallyStrict && setting.getIgnoresCase()
      String name = word.getName()
      String convertedName = Strings.convert(name, ignoresAccent, ignoresCase)
      for (ConjugationCandidate conjugationCandidate : $conjugationCandidates) {
        if (convertedName == conjugationCandidate.getName() && word.getDescription() =~ conjugationCandidate.getType().getPattern()) {
          ShaleiaPossibility possibility = ShaleiaPossibility.new([word], conjugationCandidate.getExplanation())
          $suggestions[0].getPossibilities().add(possibility)
          $suggestions[0].setDisplayed(true)
          $suggestions[0].update()
        }
      }
    }
  }

  private void checkAbbreviation(ShaleiaWord word) {
    if (!$abbreviationCandidates.isEmpty()) {
      Boolean reallyStrict = $parameter.isReallyStrict()
      Setting setting = Setting.getInstance()
      Boolean ignoresAccent = !reallyStrict && setting.getIgnoresAccent()
      Boolean ignoresCase = !reallyStrict && setting.getIgnoresCase()
      String name = word.getName()
      String convertedName = Strings.convert(name, ignoresAccent, ignoresCase)
      for (AbbreviationCandidate abbreviationCandidate : $abbreviationCandidates) {
        if (abbreviationCandidate.getBeforeName() == convertedName) {
          abbreviationCandidate.getBeforeWords().add(word)
        }
        if (abbreviationCandidate.getAfterName() == convertedName) {
          abbreviationCandidate.getAfterWords().add(word)
        }
      }
    }
  }

  private void postcheckAbbreviation() {
    for (AbbreviationCandidate abbreviationCandidate : $abbreviationCandidates) {
      for (ShaleiaWord beforeWord : abbreviationCandidate.getBeforeWords()) {
        for (ShaleiaWord afterWord : abbreviationCandidate.getAfterWords()) {
          ShaleiaPossibility possibility = ShaleiaPossibility.new([beforeWord, afterWord], "合成")
          $suggestions[0].getPossibilities().add(possibility)
          $suggestions[0].setDisplayed(true)
          $suggestions[0].update()
        }
      }
    }
  }

}


@InnerClass(ShaleiaAlphaConjugationResolver)
@CompileStatic @Ziphilify
private static class ConjugationCandidate {

  private ConjugationType $type
  private String $explanation
  private String $name

  public ConjugationCandidate(ConjugationType type, String explanation, String name) {
    $type = type
    $explanation = explanation
    $name = name
  }

  public ConjugationType getType() {
    return $type
  }

  public String getExplanation() {
    return $explanation
  }

  public String getName() {
    return $name
  }

}


@InnerClass(ShaleiaAlphaConjugationResolver)
@CompileStatic @Ziphilify
private static class AbbreviationCandidate {

  private String $beforeName
  private String $afterName
  private List<ShaleiaWord> $beforeWords = ArrayList.new()
  private List<ShaleiaWord> $afterWords = ArrayList.new()

  public AbbreviationCandidate(String beforeName, String afterName) {
    $beforeName = beforeName
    $afterName = afterName
  }

  public String getBeforeName() {
    return $beforeName
  }

  public String getAfterName() {
    return $afterName
  }

  public List<ShaleiaWord> getBeforeWords() {
    return $beforeWords
  }

  public List<ShaleiaWord> getAfterWords() {
    return $afterWords
  }

}



@InnerClass(ShaleiaAlphaConjugationResolver)
@CompileStatic @Ziphilify
private static enum ConjugationType {

  VERB(/^\+.*〈.*動.*〉/),
  NOUN(/^\+.*〈.*名.*〉/),
  ADVERB(/^\+.*〈.*副.*〉/),
  PARTICLE(/^\+.*〈.*助.*〉/)

  private String $pattern

  private ConjugationType(String pattern) {
    $pattern = pattern
  }

  public String getPattern() {
    return $pattern
  }

}