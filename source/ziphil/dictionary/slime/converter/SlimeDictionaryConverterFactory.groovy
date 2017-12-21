package ziphil.dictionary.slime.converter

import groovy.transform.CompileStatic
import ziphil.dictionary.Dictionary
import ziphil.dictionary.DictionaryConverter
import ziphil.dictionary.DictionaryConverterFactory
import ziphil.dictionary.DictionaryFactory
import ziphil.dictionary.personal.PersonalDictionary
import ziphil.dictionary.shaleia.ShaleiaDictionary
import ziphil.dictionary.slime.SlimeDictionary
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class SlimeDictionaryConverterFactory extends DictionaryConverterFactory {

  public DictionaryConverter create(DictionaryFactory factory, Dictionary sourceDictionary) {
    DictionaryConverter converter = null
    if (factory.getDictionaryClass() == SlimeDictionary) {
      if (sourceDictionary instanceof PersonalDictionary) {
        converter = SlimePersonalDictionaryConverter.new(sourceDictionary)
      } else if (sourceDictionary instanceof ShaleiaDictionary) {
        converter = SlimeShaleiaDictionaryConverter.new(sourceDictionary)
      }
    }
    return converter
  }

  public Boolean isAvailable(DictionaryFactory factory, Dictionary sourceDictionary) {
    if (factory.getDictionaryClass() == SlimeDictionary) {
      if (sourceDictionary instanceof PersonalDictionary) {
        return true
      } else if (sourceDictionary instanceof ShaleiaDictionary) {
        return true
      } else {
        return false
      }
    } else {
      return false
    }
  }

}