package ziphil.custom

import groovy.transform.CompileStatic
import java.util.regex.Matcher
import javafx.scene.control.TreeCell
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import ziphilib.transform.ConvertPrimitive


@CompileStatic @Newify
public class DirectoryCell extends TreeCell<File> {

  private static final Image DIRECTORY_ICON = createIcon("resource/icon/directory.png")
  private static final Image DRIVE_ICON = createIcon("resource/icon/drive.png")

  public DirectoryCell() {
    super()
  }

  @ConvertPrimitive
  protected void updateItem(File file, Boolean isEmpty) {
    super.updateItem(file, isEmpty)
    if (isEmpty || file == null) {
      setText(null)
      setGraphic(null)
    } else {
      String path = file.toString()
      String name = file.getName()
      Image icon = (name == "") ? DRIVE_ICON : DIRECTORY_ICON
      String separator = File.separator.replaceAll("\\\\", "\\\\\\\\")
      Matcher matcher = path =~ /.*${separator}(.+?)$/
      if (matcher.find()) {
        setText(matcher.group(1))
      } else {
        setText(path.replaceAll(separator, ""))
      }
      setGraphic(ImageView.new(icon))
    }
  }

  private static Image createIcon(String path) {
    return Image.new(DirectoryItem.getClassLoader().getResourceAsStream(path))
  }

}