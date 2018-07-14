package ziphil.custom

import groovy.transform.CompileStatic
import javafx.scene.control.ListCell
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class FileCell extends ListCell<File> {

  private static final Image DIRECTORY_ICON = createIcon("resource/image/menu/directory.png")
  private static final Image FILE_ICON = createIcon("resource/image/menu/file.png")

  public FileCell() {
    super()
  }

  protected void updateItem(File file, Boolean empty) {
    super.updateItem(file, empty)
    if (empty || file == null) {
      setText(null)
      setGraphic(null)
    } else {
      String fileName = file.getName()
      Image icon
      if (file.isDirectory()) {
        icon = DIRECTORY_ICON
      } else {
        icon = selectIcon(fileName)
        if (icon == null) {
          icon = FILE_ICON
        }
      }
      setText(fileName)
      setGraphic(ImageView.new(icon))
    }
  }

  protected Image selectIcon(String fileName) {
    return null
  }

  private static Image createIcon(String path) {
    return Image.new(DirectoryItem.getClassLoader().getResourceAsStream(path))
  }

}