package ziphil.dictionary

import groovy.transform.CompileStatic
import javafx.scene.image.Image
import ziphil.custom.ExtensionFilter
import ziphil.dictionary.personal.PersonalDictionary
import ziphil.dictionary.shaleia.ShaleiaDictionary
import ziphil.dictionary.slime.SlimeDictionary
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public enum DictionaryType {

  SLIME("OneToMany-JSON形式", "json", "otm_dictionary.png"),
  PERSONAL("PDIC-CSV形式", "csv", "csv_dictionary.png"),
  BINARY("PDIC-DIC形式", "dic", "csv_dictionary.png"),
  SHALEIA("シャレイア語辞典形式", "xdc", "xdc_dictionary.png")

  private static final String ICON_DIRECTORY = "resource/icon/"

  private String $name = ""
  private String $extension = ""
  private String $iconPath = ""

  private DictionaryType(String name, String extension, String iconPath) {
    $name = name
    $extension = extension
    $iconPath = iconPath
  }

  public Image createIcon() {
    String path = ICON_DIRECTORY + $iconPath
    return Image.new(getClass().getClassLoader().getResourceAsStream(path))
  }

  public ExtensionFilter createExtensionFilter() {
    return ExtensionFilter.new($name, $extension)
  }

  public static DictionaryType valueOfDictionary(Dictionary dictionary) {
    if (dictionary instanceof ShaleiaDictionary) {
      return DictionaryType.SHALEIA
    } else if (dictionary instanceof PersonalDictionary) {
      return DictionaryType.PERSONAL
    } else if (dictionary instanceof SlimeDictionary) {
      return DictionaryType.SLIME
    } else {
      return null
    }
  }

  public String getName() {
    return $name
  }

  public String getExtension() {
    return $extension
  }

}