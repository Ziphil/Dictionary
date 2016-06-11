package ziphil.custom

import groovy.transform.CompileStatic
import javafx.collections.ObservableList
import javafx.scene.control.TreeItem


@CompileStatic @Newify
public class DirectoryItem extends TreeItem<File> {

  public DirectoryItem(File file){
    super(file)
  }

  public boolean isLeaf() {
    File file = getValue()
    if (file != null) {
      return getChildren().isEmpty()
    } else {
      return false
    }
  }

  public ObservableList<TreeItem<File>> getChildren() {
    ObservableList<TreeItem<File>> children = super.getChildren()
    File file = getValue()
    if (children.isEmpty() && file != null) {
      file.listFiles().each() { File childFile ->
        if (!childFile.isFile()) {
          children.add(DirectoryItem.new(childFile))
        }
      }
      return children
    } else {
      return children
    }
  }

}