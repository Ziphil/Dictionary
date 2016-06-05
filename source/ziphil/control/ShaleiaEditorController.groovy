package ziphil.control

import groovy.transform.CompileStatic
import javafx.fxml.Initializable
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.control.TextArea
import javafx.scene.control.TextField
import ziphil.dictionary.ShaleiaWord


@CompileStatic @Newify
public class ShaleiaEditorController {

  private static String RESOURCE_PATH = "resource/fxml/shaleia_editor.fxml"
  private static final String TITLE = "単語編集"
  private static Integer DEFAULT_WIDTH = 640
  private static Integer DEFAULT_HEIGHT = 320

  @FXML private TextField $name
  @FXML private TextArea $content

  private ShaleiaWord $word
  
  private UtilityStage<Boolean> $stage
  private Scene $scene

  public ShaleiaEditorController(UtilityStage<Boolean> stage) {
    $stage = stage
    loadResource()
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

  public void prepare(ShaleiaWord word) {
    $word = word
    $name.setText(word.getUniqueName())
    $content.setText(word.getContent().replaceAll(/^\*\s*(.+)\n/, ""))
    $content.requestFocus()
  }

  @FXML
  private void commitEdit() {
    String name = $name.getText()
    String content = $content.getText()
    $word.update("* ${name}\n${content}")
    $stage.close(true)
  }

  @FXML
  private void cancelEdit() {
    $stage.close(false)
  }

}