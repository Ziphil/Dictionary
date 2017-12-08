package ziphil.dictionary

import groovy.transform.CompileStatic
import ziphil.dictionary.personal.BinaryDictionary
import ziphil.dictionary.personal.PersonalDictionary
import ziphil.dictionary.personal.PersonalWord
import ziphil.dictionary.shaleia.ShaleiaDictionary
import ziphil.dictionary.shaleia.ShaleiaWord
import ziphil.dictionary.slime.SlimeDictionary
import ziphil.dictionary.slime.SlimeIndividualSetting
import ziphil.dictionary.slime.SlimeWord
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify 
public class Dictionaries {

  public static IndividualSetting createIndividualSetting(Dictionary dictionary) {
    if (dictionary != null) {
      if (dictionary instanceof SlimeDictionary) {
        return SlimeIndividualSetting.create(dictionary)
      } else {
        return null
      }
    } else {
      return null
    }
  }

  public static String plainNameOf(Dictionary dictionary) {
    if (dictionary != null) {
      String plainName = null
      if (dictionary instanceof ShaleiaDictionary) {
        plainName = "shaleia"
      } else if (dictionary instanceof PersonalDictionary) {
        plainName = "personal"
      } else if (dictionary instanceof SlimeDictionary) {
        plainName = "slime"
      }
      return plainName
    } else {
      return null
    }
  }

}