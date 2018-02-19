package ziphil.dictionary.personal

import groovy.transform.CompileStatic
import javafx.scene.image.Image
import ziphil.dictionary.Dictionary
import ziphil.dictionary.DictionaryFactory
import ziphil.dictionary.Loader
import ziphil.dictionary.Saver
import ziphil.dictionary.shaleia.ShaleiaDictionary
import ziphil.dictionary.slime.SlimeDictionary
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class BinaryDictionaryFactory extends DictionaryFactory {

  private static final String NAME = "PDIC-DIC形式"
  private static final String EXTENSION = "dic"
  private static final String ICON_PATH = "resource/icon/dic_dictionary.png"

  protected Dictionary create(File file, Loader loader) {
    if (loader != null) {
      BinaryDictionary dictionary = BinaryDictionary.new(file.getName(), file.getPath(), loader)
      return dictionary
    } else {
      BinaryDictionary dictionary = BinaryDictionary.new(file.getName(), file.getPath())
      return dictionary
    }
  }

  protected Loader createLoader(File file) {
    BinaryLoader loader = BinaryLoader.new(file.getPath())
    return loader
  }

  protected Saver createSaver() {
    return null
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