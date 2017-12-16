package ziphil.dictionary.personal

import groovy.transform.CompileStatic
import javafx.scene.image.Image
import ziphil.dictionary.Dictionary
import ziphil.dictionary.DictionaryFactory
import ziphil.dictionary.shaleia.ShaleiaDictionary
import ziphil.dictionary.slime.SlimeDictionary
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class BinaryDictionaryFactory extends DictionaryFactory {

  private static final String NAME = "PDIC-DIC形式"
  private static final String EXTENSION = "dic"
  private static final Boolean CREATABLE = false
  private static final String ICON_PATH = "resource/icon/dic_dictionary.png"

  public Dictionary loadDictionary(File file) {
    Dictionary dictionary = BinaryDictionary.new(file.getName(), file.getPath())
    return dictionary
  }

  public Dictionary loadEmptyDictionary(File file) {
    Dictionary dictionary = BinaryDictionary.new(file.getName(), null)
    dictionary.setPath(file.getPath())
    return dictionary
  }

  public Dictionary convertDictionary(Dictionary oldDictionary, File file) {
    Dictionary dictionary = BinaryDictionary.new(file.getName(), file.getPath(), oldDictionary)
    return dictionary
  }

  public Image createIcon() {
    Image icon = Image.new(getClass().getClassLoader().getResourceAsStream(ICON_PATH))
    return icon
  }

  public Boolean isConvertableFrom(Dictionary dictionary) {
    return false
  }

  public Boolean isCreatable() {
    return CREATABLE
  }

  public String getName() {
    return NAME
  }

  public String getExtension() {
    return EXTENSION
  }

}