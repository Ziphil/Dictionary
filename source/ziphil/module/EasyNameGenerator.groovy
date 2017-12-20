package ziphil.module

import groovy.transform.CompileStatic
import ziphilib.transform.InnerClass
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class EasyNameGenerator implements NameGenerator {

  private static final Random RANDOM = Random.new()

  private Config $config

  public void load(Config config) {
    $config = config
  }

  public String generate() {
    List<String> vowels = $config.getVowels()
    List<String> consonants = $config.getConsonants()
    List<String> syllablePatterns = $config.getSyllablePatterns()
    Int minSyllableSize = $config.getMinSyllableSize()
    Int maxSyllableSize = $config.getMaxSyllableSize()
    StringBuilder name = StringBuilder.new()
    Int syllableSize = RANDOM.nextInt(maxSyllableSize - minSyllableSize + 1) + minSyllableSize
    for (Int i = 0 ; i < syllableSize ; i ++) {
      String syllablePattern = (syllablePatterns.isEmpty()) ? "" : syllablePatterns[RANDOM.nextInt(syllablePatterns.size())]
      String syllable = syllablePattern.replaceAll(/(.)/) { List<String> match ->
        String character = match[1]
        if (character == "V" || character == "v") {
          return (vowels.isEmpty()) ? "" : vowels[RANDOM.nextInt(vowels.size())]
        } else if (character == "C" || character == "c") {
          return (consonants.isEmpty()) ? "" : consonants[RANDOM.nextInt(consonants.size())]
        } else {
          return ""
        }
      }
      name.append(syllable)
    }
    return name.toString()
  }

}


@InnerClass(EasyNameGenerator)
@CompileStatic @Ziphilify
public static class Config {

  private List<String> $vowels = ArrayList.new()
  private List<String> $consonants = ArrayList.new()
  private List<String> $syllablePatterns = ArrayList.new()
  private Int $minSyllableSize = 1
  private Int $maxSyllableSize = 3

  public List<String> getVowels() {
    return $vowels
  }

  public void setVowels(List<String> vowels) {
    $vowels = vowels
  }

  public List<String> getConsonants() {
    return $consonants
  }

  public void setConsonants(List<String> consonants) {
    $consonants = consonants
  }

  public List<String> getSyllablePatterns() {
    return $syllablePatterns
  }

  public void setSyllablePatterns(List<String> syllablePatterns) {
    $syllablePatterns = syllablePatterns
  }

  public Int getMinSyllableSize() {
    return $minSyllableSize
  }

  public void setMinSyllableSize(Int minSyllableSize) {
    $minSyllableSize = minSyllableSize
  }

  public Int getMaxSyllableSize() {
    return $maxSyllableSize
  }

  public void setMaxSyllableSize(Int maxSyllableSize) {
    $maxSyllableSize = maxSyllableSize
  }

}