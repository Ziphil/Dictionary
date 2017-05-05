package ziphil.dictionary.personal

import groovy.transform.CompileStatic
import java.util.regex.Matcher
import ziphil.dictionary.DictionaryConverter
import ziphil.dictionary.Word
import ziphil.dictionary.shaleia.ShaleiaDescriptionReader
import ziphil.dictionary.shaleia.ShaleiaDictionary
import ziphil.dictionary.shaleia.ShaleiaWord
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class PersonalShaleiaDictionaryConverter extends DictionaryConverter<PersonalDictionary, ShaleiaDictionary, PersonalWord> {

  public PersonalShaleiaDictionaryConverter(PersonalDictionary newDictionary, ShaleiaDictionary oldDictionary) {
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
        PersonalWord newWord = PersonalWord.new()
        newWord.setName(oldWord.getName())
        StringBuilder newTranslation = StringBuilder.new()
        StringBuilder newUsage = StringBuilder.new()
        ShaleiaDescriptionReader oldReader = ShaleiaDescriptionReader.new(oldWord.getDescription())
        try {
          while (oldReader.readLine() != null) {
            if (oldReader.findCreationDate()) {
              String creationDate = oldReader.lookupCreationDate()
              String totalPart = oldReader.lookupTotalPart()
              appendCreationDate(newTranslation, totalPart, creationDate)
            }
            if (oldReader.findEquivalent()) {
              String part = oldReader.lookupPart()
              String equivalent = oldReader.lookupEquivalent()
              appendEquivalent(newTranslation, part, equivalent)
            }
            if (oldReader.findContent()) {
              String title = oldReader.title()
              String content = oldReader.lookupContent()
              appendContent(newUsage, title, content)
            }
            if (oldReader.findSynonym()) {
              String synonym = oldReader.lookupSynonym()
              appendSynonym(newUsage, synonym)
            }
          }
          modifyBreak(newTranslation)
          modifyBreak(newUsage)
          newWord.setTranslation(newTranslation.toString())
          newWord.setUsage(newUsage.toString())
          newWord.setDictionary($newDictionary)
          $newWords.add(newWord)
        } finally {
          oldReader.close()
        }
      }
      updateProgress(i + 1, size)
    }
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

  private void appendContent(StringBuilder newUsage, String title, String content) {
    newUsage.append("【")
    newUsage.append(title)
    newUsage.append("】\n")
    newUsage.append(content.replaceAll(/(\{|\}|\[|\]|\/)/, ""))
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