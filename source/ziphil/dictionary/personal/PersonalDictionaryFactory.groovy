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
public class PersonalDictionaryFactory extends DictionaryFactory {

  private static final String NAME = "PDIC-CSV形式"
  private static final String EXTENSION = "csv"
  private static final String ICON_PATH = "resource/icon/csv_dictionary.png"

  protected Dictionary create(File file, Loader loader) {
    if (loader != null) {
      PersonalDictionary dictionary = PersonalDictionary.new(file.getName(), file.getPath(), loader)
      return dictionary
    } else {
      PersonalDictionary dictionary = PersonalDictionary.new(file.getName(), file.getPath())
      return dictionary
    }
  }

  protected Loader createLoader(File file) {
    PersonalLoader loader = PersonalLoader.new(file.getPath())
    return loader
  }

  protected Saver createSaver() {
    PersonalSaver saver = PersonalSaver.new()
    return saver
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
    return PersonalDictionary
  }

}