package ziphil.dictionary.slime

import groovy.transform.CompileStatic
import javafx.scene.image.Image
import ziphil.dictionary.Dictionary
import ziphil.dictionary.DictionaryConverter
import ziphil.dictionary.DictionaryFactory
import ziphil.dictionary.personal.PersonalDictionary
import ziphil.dictionary.shaleia.ShaleiaDictionary
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class SlimeDictionaryFactory extends DictionaryFactory {

  private static final String NAME = "OneToMany-JSON形式"
  private static final String EXTENSION = "json"
  private static final String ICON_PATH = "resource/icon/otm_dictionary.png"

  public Dictionary loadDictionary(File file) {
    Dictionary dictionary = SlimeDictionary.new(file.getName(), file.getPath())
    return dictionary
  }

  public Dictionary loadEmptyDictionary(File file) {
    Dictionary dictionary = SlimeDictionary.new(file.getName(), null)
    dictionary.setPath(file.getPath())
    return dictionary
  }

  public Dictionary convertDictionary(File file, DictionaryConverter converter) {
    Dictionary dictionary = SlimeDictionary.new(file.getName(), file.getPath(), converter)
    return dictionary
  }

  public Image createIcon() {
    Image icon = Image.new(getClass().getClassLoader().getResourceAsStream(ICON_PATH))
    return icon
  }

  public Boolean isCreatable() {
    return true
  }

  public String getName() {
    return NAME
  }

  public String getExtension() {
    return EXTENSION
  }

  public Class<? extends Dictionary> getDictionaryClass() {
    return SlimeDictionary
  }

}