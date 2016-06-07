package ziphil.node

import groovy.transform.CompileStatic
import javafx.scene.control.TreeView


@CompileStatic @Newify
public class FileChooser extends TreeView<String> {

  public FileChooser() {
    super()
    FileChooserItem root = FileChooserItem.new(null)
    File.listRoots().each() { File file ->
      root.getChildren().add(FileChooserItem.new(file))
    }
    setRoot(root)
    setShowRoot(false)
  }

}