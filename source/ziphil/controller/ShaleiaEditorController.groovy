package ziphil.controller

import groovy.transform.CompileStatic
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.control.TextArea
import javafx.scene.control.TextField
import ziphil.dictionary.ShaleiaWord
import ziphil.node.UtilityStage


@CompileStatic @Newify
public class ShaleiaEditorController {

  private static final String RESOURCE_PATH = "resource/fxml/shaleia_editor.fxml"
  private static final String TITLE = "単語編集"
  private static final Integer DEFAULT_WIDTH = 640
  private static final Integer DEFAULT_HEIGHT = 320

  @FXML private TextField $name
  @FXML private TextArea $data
  private ShaleiaWord $word
  private UtilityStage<Boolean> $stage
  private Scene $scene

  public ShaleiaEditorController(UtilityStage<Boolean> stage) {
    $stage = stage
    loadResource()
  }

  public void prepare(ShaleiaWord word) {
    $word = word
    $name.setText(word.getUniqueName())
    $data.setText(word.getData())
    $data.requestFocus()
  }

  @FXML
  private void commitEdit() {
    String name = $name.getText()
    String data = $data.getText()
    $word.update(name, data)
    $stage.close(true)
  }

  @FXML
  private void cancelEdit() {
    $stage.close(false)
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