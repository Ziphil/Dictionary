package ziphil.dictionary.personal

import groovy.transform.CompileStatic
import javafx.scene.image.Image
import ziphil.dictionary.Dictionary
import ziphil.dictionary.DictionaryLoader
import ziphil.dictionary.DictionaryFactory
import ziphil.dictionary.shaleia.ShaleiaDictionary
import ziphil.dictionary.slime.SlimeDictionary
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class BinaryDictionaryFactory extends DictionaryFactory {

  private static final String NAME = "PDIC-DIC形式"
  private static final String EXTENSION = "dic"
  private static final String ICON_PATH = "resource/icon/dic_dictionary.png"

  public Dictionary loadDictionary(File file) {
    BinaryDictionaryLoader loader = BinaryDictionaryLoader.new(file.getPath())
    Dictionary dictionary = BinaryDictionary.new(file.getName(), file.getPath(), loader)
    return dictionary
  }

  public Dictionary loadEmptyDictionary(File file) {
    Dictionary dictionary = BinaryDictionary.new(file.getName(), file.getPath())
    return dictionary
  }

  public Dictionary convertDictionary(File file, DictionaryLoader converter) {
    Dictionary dictionary = BinaryDictionary.new(file.getName(), file.getPath(), converter)
    return dictionary
  }

  public Image createIcon() {
    Image icon = Image.new(getClass().getClassLoader().getResourceAsStream(ICON_PATH))
    return icon
  }

  public Boolean isCreatable() {
    return false
  }

  public String getName() {
    return NAME
  }

  public String getExtension() {
    return EXTENSION
  }

  public Class<? extends Dictionary> getDictionaryClass() {
    return BinaryDictionary
  }

}