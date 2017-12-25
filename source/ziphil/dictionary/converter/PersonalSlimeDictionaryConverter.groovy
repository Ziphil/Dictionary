package ziphil.dictionary.converter

import groovy.transform.CompileStatic
import ziphil.dictionary.DictionaryLoader
import ziphil.dictionary.Word
import ziphil.dictionary.personal.PersonalDictionary
import ziphil.dictionary.personal.PersonalWord
import ziphil.dictionary.slime.SlimeDictionary
import ziphil.dictionary.slime.SlimeEquivalent
import ziphil.dictionary.slime.SlimeInformation
import ziphil.dictionary.slime.SlimeRelation
import ziphil.dictionary.slime.SlimeWord
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class PersonalSlimeDictionaryConverter extends DictionaryLoader<PersonalDictionary, PersonalWord> {

  private SlimeDictionary $sourceDictionary

  public PersonalSlimeDictionaryConverter(SlimeDictionary sourceDictionary) {
    super()
    $sourceDictionary = sourceDictionary
  }

  protected BooleanClass load() {
    List<SlimeWord> sourceWords = $sourceDictionary.getRawWords()
    Int size = sourceWords.size()
    for (Int i = 0 ; i < size ; i ++) {
      if (isCancelled()) {
        return false
      }
      SlimeWord sourceWord = sourceWords[i]
      PersonalWord word = PersonalWord.new()
      word.setName(sourceWord.getName())
      StringBuilder translation = StringBuilder.new()
      StringBuilder usage = StringBuilder.new()
      for (SlimeEquivalent sourceEquivalent : sourceWord.getRawEquivalents()) {
        appendEquivalent(translation, sourceEquivalent.getTitle(), sourceEquivalent.getNames())
      }
      for (SlimeInformation sourceInformation : sourceWord.sortedInformations()) {
        appendInformation(usage, sourceInformation.getTitle(), sourceInformation.getText())
      }
      for (Map.Entry<String, List<SlimeRelation>> sourceEntry : sourceWord.groupedRelations()) {
        String sourceTitle = sourceEntry.getKey()
        List<String> sourceNames = sourceEntry.getValue().collect{it.getName()}
        appendRelation(usage, sourceTitle, sourceNames)
      }
      modifyBreak(translation)
      modifyBreak(usage)
      word.setTranslation(translation.toString())
      word.setUsage(usage.toString())
      word.setDictionary($dictionary)
      $words.add(word)
      updateProgress(i + 1, size)
    }
    return true
  }

  private void appendEquivalent(StringBuilder translation, String sourceTitle, List<String> sourceNames) {
    translation.append("〈")
    translation.append(sourceTitle)
    translation.append("〉 ")
    for (Int i = 0 ; i < sourceNames.size() ; i ++) {
      String sourceName = sourceNames[i]
      translation.append(sourceName)
      if (i < sourceNames.size() - 1) {
        translation.append(", ")
      }
    }
    translation.append("\n")
  }

  private void appendInformation(StringBuilder usage, String sourceTitle, String sourceText) {
    Boolean insertsBreak = !$sourceDictionary.getPlainInformationTitles().contains(sourceTitle)
    usage.append("【")
    usage.append(sourceTitle)
    usage.append("】")
    usage.append((insertsBreak) ? "\n" : " ")
    usage.append(sourceText)
    usage.append("\n")
  }

  private void appendRelation(StringBuilder usage, String sourceTitle, List<String> sourceNames) {
    usage.append("cf:")
    if (sourceTitle != "") {
      usage.append("〈")
      usage.append(sourceTitle)
      usage.append("〉")
    }
    usage.append(" ")
    for (Int i = 0 ; i < sourceNames.size() ; i ++) {
      String sourceName = sourceNames[i]
      usage.append(sourceName)
      if (i < sourceNames.size() - 1) {
        usage.append(", ")
      }
    }
    usage.append("\n")
  }

  private void modifyBreak(StringBuilder string) {
    if (string.length() > 0) {
      if (string.codePointAt(string.length() - 1) == '\n') {
        string.deleteCharAt(string.length() - 1)
      }
    }
  }

}