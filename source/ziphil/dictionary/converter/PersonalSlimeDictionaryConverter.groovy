package ziphil.dictionary.converter

import groovy.transform.CompileStatic
import ziphil.dictionary.DictionaryConverter
import ziphil.dictionary.Word
import ziphil.dictionary.personal.PersonalDictionary
import ziphil.dictionary.personal.PersonalWord
import ziphil.dictionary.slime.SlimeInformation
import ziphil.dictionary.slime.SlimeDictionary
import ziphil.dictionary.slime.SlimeWord
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class PersonalSlimeDictionaryConverter implements DictionaryConverter<PersonalDictionary, SlimeDictionary> {

  public SlimeDictionary convert(PersonalDictionary oldDictionary) {
    SlimeDictionary newDictionary = SlimeDictionary.new(oldDictionary.getName(), null)
    List<PersonalWord> oldWords = oldDictionary.getRawWords()
    for (Integer newId : 0 ..< oldWords.size()) {
      PersonalWord oldWord = oldWords[newId]
      SlimeWord newWord = SlimeWord.new()
      newWord.setId(newId)
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
      newWord.setDictionary(newDictionary)
      newDictionary.getRawWords().add(newWord)
    }
    newDictionary.updateFirst()
    for (Word word : newDictionary.getRawWords()) {
      word.update()
    }
    return newDictionary
  }

}