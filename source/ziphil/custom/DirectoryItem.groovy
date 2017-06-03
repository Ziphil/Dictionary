package ziphil.custom

import groovy.transform.CompileStatic
import javafx.collections.ObservableList
import javafx.scene.control.TreeItem
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class DirectoryItem extends TreeItem<File> {

  public DirectoryItem(File file){
    super(file)
  }

  public Boolean isLeaf() {
    return false
  }

  public ObservableList<TreeItem<File>> getChildren() {
    ObservableList<TreeItem<File>> children = super.getChildren()
    File file = getValue()
    if (children.isEmpty() && file != null) {
      for (File childFile : file.listFiles()) {
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