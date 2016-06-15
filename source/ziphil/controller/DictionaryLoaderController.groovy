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
import ziphil.module.DictionarySetting
import ziphil.module.Setting
import ziphil.node.UtilityStage


@CompileStatic @Newify
public class DictionaryLoaderController {

  private static final String RESOURCE_PATH = "resource/fxml/dictionary_loader.fxml"
  private static final Map<String, String> TYPE_NAMES = [("PDIC-CSV形式"): "PERSONAL", ("シャレイア語辞典形式"): "SHALEIA"]
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
    String name = $name.getText()
    String typeName = TYPE_NAMES[$type.getValue()]
    String path = $path.getText()
    DictionarySetting setting = DictionarySetting.new(name, typeName, path)
    Setting.getInstance().getDictionarySettings().add(setting)
    $stage.close(true)
  }

  @FXML
  private void cancelLoad() {
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