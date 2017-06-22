package ziphil.dictionary

import groovy.transform.CompileStatic
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class NameGenerator {

  private List<String> $vowels = ArrayList.new()
  private List<String> $consonants = ArrayList.new()
  private List<String> $syllablePatterns = ArrayList.new()
  private Int $minSyllableSize = 1
  private Int $maxSyllableSize = 3
  private Random $random = Random.new()

  public String generate() {
    StringBuilder name = StringBuilder.new()
    Int syllableSize = $random.nextInt($maxSyllableSize - $minSyllableSize + 1) + $minSyllableSize
    for (Int i = 0 ; i < syllableSize ; i ++) {
      String syllablePattern = ($syllablePatterns.isEmpty()) ? "" : $syllablePatterns[$random.nextInt($syllablePatterns.size())]
      String syllable = syllablePattern.replaceAll(/(.)/) { List<String> match ->
        String character = match[1]
        if (character == "V") {
          return ($vowels.isEmpty()) ? "" : $vowels[$random.nextInt($vowels.size())]
        } else if (character == "C") {
          return ($consonants.isEmpty()) ? "" : $consonants[$random.nextInt($consonants.size())]
        } else {
          return ""
        }
      }
      name.append(syllable)
    }
    return name.toString()
  }

  public void setVowels(List<String> vowels) {
    $vowels = vowels
  }

  public void setConsonants(List<String> consonants) {
    $consonants = consonants
  }

  public void setSyllablePatterns(List<String> syllablePatterns) {
    $syllablePatterns = syllablePatterns
  }

  public void setMinSyllableSize(Int minSyllableSize) {
    $minSyllableSize = minSyllableSize
  }

  public void setMaxSyllableSize(Int maxSyllableSize) {
    $maxSyllableSize = maxSyllableSize
  }

}