package ziphil.controller

import groovy.transform.CompileStatic
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.control.ComboBox
import javafx.scene.control.TextField
import javafx.stage.Modality
import javafx.stage.StageStyle
import ziphil.node.UtilityStage


@CompileStatic @Newify
public class DictionaryLoaderController {

  private static final String RESOURCE_PATH = "resource/fxml/dictionary_loader.fxml"
  private static final String DICTIONARY_DATA_PATH = "data/dictionaries.zpdt"
  private static final Map<String, String> TYPES = [("PDIC-CSV形式"): "personal", ("シャレイア語辞典形式"): "shaleia"]
  private static final String TITLE = "新規辞書の追加"
  private static final Integer DEFAULT_WIDTH = 480
  private static final Integer DEFAULT_HEIGHT = -1

  @FXML private TextField $path
  @FXML private TextField $name
  @FXML private ComboBox $type
  private UtilityStage<Boolean> $stage
  private Scene $scene

  public DictionaryLoaderController(UtilityStage<Boolean> stage) {
    $stage = stage
    loadResource()
  }

  @FXML
  private void browseFiles() {
    UtilityStage<File> stage = UtilityStage.new(StageStyle.UTILITY)
    DictionaryChooserController controller = DictionaryChooserController.new(stage)
    stage.initModality(Modality.WINDOW_MODAL)
    stage.initOwner($stage)
    File file = stage.showAndWaitResult()
    if (file != null) {
      $path.setText(file.toString())
    }
  }

  @FXML
  private void commitLoad() {
    saveDictionary()
    $stage.close(true)
  }

  @FXML
  private void cancelLoad() {
    $stage.close(false)
  }

  private void saveDictionary() {
    String name = $name.getText()
    String typeText = $type.getValue()
    String path = $path.getText()
    File file = File.new(DICTIONARY_DATA_PATH)
    file.append("\"${name}\", \"${TYPES[typeText]}\", \"${path}\"\n", "UTF-8")
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