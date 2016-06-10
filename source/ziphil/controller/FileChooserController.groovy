package ziphil.controller

import groovy.transform.CompileStatic
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.control.TextArea
import javafx.scene.control.TextField
import ziphil.custom.FileChooser
import ziphil.custom.DirectoryItem
import ziphil.dictionary.ShaleiaWord
import ziphil.node.UtilityStage


@CompileStatic @Newify
public class FileChooserController {

  private static final String RESOURCE_PATH = "resource/fxml/file_chooser.fxml"
  private static final String TITLE = "参照"
  private static final Integer DEFAULT_WIDTH = 640
  private static final Integer DEFAULT_HEIGHT = 480

  @FXML private FileChooser $chooser
  private UtilityStage<File> $stage
  private Scene $scene

  public FileChooserController(UtilityStage<File> stage) {
    $stage = stage
    loadResource()
  }

  @FXML
  private void commitChoose() {
    File file = $chooser.getSelectedFile()
    $stage.close(file)
  }

  @FXML
  private void cancelChoose() {
    $stage.close(null)
  }

  private void loadResource() {
    FXMLLoader loader = FXMLLoader.new(getClass().getClassLoader().getResource(RESOURCE_PATH))
    loader.setController(this)
    Parent root = (Parent)loader.load()
    $scene = Scene.new(root, DEFAULT_WIDTH, DEFAULT_HEIGHT)
    $stage.setScene($scene)
    $stage.setTitle(TITLE)
    $stage.sizeToScene()
  }

}