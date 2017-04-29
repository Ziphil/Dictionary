package ziphil.dictionary.slime

import groovy.transform.CompileStatic
import java.util.regex.Matcher
import ziphil.dictionary.DictionaryConverter
import ziphil.dictionary.Word
import ziphil.dictionary.shaleia.ShaleiaDictionary
import ziphil.dictionary.shaleia.ShaleiaWord
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class SlimeShaleiaDictionaryConverter extends DictionaryConverter<SlimeDictionary, ShaleiaDictionary, SlimeWord> {

  public SlimeShaleiaDictionaryConverter(SlimeDictionary newDictionary, ShaleiaDictionary oldDictionary) {
    super(newDictionary, oldDictionary)
  }

  protected Boolean convert() {
    List<ShaleiaWord> oldWords = $oldDictionary.getRawWords()
    Integer size = oldWords.size()
    for (Integer i : 0 ..< size) {
      if (isCancelled()) {
        return false
      }
      ShaleiaWord oldWord = oldWords[i]
      if (!oldWord.getName().startsWith("\$")) {
        SlimeWord newWord = SlimeWord.new()
        newWord.setName(oldWord.getName())
        BufferedReader oldDescriptionReader = BufferedReader.new(StringReader.new(oldWord.getDescription()))
        for (String line ; (line = oldDescriptionReader.readLine()) != null ;) {
          Matcher equivalentMatcher = line =~ /^\=\s*〈(.+)〉\s*(.+)$/
          Matcher meaningMatcher = line =~ /^M>\s*(.+)$/
          Matcher etymologyMatcher = line =~ /^E>\s*(.+)$/
          Matcher usageMatcher = line =~ /^U>\s*(.+)$/
          Matcher phraseMatcher = line =~ /^P>\s*(.+)$/
          Matcher noteMatcher = line =~ /^N>\s*(.+)$/
          Matcher taskMatcher = line =~ /^O>\s*(.+)$/
          Matcher exampleMatcher = line =~ /^S>\s*(.+)$/
          if (equivalentMatcher.matches()) {
            String part = equivalentMatcher.group(1)
            String equivalent = equivalentMatcher.group(2)
            addEquivalent(newWord, part, equivalent)
          }
          if (meaningMatcher.matches()) {
            String meaning = meaningMatcher.group(1)
            addOther(newWord, "語義", meaning)
          }
          if (etymologyMatcher.matches()) {
            String etymology = etymologyMatcher.group(1)
            addOther(newWord, "語源", etymology)
          }
          if (usageMatcher.matches()) {
            String usage = usageMatcher.group(1)
            addOther(newWord, "語法", usage)
          }
          if (phraseMatcher.matches()) {
            String phrase = phraseMatcher.group(1)
            addOther(newWord, "成句", phrase)
          }
          if (noteMatcher.matches()) {
            String note = noteMatcher.group(1)
            addOther(newWord, "備考", note)
          }
          if (taskMatcher.matches()) {
            String task = taskMatcher.group(1)
            addOther(newWord, "タスク", task)
          }
          if (exampleMatcher.matches()) {
            String example = exampleMatcher.group(1)
            addOther(newWord, "例文", example)
          }
        }
        newWord.setDictionary($newDictionary)
        $newWords.add(newWord)
      }
      updateProgress(i + 1, size)
    }
    return true
  }

  private void addEquivalent(SlimeWord newWord, String part, String equivalent) {
    SlimeEquivalent newEquivalent = SlimeEquivalent.new()
    newEquivalent.setTitle(part)
    newEquivalent.setNames(equivalent.replaceAll(/(\{|\}|\/|\(.*?\)\s*)/, "").split(/\s*,\s*/).toList())
    newWord.getRawEquivalents().add(newEquivalent)
  }

  private void addOther(SlimeWord newWord, String title, String other) {
    SlimeInformation newInformation = SlimeInformation.new()
    newInformation.setTitle(title)
    newInformation.setText(other.replaceAll(/(\{|\}|\[|\]|\/)/, ""))
    newWord.getInformations().add(newInformation)
  }

}