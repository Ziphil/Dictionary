package ziphil.dictionary.shaleia

import groovy.transform.CompileStatic
import ziphil.dictionary.ConjugationResolver
import ziphil.dictionary.NormalSearchParameter
import ziphil.module.Setting
import ziphil.module.Strings
import ziphilib.transform.InnerClass
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class ShaleiaConjugationResolver extends ConjugationResolver<ShaleiaWord, ShaleiaSuggestion> {

  private static final Map<String, String> TENSE_SUFFIXES = [("現在時制"): "a", ("過去時制"): "e", ("未来時制"): "i", ("通時時制"): "o"]
  private static final Map<String, String> ASPECT_SUFFIXES = [("開始相自動詞"): "f", ("開始相他動詞"): "v", ("経過相自動詞"): "c", ("経過相他動詞"): "q", ("完了相自動詞"): "k", ("完了相他動詞"): "g",
                                                              ("継続相自動詞"): "t", ("継続相他動詞"): "d", ("終了相自動詞"): "p", ("終了相他動詞"): "b", ("無相自動詞"): "s", ("無相他動詞"): "z"]
  private static final Map<String, String> VERB_CLASS_PREFIXES = [("形容詞"): "a", ("副詞"): "o"]
  private static final Map<String, String> ADVERB_CLASS_PREFIXES = [("副詞"): "e"]
  private static final Map<String, String> PARTICLE_PREFIXES = [("非動詞修飾"): "i"]
  private static final Map<String, String> NEGATION_PREFIXES = [("否定"): "du"]

  private Map<String, List<String>> $changes
  private String $version
  private List<ConjugationCandidate> $candidates = ArrayList.new()
  private String $search
  private String $convertedSearch
  private NormalSearchParameter $parameter

  public ShaleiaConjugationResolver(List<ShaleiaSuggestion> suggestions, Map<String, List<String>> changes, String version) {
    super(suggestions)
    $changes = changes
    $version = version
  }

  public void precheck(NormalSearchParameter parameter) {
    Boolean reallyStrict = parameter.isReallyStrict()
    Setting setting = Setting.getInstance()
    Boolean ignoresAccent = (reallyStrict) ? false : setting.getIgnoresAccent()
    Boolean ignoresCase = (reallyStrict) ? false : setting.getIgnoresCase()
    $search = parameter.getSearch()
    $convertedSearch = Strings.convert(parameter.getSearch(), ignoresAccent, ignoresCase)
    $parameter = parameter
    precheckConjugation()
    precheckChange()
  }

  public void check(ShaleiaWord word) {
    checkConjugation(word)
  }

  private void precheckConjugation() {
    if ($version == "5.5") {
      for (Map.Entry<String, String> tenseEntry : TENSE_SUFFIXES) {
        for (Map.Entry<String, String> aspectEntry : ASPECT_SUFFIXES) {
          String suffix = tenseEntry.getValue() + aspectEntry.getValue()
          for (Map.Entry<String, String> negationEntry : NEGATION_PREFIXES) {
            String prefix = negationEntry.getValue()
            if ($convertedSearch.endsWith(suffix) && $convertedSearch.startsWith(prefix)) {
              String explanation = tenseEntry.getKey() + aspectEntry.getKey() + negationEntry.getKey()
              String name = $convertedSearch.replaceAll(/^${prefix}|${suffix}$/, "")
              ConjugationCandidate candidate = ConjugationCandidate.new(ConjugationType.VERB, explanation, name)
              $candidates.add(candidate)
            }
          }
          if ($convertedSearch.endsWith(suffix)) {
            String explanation = tenseEntry.getKey() + aspectEntry.getKey()
            String name = $convertedSearch.replaceAll(/${suffix}$/, "")
            ConjugationCandidate candidate = ConjugationCandidate.new(ConjugationType.VERB, explanation, name)
            $candidates.add(candidate)
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
            ConjugationCandidate candidate = ConjugationCandidate.new(ConjugationType.VERB, explanation, name)
            $candidates.add(candidate)
          }
        }
        if ($convertedSearch.startsWith(prefix)) {
          String explanation = verbClassEntry.getKey()
          String name = $convertedSearch.replaceAll(/^${prefix}/, "")
          ConjugationCandidate candidate = ConjugationCandidate.new(ConjugationType.VERB, explanation, name)
          $candidates.add(candidate)
        }
      }
      for (Map.Entry<String, String> adverbClassEntry : ADVERB_CLASS_PREFIXES) {
        String prefix = adverbClassEntry.getValue()
        for (Map.Entry<String, String> negationEntry : NEGATION_PREFIXES) {
          String doublePrefix = prefix + negationEntry.getValue()
          if ($convertedSearch.startsWith(doublePrefix)) {
            String explanation = adverbClassEntry.getKey() + negationEntry.getKey()
            String name = $convertedSearch.replaceAll(/^${doublePrefix}/, "")
            ConjugationCandidate candidate = ConjugationCandidate.new(ConjugationType.ADVERB, explanation, name)
            $candidates.add(candidate)
          }
        }
        if ($convertedSearch.startsWith(prefix)) {
          String explanation = adverbClassEntry.getKey()
          String name = $convertedSearch.replaceAll(/^${prefix}/, "")
          ConjugationCandidate candidate = ConjugationCandidate.new(ConjugationType.ADVERB, explanation, name)
          $candidates.add(candidate)
        }
      }
      for (Map.Entry<String, String> prepositionEntry : PARTICLE_PREFIXES) {
        String prefix = prepositionEntry.getValue()
        if ($convertedSearch.startsWith(prefix)) {
          String explanation = prepositionEntry.getKey()
          String name = $convertedSearch.replaceAll(/^${prefix}/, "")
          ConjugationCandidate candidate = ConjugationCandidate.new(ConjugationType.PARTICLE, explanation, name)
          $candidates.add(candidate)
        }
      }
      for (Map.Entry<String, String> negationEntry : NEGATION_PREFIXES) {
        String prefix = negationEntry.getValue()
        if ($convertedSearch.startsWith(prefix)) {
          String explanation = negationEntry.getKey()
          String name = $convertedSearch.replaceAll(/^${prefix}/, "")
          ConjugationCandidate candidate = ConjugationCandidate.new(ConjugationType.NOUN, explanation, name)
          $candidates.add(candidate)
        }
      }
    }
  }

  private void precheckChange() {
    if ($changes.containsKey($convertedSearch)) {
      for (String newName : $changes[$convertedSearch]) {
        ConjugationCandidate candidate = ConjugationCandidate.new(ConjugationType.CHANGE, "変更前", newName)
        $candidates.add(candidate)
      }
    }
  }

  private void checkConjugation(ShaleiaWord word) {
    Boolean reallyStrict = $parameter.isReallyStrict()
    Setting setting = Setting.getInstance()
    Boolean ignoresAccent = (reallyStrict) ? false : setting.getIgnoresAccent()
    Boolean ignoresCase = (reallyStrict) ? false : setting.getIgnoresCase()
    if (!$candidates.isEmpty()) {
      String name = word.getName()
      String convertedName = Strings.convert(name, ignoresAccent, ignoresCase)
      for (ConjugationCandidate candidate : $candidates) {
        ConjugationType type = candidate.getType()
        if (type == ConjugationType.VERB) {
          if (convertedName == candidate.getName() && word.getDescription() =~ /^\+.*〈.*動.*〉/) {
            ShaleiaPossibility possibility = ShaleiaPossibility.new([word], candidate.getExplanation())
            $suggestions[0].getPossibilities().add(possibility)
            $suggestions[0].setDisplayed(true)
            $suggestions[0].update()
          }
        } else if (type == ConjugationType.NOUN) {
          if (convertedName == candidate.getName() && word.getDescription() =~ /^\+.*〈.*名.*〉/) {
            ShaleiaPossibility possibility = ShaleiaPossibility.new([word], candidate.getExplanation())
            $suggestions[0].getPossibilities().add(possibility)
            $suggestions[0].setDisplayed(true)
            $suggestions[0].update()
          }
        } else if (type == ConjugationType.ADVERB) {
          if (convertedName == candidate.getName() && word.getDescription() =~ /^\+.*〈.*副.*〉/) {
            ShaleiaPossibility possibility = ShaleiaPossibility.new([word], candidate.getExplanation())
            $suggestions[0].getPossibilities().add(possibility)
            $suggestions[0].setDisplayed(true)
            $suggestions[0].update()
          }
        } else if (type == ConjugationType.PARTICLE) {
          if (convertedName == candidate.getName() && word.getDescription() =~ /^\+.*〈.*助.*〉/) {
            ShaleiaPossibility possibility = ShaleiaPossibility.new([word], candidate.getExplanation())
            $suggestions[0].getPossibilities().add(possibility)
            $suggestions[0].setDisplayed(true)
            $suggestions[0].update()
          }
        } else if (type == ConjugationType.CHANGE) {
          if (name == candidate.getName()) {
            ShaleiaPossibility possibility = ShaleiaPossibility.new([word], candidate.getExplanation())
            $suggestions[1].getPossibilities().add(possibility)
            $suggestions[1].setDisplayed(true)
            $suggestions[1].update()
          }
        }
      }
    }
  }

}


@InnerClass(ShaleiaConjugationResolver)
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


@InnerClass(ShaleiaConjugationResolver)
@CompileStatic @Ziphilify
private static enum ConjugationType {

  VERB,
  NOUN,
  ADVERB,
  PARTICLE,
  CHANGE

}