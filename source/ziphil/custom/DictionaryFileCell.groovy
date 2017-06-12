package ziphil.custom

import groovy.transform.CompileStatic
import javafx.scene.control.ListCell
import javafx.scene.image.Image
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class DictionaryFileCell extends FileCell {

  private static final Image XDC_DICTIONARY_ICON = createIcon("resource/icon/xdc_dictionary.png")
  private static final Image OTM_DICTIONARY_ICON = createIcon("resource/icon/otm_dictionary.png")
  private static final Image CSV_DICTIONARY_ICON = createIcon("resource/icon/csv_dictionary.png")

  public DictionaryFileCell() {
    super()
  }

  protected Image selectIcon(String fileName) {
    Image icon = null
    if (fileName.endsWith(".xdc")) {
      icon = XDC_DICTIONARY_ICON
    } else if (fileName.endsWith(".json")) {
      icon = OTM_DICTIONARY_ICON
    } else if (fileName.endsWith(".csv")) {
      icon = CSV_DICTIONARY_ICON
    }
    return icon
  }

  private static Image createIcon(String path) {
    return Image.new(DirectoryItem.getClassLoader().getResourceAsStream(path))
  }

}