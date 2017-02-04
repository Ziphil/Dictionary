package ziphil.dictionary

import groovy.transform.CompileStatic
import ziphil.dictionary.personal.PersonalDictionary
import ziphil.dictionary.shaleia.ShaleiaDictionary
import ziphil.dictionary.slime.SlimeDictionary
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify 
public class Dictionaries {

  public static Dictionary loadDictionary(File file) {
    if (file.exists() && file.isFile()) {
      Dictionary dictionary
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
  }

  public static Dictionary loadEmptyDictionary(File file) {
    Dictionary dictionary
    String fileName = file.getName()
    String filePath = file.getPath()
    if (filePath.endsWith(".xdc")) {
      dictionary = ShaleiaDictionary.new(fileName, null)
      dictionary.setPath(filePath)
    } else if (filePath.endsWith(".csv")) {
      dictionary = PersonalDictionary.new(fileName, null)
      dictionary.setPath(filePath)
    } else if (filePath.endsWith(".json")) {
      dictionary = SlimeDictionary.new(fileName, null)
      dictionary.setPath(filePath)
    }
    return dictionary
  }

  public static String getExtension(Dictionary dictionary) {
    String extension
    if (dictionary instanceof ShaleiaDictionary) {
      extension = "xdc"
    } else if (dictionary instanceof PersonalDictionary) {
      extension = "csv"
    } else if (dictionary instanceof SlimeDictionary) {
      extension = "json"
    }
    return extension
  }

}