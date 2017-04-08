package ziphil.dictionary.slime

import groovy.transform.CompileStatic
import ziphil.dictionary.DictionaryConverter
import ziphil.dictionary.Word
import ziphil.dictionary.personal.PersonalDictionary
import ziphil.dictionary.personal.PersonalWord
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class SlimePersonalDictionaryConverter extends DictionaryConverter<SlimeDictionary, PersonalDictionary, SlimeWord> {

  public SlimePersonalDictionaryConverter(SlimeDictionary newDictionary, PersonalDictionary oldDictionary) {
    super(newDictionary, oldDictionary)
  }

  protected Boolean convert() {
    List<PersonalWord> oldWords = $oldDictionary.getRawWords()
    Integer size = oldWords.size()
    for (Integer i : 0 ..< size) {
      if (isCancelled()) {
        return false
      }
      PersonalWord oldWord = oldWords[i]
      SlimeWord newWord = SlimeWord.new()
      newWord.setId(i)
      newWord.setName(oldWord.getName())
      String oldTranslation = oldWord.getTranslation()
      if (oldTranslation != "") {
        SlimeInformation newInformation = SlimeInformation.new()
        newInformation.setTitle("訳語")
        newInformation.setText(oldTranslation)
        newWord.getInformations().add(newInformation)
      }
      String oldUsage = oldWord.getUsage()
      if (oldUsage != "") {
        SlimeInformation newInformation = SlimeInformation.new()
        newInformation.setTitle("用例")
        newInformation.setText(oldUsage)
        newWord.getInformations().add(newInformation)
      }
      newWord.setDictionary($newDictionary)
      $newWords.add(newWord)
      updateProgress(i + 1, size)
    }
    return true
  }

}