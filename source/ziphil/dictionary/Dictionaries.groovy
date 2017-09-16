package ziphil.dictionary

import groovy.transform.CompileStatic
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

  public static Dictionary loadDictionary(File file) {
    if (file != null) {
      if (file.exists() && file.isFile()) {
        Dictionary dictionary = null
        String fileName = file.getName()
        String filePath = file.getPath()
        if (filePath.endsWith(".xdc")) {
          dictionary = ShaleiaDictionary.new(fileName, filePath)
        } else if (filePath.endsWith(".csv")) {
          dictionary = PersonalDictionary.new(fileName, filePath)
        } else if (filePath.endsWith(".json")) {
          dictionary = SlimeDictionary.new(fileName, filePath)
        }
        return dictionary
      } else {
        return null
      }
    } else {
      return null
    }
  }

  public static Dictionary loadEmptyDictionary(DictionaryType type, File file) {
    if (file != null) {
      Dictionary dictionary = null
      String fileName = file.getName()
      String filePath = file.getPath()
      if (type == DictionaryType.SHALEIA) {
        dictionary = ShaleiaDictionary.new(fileName, null)
        dictionary.setPath(filePath)
      } else if (type == DictionaryType.PERSONAL) {
        dictionary = PersonalDictionary.new(fileName, null)
        dictionary.setPath(filePath)
      } else if (type == DictionaryType.SLIME) {
        dictionary = SlimeDictionary.new(fileName, null)
        dictionary.setPath(filePath)
      }
      return dictionary
    } else {
      return null
    }
  }

  public static Dictionary convertDictionary(DictionaryType type, Dictionary oldDictionary, File file) {
    if (file != null) {
      Dictionary dictionary = null
      String fileName = file.getName()
      String filePath = file.getPath()
      if (type == DictionaryType.SHALEIA) {
        dictionary = ShaleiaDictionary.new(fileName, filePath, oldDictionary)
      } else if (type == DictionaryType.PERSONAL) {
        dictionary = PersonalDictionary.new(fileName, filePath, oldDictionary)
      } else if (type == DictionaryType.SLIME) {
        dictionary = SlimeDictionary.new(fileName, filePath, oldDictionary)
      }
      return dictionary
    } else {
      return null
    }
  }

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

  public static Boolean checkWordType(Dictionary dictionary, Word word) {
    if (dictionary != null && word != null) {
      if (dictionary instanceof ShaleiaDictionary && word instanceof ShaleiaWord) {
        return true
      } else if (dictionary instanceof PersonalDictionary && word instanceof PersonalWord) {
        return true
      } else if (dictionary instanceof SlimeDictionary && word instanceof SlimeWord) {
        return true
      } else {
        return false
      }
    } else {
      return false
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