package ziphil.dictionary.personal

import groovy.transform.CompileStatic
import java.util.regex.Matcher
import ziphil.dictionary.DictionaryConverter
import ziphil.dictionary.Word
import ziphil.dictionary.shaleia.ShaleiaDictionary
import ziphil.dictionary.shaleia.ShaleiaWord
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class PersonalShaleiaDictionaryConverter extends DictionaryConverter<PersonalDictionary, ShaleiaDictionary, PersonalWord> {

  public PersonalShaleiaDictionaryConverter(PersonalDictionary newDictionary, ShaleiaDictionary oldDictionary) {
    super(newDictionary, oldDictionary)
    updateProgress(0, 1)
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
        PersonalWord newWord = PersonalWord.new()
        newWord.setName(oldWord.getName())
        StringBuilder newTranslation = StringBuilder.new()
        StringBuilder newUsage = StringBuilder.new()
        BufferedReader oldDescriptionReader = BufferedReader.new(StringReader.new(oldWord.getDescription()))
        for (String line ; (line = oldDescriptionReader.readLine()) != null ;) {
          Matcher creationDateMatcher = line =~ /^\+\s*(\d+)(?:\s*〈(.+)〉)?\s*$/
          Matcher equivalentMatcher = line =~ /^\=\s*〈(.+)〉\s*(.+)$/
          Matcher meaningMatcher = line =~ /^M>\s*(.+)$/
          Matcher etymologyMatcher = line =~ /^E>\s*(.+)$/
          Matcher usageMatcher = line =~ /^U>\s*(.+)$/
          Matcher phraseMatcher = line =~ /^P>\s*(.+)$/
          Matcher noteMatcher = line =~ /^N>\s*(.+)$/
          Matcher taskMatcher = line =~ /^O>\s*(.+)$/
          Matcher exampleMatcher = line =~ /^S>\s*(.+)$/
          Matcher synonymMatcher = line =~ /^\-\s*(.+)$/
          if (creationDateMatcher.matches()) {
            String creationDate = creationDateMatcher.group(1)
            String totalPart = creationDateMatcher.group(2)
            appendCreationDate(newTranslation, totalPart, creationDate)
          }
          if (equivalentMatcher.matches()) {
            String part = equivalentMatcher.group(1)
            String equivalent = equivalentMatcher.group(2)
            appendEquivalent(newTranslation, part, equivalent)
          }
          if (meaningMatcher.matches()) {
            String meaning = meaningMatcher.group(1)
            appendOther(newUsage, "語義", meaning)
          }
          if (etymologyMatcher.matches()) {
            String etymology = etymologyMatcher.group(1)
            appendOther(newUsage, "語源", etymology)
          }
          if (usageMatcher.matches()) {
            String usage = usageMatcher.group(1)
            appendOther(newUsage, "語法", usage)
          }
          if (phraseMatcher.matches()) {
            String phrase = phraseMatcher.group(1)
            appendOther(newUsage, "成句", phrase)
          }
          if (noteMatcher.matches()) {
            String note = noteMatcher.group(1)
            appendOther(newUsage, "備考", note)
          }
          if (taskMatcher.matches()) {
            String task = taskMatcher.group(1)
            appendOther(newUsage, "タスク", task)
          }
          if (exampleMatcher.matches()) {
            String example = exampleMatcher.group(1)
            appendOther(newUsage, "例文", example)
          }
          if (synonymMatcher.matches()) {
            String synonym = synonymMatcher.group(1)
            appendSynonym(newUsage, synonym)
          }
        }
        modifyBreak(newTranslation)
        modifyBreak(newUsage)
        newWord.setTranslation(newTranslation.toString())
        newWord.setUsage(newUsage.toString())
        newWord.setDictionary($newDictionary)
        $newWords.add(newWord)
      }
      updateProgress(i + 1, size)
    }
    updateProgress(1, 1)
    return true
  }

  private void appendCreationDate(StringBuilder newTranslation, String totalPart, String creationDate) {
    newTranslation.append("《")
    newTranslation.append(totalPart)
    newTranslation.append("》 ")
    newTranslation.append(creationDate)
    newTranslation.append("\n")
  }

  private void appendEquivalent(StringBuilder newTranslation, String part, String equivalent) {
    newTranslation.append("〈")
    newTranslation.append(part)
    newTranslation.append("〉 ")
    newTranslation.append(equivalent.replaceAll(/(\{|\}|\/)/, ""))
    newTranslation.append("\n")
  }

  private void appendOther(StringBuilder newUsage, String title, String other) {
    newUsage.append("【")
    newUsage.append(title)
    newUsage.append("】\n")
    newUsage.append(other.replaceAll(/(\{|\}|\[|\]|\/)/, ""))
    newUsage.append("\n")
  }

  private void appendSynonym(StringBuilder newUsage, String synonym) {
    newUsage.append("cf: ")
    newUsage.append(synonym.replaceAll(/(\{|\})/, ""))
    newUsage.append("\n")
  }

  private void modifyBreak(StringBuilder string) {
    if (string.length() > 0) {
      if (string.codePointAt(string.length() - 1) == '\n') {
        string.deleteCharAt(string.length() - 1)
      }
    }
  }

}