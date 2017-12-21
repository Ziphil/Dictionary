package ziphil.dictionary.slime.converter

import groovy.transform.CompileStatic
import ziphil.dictionary.DictionaryConverter
import ziphil.dictionary.Word
import ziphil.dictionary.personal.PersonalDictionary
import ziphil.dictionary.personal.PersonalWord
import ziphil.dictionary.slime.SlimeDictionary
import ziphil.dictionary.slime.SlimeInformation
import ziphil.dictionary.slime.SlimeWord
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class SlimePersonalDictionaryConverter extends DictionaryConverter<SlimeDictionary, PersonalDictionary, SlimeWord> {

  public SlimePersonalDictionaryConverter(PersonalDictionary sourceDictionary) {
    super(sourceDictionary)
  }

  protected BooleanClass convert() {
    List<PersonalWord> sourceWords = $sourceDictionary.getRawWords()
    Int size = sourceWords.size()
    for (Int i = 0 ; i < size ; i ++) {
      if (isCancelled()) {
        return false
      }
      PersonalWord sourceWord = sourceWords[i]
      SlimeWord word = SlimeWord.new()
      word.setId(i + 1)
      word.setName(sourceWord.getName())
      String sourcePronunciation = sourceWord.getPronunciation()
      if (sourcePronunciation != "") {
        SlimeInformation information = SlimeInformation.new()
        information.setTitle("発音")
        information.setText(sourcePronunciation)
        word.getInformations().add(information)
      }
      String sourceTranslation = sourceWord.getTranslation()
      if (sourceTranslation != "") {
        SlimeInformation information = SlimeInformation.new()
        information.setTitle("訳語")
        information.setText(sourceTranslation)
        word.getInformations().add(information)
      }
      String sourceUsage = sourceWord.getUsage()
      if (sourceUsage != "") {
        SlimeInformation information = SlimeInformation.new()
        information.setTitle("用例")
        information.setText(sourceUsage)
        word.getInformations().add(information)
      }
      word.setDictionary($dictionary)
      $words.add(word)
      updateProgress(i + 1, size)
    }
    return true
  }

}