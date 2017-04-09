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

  SLIME("OneToMany-JSON形式", "json"), PERSONAL("PDIC-CSV形式", "csv"), SHALEIA("シャレイア語辞典形式", "xdc")

  private String $name = ""
  private String $extension = ""

  private DictionaryType(String name, String extension) {
    $name = name
    $extension = extension
  }

  public Image createIcon() {
    String path = ""
    if (this == SHALEIA) {
      path = "resource/icon/xdc_dictionary.png"
    } else if (this == PERSONAL) {
      path = "resource/icon/csv_dictionary.png"
    } else if (this == SLIME) {
      path = "resource/icon/otm_dictionary.png"
    }
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