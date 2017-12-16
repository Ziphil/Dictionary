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

  protected BooleanClass convert() {
    List<PersonalWord> oldWords = $oldDictionary.getRawWords()
    Int size = oldWords.size()
    for (Int i = 0 ; i < size ; i ++) {
      if (isCancelled()) {
        return false
      }
      PersonalWord oldWord = oldWords[i]
      SlimeWord newWord = SlimeWord.new()
      newWord.setId(i + 1)
      newWord.setName(oldWord.getName())
      String oldPronunciation = oldWord.getPronunciation()
      if (oldPronunciation != "") {
        SlimeInformation newInformation = SlimeInformation.new()
        newInformation.setTitle("発音")
        newInformation.setText(oldPronunciation)
        newWord.getInformations().add(newInformation)
      }
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