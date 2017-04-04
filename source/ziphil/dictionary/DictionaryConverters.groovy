package ziphil.dictionary

import groovy.transform.CompileStatic
import ziphil.dictionary.converter.IdentityDictionaryConverter
import ziphil.dictionary.converter.PersonalSlimeDictionaryConverter
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class DictionaryConverters {

  public static DictionaryConverter createConverter(String oldExtension, String newExtension) {
    DictionaryConverter converter = null
    if (oldExtension != newExtension) {
      if (oldExtension == "csv") {
        if (newExtension == "json") {
          converter = PersonalSlimeDictionaryConverter.new()
        }
      }
    } else {
      converter = IdentityDictionaryConverter.new()
    }
    return converter
  }

}