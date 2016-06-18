package ziphil.controller

import groovy.transform.CompileStatic
import java.util.concurrent.Callable
import javafx.beans.binding.Bindings
import javafx.beans.binding.StringBinding
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.ComboBox
import javafx.scene.control.CheckBox
import javafx.scene.control.Label
import javafx.scene.control.Spinner
import javafx.scene.control.TextField
import javafx.scene.control.ToggleButton
import javafx.scene.layout.GridPane
import javafx.scene.layout.HBox
import javafx.scene.text.Font
import javafx.stage.Stage
import javafx.stage.StageStyle
import javafx.stage.Modality
import ziphil.custom.CustomBuilderFactory
import ziphil.custom.Measurement
import ziphil.module.Setting
import ziphil.node.UtilityStage


@CompileStatic @Newify
public class SettingController {

  private static final String RESOURCE_PATH = "resource/fxml/setting.fxml"
  private static final String TITLE = "設定"
  private static final Double DEFAULT_WIDTH = -1
  private static final Double DEFAULT_HEIGHT = -1

  @FXML private ComboBox<String> $contentFontNames
  @FXML private Spinner $contentFontSize
  @FXML private CheckBox $usesSystemContentFont
  @FXML private ComboBox<String> $editorFontNames
  @FXML private Spinner $editorFontSize
  @FXML private CheckBox $usesSystemEditorFont
  @FXML private ToggleButton $modifiesPunctuation
  @FXML private GridPane $registeredDictionaryPane
  @FXML private List<TextField> $registeredDictionaryPaths = ArrayList.new(10)
  @FXML private ToggleButton $savesAutomatically
  private Stage $stage
  private Scene $scene

  public SettingController(Stage stage) {
    $stage = stage
    loadResource()
  }

  @FXML
  private void initialize() {
    setupRegisteredDictionaryPane()
    setupContentFontNames()
    setupFontDisableBindings()
    setupTextBindings()
    applySetting()
  }

  private void applySetting() {
    Setting setting = Setting.getInstance()
    String contentFontFamily = setting.getContentFontFamily()
    Integer contentFontSize = setting.getContentFontSize()
    Boolean modifiesPunctuation = setting.modifiesPunctuation()
    Boolean savesAutomatically = setting.savesAutomatically()
    List<String> registeredDictionaryPaths = setting.getRegisteredDictionaryPaths()
    if (contentFontFamily != null) {
      $contentFontNames.getSelectionModel().select(contentFontFamily)
    } else {
      $usesSystemContentFont.setSelected(true)
    }
    if (contentFontSize != null) {
      $contentFontSize.getValueFactory().setValue(contentFontSize)
    }
    if (modifiesPunctuation) {
      $modifiesPunctuation.setSelected(true)
    }
    if (savesAutomatically) {
      $savesAutomatically.setSelected(true)
    }
    (0 ..< 10).each() { Integer i ->
      $registeredDictionaryPaths[i].setText(registeredDictionaryPaths[i])
    }
  }

  private void saveSetting() {
    Setting setting = Setting.getInstance()
    String contentFontFamily = $contentFontNames.getSelectionModel().getSelectedItem()
    Integer contentFontSize = $contentFontSize.getValue()
    Boolean usesSystemContentFont = $usesSystemContentFont.isSelected()
    Boolean modifiesPunctuation = $modifiesPunctuation.isSelected()
    Boolean savesAutomatically = $savesAutomatically.isSelected()
    if (!usesSystemContentFont && contentFontFamily != null) {
      setting.setContentFontFamily(contentFontFamily)
      setting.setContentFontSize(contentFontSize)
    } else {
      setting.setContentFontFamily(null)
      setting.setContentFontSize(null)
    }
    setting.setModifiesPunctuation(modifiesPunctuation)
    setting.setSavesAutomatically(savesAutomatically)
    (0 ..< 10).each() { Integer i ->
      String path = $registeredDictionaryPaths[i].getText()
      setting.getRegisteredDictionaryPaths()[i] = (path != "") ? path : null
    }
    setting.save()
  }

  private void browseDictionary(Integer i) {
    UtilityStage<File> stage = UtilityStage.new(StageStyle.UTILITY)
    DictionaryChooserController controller = DictionaryChooserController.new(stage)
    stage.initModality(Modality.WINDOW_MODAL)
    stage.initOwner($stage)
    File file = stage.showAndWaitResult()
    if (file != null) {
      $registeredDictionaryPaths[i].setText(file.getAbsolutePath())
    }
  }

  @FXML
  private void commitChange() {
    saveSetting()
    $stage.close()
  }

  @FXML
  private void cancelChange() {
    $stage.close()
  }

  private void setupRegisteredDictionaryPane() {
    (0 ..< 10).each() { Integer i ->
      Label number = Label.new("登録辞書${(i + 1) % 10}:")
      HBox box = HBox.new()
      TextField dictionaryPath = TextField.new()
      Button browse = Button.new("参照")
      dictionaryPath.setPrefWidth(Measurement.rpx(400))
      dictionaryPath.setMinWidth(Measurement.rpx(400))
      dictionaryPath.getStyleClass().add("left-pill")
      browse.setPrefWidth(Measurement.rpx(60))
      browse.setMinWidth(Measurement.rpx(60))
      browse.getStyleClass().add("right-pill")
      browse.setOnAction() {
        browseDictionary(i)
      }
      box.getChildren().addAll(dictionaryPath, browse)
      $registeredDictionaryPaths[i] = dictionaryPath
      $registeredDictionaryPane.add(number, 0, i)
      $registeredDictionaryPane.add(box, 1, i)
    }
  }

  private void setupContentFontNames() {
    List<String> fontNames = Font.getFontNames()
    $contentFontNames.getItems().addAll(fontNames)
  }

  private void setupFontDisableBindings() {
    $contentFontNames.disableProperty().bind($usesSystemContentFont.selectedProperty())
    $contentFontSize.disableProperty().bind($usesSystemContentFont.selectedProperty())
  }

  private void setupTextBindings() {
    Callable<String> modifiesPunctuationFunction = (Callable){
      return ($modifiesPunctuation.selectedProperty().get()) ? "有効" : "無効"
    }
    Callable<String> savesAutomaticallyFunction = (Callable){
      return ($savesAutomatically.selectedProperty().get()) ? "有効" : "無効"
    }
    StringBinding modifiesPunctuationBinding = Bindings.createStringBinding(modifiesPunctuationFunction, $modifiesPunctuation.selectedProperty())
    StringBinding savesAutomaticallyBinding = Bindings.createStringBinding(savesAutomaticallyFunction, $savesAutomatically.selectedProperty())
    $modifiesPunctuation.textProperty().bind(modifiesPunctuationBinding)
    $savesAutomatically.textProperty().bind(savesAutomaticallyBinding)
  }

  private void loadResource() {
    FXMLLoader loader = FXMLLoader.new(getClass().getClassLoader().getResource(RESOURCE_PATH), null, CustomBuilderFactory.new())
    loader.setController(this)
    Parent root = (Parent)loader.load()
    $scene = Scene.new(root, DEFAULT_WIDTH, DEFAULT_HEIGHT)
    $stage.setScene($scene)
    $stage.setTitle(TITLE)
    $stage.setResizable(false)
    $stage.sizeToScene()
  }

}