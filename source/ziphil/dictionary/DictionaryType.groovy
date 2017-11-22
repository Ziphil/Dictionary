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

  SLIME("OneToMany-JSON形式", "json", "otm_dictionary.png", true, true),
  PERSONAL("PDIC-CSV形式", "csv", "csv_dictionary.png", true, true),
  BINARY("PDIC-DIC形式", "dic", "dic_dictionary.png", false, false),
  SHALEIA("シャレイア語辞典形式", "xdc", "xdc_dictionary.png", true, false)

  private static final String ICON_DIRECTORY = "resource/icon/"

  private String $name
  private String $extension
  private String $iconPath
  private Boolean $creatable
  private Boolean $convertable

  private DictionaryType(String name, String extension, String iconPath, Boolean creatable, Boolean convertable) {
    $name = name
    $extension = extension
    $iconPath = iconPath
    $creatable = creatable
    $convertable = convertable
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

  public Boolean isCreatable() {
    return $creatable
  }

  public Boolean isConvertable() {
    return $convertable
  }

}