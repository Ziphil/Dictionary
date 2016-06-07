package ziphil.node

import groovy.transform.CompileStatic
import java.util.regex.Matcher
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.scene.control.TreeItem


@CompileStatic @Newify
public class FileChooserItem extends TreeItem<String> {

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