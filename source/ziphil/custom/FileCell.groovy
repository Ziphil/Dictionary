package ziphil.custom

import groovy.transform.CompileStatic
import java.util.regex.Matcher
import javafx.scene.control.ListCell
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import ziphilib.transform.ConvertPrimitiveArgs
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class FileCell extends ListCell<File> {

  private static final Image DIRECTORY_ICON = createIcon("resource/icon/directory.png")
  private static final Image FILE_ICON = createIcon("resource/icon/file.png")
  private static final Image XDC_DICTIONARY_ICON = createIcon("resource/icon/xdc_dictionary.png")
  private static final Image OTM_DICTIONARY_ICON = createIcon("resource/icon/otm_dictionary.png")
  private static final Image CSV_DICTIONARY_ICON = createIcon("resource/icon/csv_dictionary.png")

  public FileCell() {
    super()
  }

  @ConvertPrimitiveArgs
  protected void updateItem(File file, Boolean isEmpty) {
    super.updateItem(file, isEmpty)
    if (isEmpty || file == null) {
      setText(null)
      setGraphic(null)
    } else {
      String name = file.getName()
      Image icon
      if (file.isDirectory()) {
        icon = DIRECTORY_ICON
      } else {
        if (name.endsWith(".xdc")) {
          icon = XDC_DICTIONARY_ICON
        } else if (name.endsWith(".json")) {
          icon = OTM_DICTIONARY_ICON
        } else if (name.endsWith(".csv")) {
          icon = CSV_DICTIONARY_ICON
        } else {
          icon = FILE_ICON
        }
      }
      setText(name)
      setGraphic(ImageView.new(icon))
    }
  }

  private static Image createIcon(String path) {
    return Image.new(DirectoryItem.getClassLoader().getResourceAsStream(path))
  }

}