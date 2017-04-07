package ziphil.dictionary

import groovy.transform.CompileStatic
import javafx.scene.image.Image
import ziphil.custom.ExtensionFilter
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public enum DictionaryType {

  SHALEIA("シャレイア語辞典形式", "xdc"), PERSONAL("PDIC-CSV形式", "csv"), SLIME("OneToMany-JSON形式", "json")

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
      path = "resource/icon/json_dictionary.png"
    }
    return Image.new(getClass().getClassLoader().getResourceAsStream(path))
  }

  public ExtensionFilter createExtensionFilter() {
    return ExtensionFilter.new($name, $extension)
  }

  public String getName() {
    return $name
  }

  public String getExtension() {
    return $extension
  }

}