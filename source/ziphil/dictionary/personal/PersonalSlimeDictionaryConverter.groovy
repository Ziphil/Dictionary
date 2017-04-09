package ziphil.dictionary.personal

import groovy.transform.CompileStatic
import java.util.Map.Entry
import ziphil.dictionary.DictionaryConverter
import ziphil.dictionary.Word
import ziphil.dictionary.slime.SlimeDictionary
import ziphil.dictionary.slime.SlimeEquivalent
import ziphil.dictionary.slime.SlimeInformation
import ziphil.dictionary.slime.SlimeRelation
import ziphil.dictionary.slime.SlimeWord
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class PersonalSlimeDictionaryConverter extends DictionaryConverter<PersonalDictionary, SlimeDictionary, PersonalWord> {

  public PersonalSlimeDictionaryConverter(PersonalDictionary newDictionary, SlimeDictionary oldDictionary) {
    super(newDictionary, oldDictionary)
  }

  protected Boolean convert() {
    List<SlimeWord> oldWords = $oldDictionary.getRawWords()
    Integer size = oldWords.size()
    for (Integer i : 0 ..< size) {
      if (isCancelled()) {
        return false
      }
      SlimeWord oldWord = oldWords[i]
      PersonalWord newWord = PersonalWord.new()
      newWord.setName(oldWord.getName())
      StringBuilder newTranslation = StringBuilder.new()
      StringBuilder newUsage = StringBuilder.new()
      for (SlimeEquivalent oldEquivalent : oldWord.getRawEquivalents()) {
        appendEquivalent(newTranslation, oldEquivalent.getTitle(), oldEquivalent.getNames())
      }
      for (SlimeInformation oldInformation : oldWord.sortedInformations()) {
        appendInformation(newUsage, oldInformation.getTitle(), oldInformation.getText())
      }
      for (Entry<String, List<SlimeRelation>> oldEntry : oldWord.groupedRelations()) {
        String oldTitle = oldEntry.getKey()
        List<String> oldNames = oldEntry.getValue().collect{relation -> relation.getName()}
        appendRelation(newUsage, oldTitle, oldNames)
      }
      modifyBreak(newTranslation)
      modifyBreak(newUsage)
      newWord.setTranslation(newTranslation.toString())
      newWord.setUsage(newUsage.toString())
      newWord.setDictionary($newDictionary)
      $newWords.add(newWord)
      updateProgress(i + 1, size)
    }
    return true
  }

  private void appendEquivalent(StringBuilder newTranslation, String oldTitle, List<String> oldNames) {
    newTranslation.append("〈")
    newTranslation.append(oldTitle)
    newTranslation.append("〉 ")
    for (Integer i : 0 ..< oldNames.size()) {
      String oldName = oldNames[i]
      newTranslation.append(oldName)
      if (i < oldNames.size() - 1) {
        newTranslation.append(", ")
      }
    }
    newTranslation.append("\n")
  }

  private void appendInformation(StringBuilder newUsage, String oldTitle, String oldText) {
    Boolean insertsBreak = !$oldDictionary.getPlainInformationTitles().contains(oldTitle)
    newUsage.append("【")
    newUsage.append(oldTitle)
    newUsage.append("】")
    newUsage.append((insertsBreak) ? "\n" : " ")
    newUsage.append(oldText)
    newUsage.append("\n")
  }

  private void appendRelation(StringBuilder newUsage, String oldTitle, List<String> oldNames) {
    newUsage.append("cf:")
    if (oldTitle != "") {
      newUsage.append("〈")
      newUsage.append(oldTitle)
      newUsage.append("〉")
    }
    newUsage.append(" ")
    for (Integer i : 0 ..< oldNames.size()) {
      String oldName = oldNames[i]
      newUsage.append(oldName)
      if (i < oldNames.size() - 1) {
        newUsage.append(", ")
      }
    }
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