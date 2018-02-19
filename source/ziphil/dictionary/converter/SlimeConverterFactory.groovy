package ziphil.dictionary.converter

import groovy.transform.CompileStatic
import ziphil.dictionary.ConverterFactory
import ziphil.dictionary.Dictionary
import ziphil.dictionary.DictionaryFactory
import ziphil.dictionary.Loader
import ziphil.dictionary.personal.PersonalDictionary
import ziphil.dictionary.shaleia.ShaleiaDictionary
import ziphil.dictionary.slime.SlimeDictionary
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class SlimeConverterFactory extends ConverterFactory {

  public Loader create(DictionaryFactory factory, Dictionary sourceDictionary) {
    Loader converter = null
    if (factory.getDictionaryClass() == SlimeDictionary) {
      if (sourceDictionary instanceof PersonalDictionary) {
        converter = SlimePersonalConverter.new(sourceDictionary)
      } else if (sourceDictionary instanceof ShaleiaDictionary) {
        converter = SlimeShaleiaConverter.new(sourceDictionary)
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