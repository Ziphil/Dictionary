package ziphil.module

import groovy.transform.CompileStatic
import ziphilib.transform.Ziphilify
import ziphilib.type.PrimDouble
import ziphilib.type.PrimInt


@CompileStatic @Ziphilify
public class CharacterFrequencyAnalyzer {

  private List<CharacterStatus> $characterStatuses = ArrayList.new()
  private String $excludedCharacters = ""
  private PrimInt $totalFrequency = 0
  private PrimInt $totalWordSize = 0

  public CharacterFrequencyAnalyzer(String source) {
    addSource(source)
  }

  public CharacterFrequencyAnalyzer() {
  }

  public void addSource(String source) {
    for (String wordName : source.split(/\s*/)) {
      addWordName(wordName)
    }
  }

  public void addWordName(String wordName) {
    Set<String> countedCharacters = HashSet.new()
    for (String character : wordName) {
      if ($excludedCharacters.indexOf(character) < 0) {
        CharacterStatus status = $characterStatuses.find{it.getCharacter() == character}
        if (status != null) {
          status.setFrequency(status.getFrequency() + 1)
          if (!countedCharacters.contains(character)) {
            status.setUsingWordSize(status.getUsingWordSize() + 1)
            countedCharacters.add(character)
          }
        } else {
          CharacterStatus nextStatus = CharacterStatus.new()
          nextStatus.setCharacter(character)
          nextStatus.setFrequency(1)
          nextStatus.setUsingWordSize(1)
          countedCharacters.add(character)
          $characterStatuses.add(nextStatus)
        }
        $totalFrequency ++
      }
    }
    $totalWordSize ++
    countedCharacters.clear()
  }

  public List<CharacterStatus> characterStatuses() {
    for (CharacterStatus status : $characterStatuses) {
      status.setFrequencyPercentage(status.getFrequency() * 100 / $totalFrequency)
      status.setUsingWordSizePercentage(status.getUsingWordSize() * 100 / $totalWordSize)
    }
    $characterStatuses.sort() { CharacterStatus firstStatus, CharacterStatus secondStatus ->
      return secondStatus.getFrequency() <=> firstStatus.getFrequency()
    }
    return $characterStatuses
  }

  public void setExcludedCharacters(String excludedCharacters) {
    $excludedCharacters = excludedCharacters
  }

}