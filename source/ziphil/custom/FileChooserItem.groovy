package ziphil.custom

import groovy.transform.CompileStatic
import java.util.regex.Matcher
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.scene.control.TreeItem
import javafx.scene.image.Image
import javafx.scene.image.ImageView


@CompileStatic @Newify
public class FileChooserItem extends TreeItem<String> {

  private static final String FILE_ICON_PATH = "resource/icon/file.png"
  private static final String DIRECTORY_ICON_PATH = "resource/icon/directory.png"
  private static final String DRIVE_ICON_PATH = "resource/icon/drive.png"

  private File $file 

  public FileChooserItem(File file){
    super(file)
    $file = file
    setupValue()
    setupGraphic()
  }

  private void setupValue() {
    if ($file != null) {
      String path = $file.toString()
      String separator = File.separator.replaceAll("\\\\", "\\\\\\\\")
      Matcher matcher = path =~ /.*${separator}(.+?)$/
      if (matcher.find()) {
        setValue(matcher.group(1))
      } else {
        setValue(path.replaceAll(separator, ""))
      }
    } else {
      setValue("")
    }
  }

  private void setupGraphic() {
    Image icon
    if ($file != null) {
      if ($file.isDirectory()) {
        String name = $file.getName()
        if (name == "") {
          icon = Image.new(getClass().getClassLoader().getResourceAsStream(DRIVE_ICON_PATH))
        } else {
          icon = Image.new(getClass().getClassLoader().getResourceAsStream(DIRECTORY_ICON_PATH))
        }
      } else {
        icon = Image.new(getClass().getClassLoader().getResourceAsStream(FILE_ICON_PATH))
      }
    }
    setGraphic(ImageView.new(icon))
  }

  public boolean isLeaf(){
    return $file != null && $file.isFile()
  }

  public ObservableList<TreeItem<String>> getChildren() {
    ObservableList<TreeItem<String>> children = super.getChildren()
    if (children.isEmpty() && $file != null) {
      if (!$file.isDirectory()) {
        return FXCollections.emptyObservableList()
      } else {
        $file.listFiles().each() { File childFile ->
          children.add(FileChooserItem.new(childFile))
        }
        return children
      }
    } else {
      return children
    }
  }

  public File getFile() {
    return $file
  }

}