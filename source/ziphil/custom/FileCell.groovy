package ziphil.custom

import groovy.transform.CompileStatic
import java.util.regex.Matcher
import javafx.scene.control.ListCell
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import ziphilib.transform.ConvertPrimitive


@CompileStatic @Newify
public class FileCell extends ListCell<File> {

  private static final Image DIRECTORY_ICON = createIcon("resource/icon/directory.png")
  private static final Image FILE_ICON = createIcon("resource/icon/file.png")

  public FileCell() {
    super()
  }

  @ConvertPrimitive
  protected void updateItem(File file, Boolean isEmpty) {
    super.updateItem(file, isEmpty)
    if (isEmpty || file == null) {
      setText(null)
      setGraphic(null)
    } else {
      String name = file.getName()
      Image icon = (file.isDirectory()) ? DIRECTORY_ICON : FILE_ICON
      setText(name)
      setGraphic(ImageView.new(icon))
    }
  }

  private static Image createIcon(String path) {
    return Image.new(DirectoryItem.getClassLoader().getResourceAsStream(path))
  }

}