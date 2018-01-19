package ziphil.custom

import groovy.transform.CompileStatic
import java.util.regex.Matcher
import javafx.scene.control.TreeCell
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import ziphil.Launcher
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class DirectoryCell extends TreeCell<File> {

  private static final Image DIRECTORY_ICON = createIcon("resource/icon/directory.png")
  private static final Image DRIVE_ICON = createIcon("resource/icon/drive.png")

  public DirectoryCell() {
    super()
  }

  protected void updateItem(File file, Boolean empty) {
    super.updateItem(file, empty)
    if (empty || file == null) {
      setText(null)
      setGraphic(null)
    } else {
      String path = file.toString()
      String name = file.getName()
      Image icon = (name.isEmpty()) ? DRIVE_ICON : DIRECTORY_ICON
      String separator = Launcher.FILE_SEPARATOR.replaceAll("\\\\", "\\\\\\\\")
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