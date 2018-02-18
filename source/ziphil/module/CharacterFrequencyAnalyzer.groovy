package ziphil.module

import groovy.transform.CompileStatic
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class CharacterFrequencyAnalyzer {

  private List<CharacterStatus> $characterStatuses = ArrayList.new()
  private String $excludedCharacters = ""
  private List<String> $multigraphs = ArrayList.new()
  private Int $totalFrequency = 0
  private Int $totalWordSize = 0

  public CharacterFrequencyAnalyzer(String input, String excludedCharacters, List<String> multigraphs) {
    $excludedCharacters = excludedCharacters
    $multigraphs = multigraphs
    addInput(input)
  }

  public CharacterFrequencyAnalyzer() {
  }

  public void addInput(String input) {
    for (String wordName : input.split(/\s+/)) {
      addWordName(wordName)
    }
  }

  public void addWordName(String wordName) {
    Set<String> countedCharacters = HashSet.new()
    StringBuilder cachedString = StringBuilder.new()
    Boolean hasCharacter = false
    for (Int i = 0 ; i <= wordName.length() ; i ++) {
      String character = (i < wordName.length()) ? wordName.charAt(i) : "\b"
      if ($excludedCharacters.indexOf(character) < 0) {
        cachedString.append(character)
        if (!$multigraphs.any{it.startsWith(cachedString.toString())}) {
          String matchedCharacter = cachedString.substring(0, 1)
          i -= cachedString.length() - 1
          for (Int j = cachedString.length() - 1 ; j > 1 ; j --) {
            String cachedSubstring = cachedString.substring(0, j)
            if ($multigraphs.contains(cachedSubstring)) {
              matchedCharacter = cachedSubstring
              i += j - 1
              break
            }
          }
          if (matchedCharacter != "\b") {
            CharacterStatus status = $characterStatuses.find{it.getCharacter() == matchedCharacter}
            if (status != null) {
              status.setFrequency(status.getFrequency() + 1)
              if (!countedCharacters.contains(matchedCharacter)) {
                status.setUsingWordSize(status.getUsingWordSize() + 1)
                countedCharacters.add(matchedCharacter)
              }
            } else {
              CharacterStatus nextStatus = CharacterStatus.new(matchedCharacter, 1, 1)
              countedCharacters.add(matchedCharacter)
              $characterStatuses.add(nextStatus)
            }
            cachedString.setLength(0)
            hasCharacter = true
            $totalFrequency ++
          }
        }
      }
    }
    if (hasCharacter) {
      $totalWordSize ++
    }
    countedCharacters.clear()
  }

  public List<CharacterStatus> characterStatuses() {
    for (CharacterStatus status : $characterStatuses) {
      status.setFrequencyPercentage(status.getFrequency() * 100D / $totalFrequency)
      status.setUsingWordSizePercentage(status.getUsingWordSize() * 100D / $totalWordSize)
    }
    $characterStatuses.sort() { CharacterStatus firstStatus, CharacterStatus secondStatus ->
      return secondStatus.getFrequency() <=> firstStatus.getFrequency()
    }
    return $characterStatuses
  }

  public void save(String path) {
    CharacterFrequencySaver saver = CharacterFrequencySaver.new(this, path)
    saver.run()
  }

  public void setExcludedCharacters(String excludedCharacters) {
    $excludedCharacters = excludedCharacters
  }

  public void setMultigraphs(List<String> multigraphs) {
    $multigraphs = multigraphs
  }

}