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
import ziphil.custom.UtilityStage
import ziphil.module.Setting


@CompileStatic @Newify
public class SettingController {

  private static final String RESOURCE_PATH = "resource/fxml/setting.fxml"
  private static final String TITLE = "設定"
  private static final Double DEFAULT_WIDTH = -1
  private static final Double DEFAULT_HEIGHT = -1

  @FXML private ComboBox<String> $contentFontFamilies
  @FXML private Spinner $contentFontSize
  @FXML private CheckBox $usesSystemContentFont
  @FXML private ComboBox<String> $editorFontFamilies
  @FXML private Spinner $editorFontSize
  @FXML private CheckBox $usesSystemEditorFont
  @FXML private ToggleButton $modifiesPunctuation
  @FXML private GridPane $registeredDictionaryPane
  @FXML private List<TextField> $registeredDictionaryPaths = ArrayList.new(10)
  @FXML private ToggleButton $savesAutomatically
  @FXML private ToggleButton $ignoresAccent
  @FXML private ToggleButton $ignoresCase
  @FXML private ToggleButton $prefixSearch
  private Stage $stage
  private Scene $scene

  public SettingController(Stage stage) {
    $stage = stage
    loadResource()
  }

  @FXML
  private void initialize() {
    setupRegisteredDictionaryPane()
    setupFontFamilies()
    setupFontDisableBindings()
    setupTextBindings()
    applySettings()
  }

  private void applySettings() {
    Setting setting = Setting.getInstance()
    String contentFontFamily = setting.getContentFontFamily()
    Integer contentFontSize = setting.getContentFontSize()
    String editorFontFamily = setting.getEditorFontFamily()
    Integer editorFontSize = setting.getEditorFontSize()
    Boolean modifiesPunctuation = setting.getModifiesPunctuation()
    Boolean savesAutomatically = setting.getSavesAutomatically()
    Boolean ignoresAccent = setting.getIgnoresAccent()
    Boolean ignoresCase = setting.getIgnoresCase()
    Boolean prefixSearch = setting.getPrefixSearch()
    List<String> registeredDictionaryPaths = setting.getRegisteredDictionaryPaths()
    if (contentFontFamily != null) {
      $contentFontFamilies.getSelectionModel().select(contentFontFamily)
    } else {
      $usesSystemContentFont.setSelected(true)
    }
    if (contentFontSize != null) {
      $contentFontSize.getValueFactory().setValue(contentFontSize)
    }
    if (editorFontFamily != null) {
      $editorFontFamilies.getSelectionModel().select(editorFontFamily)
    } else {
      $usesSystemEditorFont.setSelected(true)
    }
    if (editorFontSize != null) {
      $editorFontSize.getValueFactory().setValue(editorFontSize)
    }
    if (modifiesPunctuation) {
      $modifiesPunctuation.setSelected(true)
    }
    if (savesAutomatically) {
      $savesAutomatically.setSelected(true)
    }
    if (ignoresAccent) {
      $ignoresAccent.setSelected(true)
    }
    if (ignoresCase) {
      $ignoresCase.setSelected(true)
    }
    if (prefixSearch) {
      $prefixSearch.setSelected(true)
    }
    (0 ..< 10).each() { Integer i ->
      $registeredDictionaryPaths[i].setText(registeredDictionaryPaths[i])
    }
  }

  private void saveSettings() {
    Setting setting = Setting.getInstance()
    String contentFontFamily = $contentFontFamilies.getSelectionModel().getSelectedItem()
    Integer contentFontSize = $contentFontSize.getValue()
    Boolean usesSystemContentFont = $usesSystemContentFont.isSelected()
    String editorFontFamily = $editorFontFamilies.getSelectionModel().getSelectedItem()
    Integer editorFontSize = $editorFontSize.getValue()
    Boolean usesSystemEditorFont = $usesSystemEditorFont.isSelected()
    Boolean modifiesPunctuation = $modifiesPunctuation.isSelected()
    Boolean savesAutomatically = $savesAutomatically.isSelected()
    Boolean ignoresAccent = $ignoresAccent.isSelected()
    Boolean ignoresCase = $ignoresCase.isSelected()
    Boolean prefixSearch = $prefixSearch.isSelected()
    List<String> registeredDictionaryPaths = $registeredDictionaryPaths.collect{path -> path.getText()}
    if (!usesSystemContentFont && contentFontFamily != null) {
      setting.setContentFontFamily(contentFontFamily)
      setting.setContentFontSize(contentFontSize)
    } else {
      setting.setContentFontFamily(null)
      setting.setContentFontSize(null)
    }
    if (!usesSystemEditorFont && editorFontFamily != null) {
      setting.setEditorFontFamily(editorFontFamily)
      setting.setEditorFontSize(editorFontSize)
    } else {
      setting.setEditorFontFamily(null)
      setting.setEditorFontSize(null)
    }
    setting.setModifiesPunctuation(modifiesPunctuation)
    setting.setSavesAutomatically(savesAutomatically)
    setting.setIgnoresAccent(ignoresAccent)
    setting.setIgnoresCase(ignoresCase)
    setting.setPrefixSearch(prefixSearch)
    (0 ..< 10).each() { Integer i ->
      String path = registeredDictionaryPaths[i]
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

  private void deregisterDictionary(Integer i) {
    $registeredDictionaryPaths[i].setText("")
  }

  @FXML
  private void commitChange() {
    saveSettings()
    $stage.close()
  }

  @FXML
  private void cancelChange() {
    $stage.close()
  }

  private void setupRegisteredDictionaryPane() {
    (0 ..< 10).each() { Integer i ->
      Label number = Label.new("登録辞書${(i + 1) % 10}:")
      HBox box = HBox.new(Measurement.rpx(5))
      HBox innerBox = HBox.new()
      TextField dictionaryPath = TextField.new()
      Button browse = Button.new("…")
      Button deregister = Button.new("解除")
      dictionaryPath.setPrefWidth(Measurement.rpx(400))
      dictionaryPath.setMinWidth(Measurement.rpx(400))
      dictionaryPath.getStyleClass().add("left-pill")
      browse.getStyleClass().add("right-pill")
      deregister.setPrefWidth(Measurement.rpx(70))
      deregister.setPrefWidth(Measurement.rpx(70))
      browse.setOnAction() {
        browseDictionary(i)
      }
      deregister.setOnAction() {
        deregisterDictionary(i)
      }
      innerBox.getChildren().addAll(dictionaryPath, browse)
      box.getChildren().addAll(innerBox, deregister)
      $registeredDictionaryPaths[i] = dictionaryPath
      $registeredDictionaryPane.add(number, 0, i)
      $registeredDictionaryPane.add(box, 1, i)
    }
  }

  private void setupFontFamilies() {
    List<String> fontFamilies = Font.getFamilies()
    $contentFontFamilies.getItems().addAll(fontFamilies)
    $editorFontFamilies.getItems().addAll(fontFamilies)
  }

  private void setupFontDisableBindings() {
    $contentFontFamilies.disableProperty().bind($usesSystemContentFont.selectedProperty())
    $contentFontSize.disableProperty().bind($usesSystemContentFont.selectedProperty())
    $editorFontFamilies.disableProperty().bind($usesSystemEditorFont.selectedProperty())
    $editorFontSize.disableProperty().bind($usesSystemEditorFont.selectedProperty())
  }

  private void setupTextBindings() {
    Callable<String> modifiesPunctuationFunction = (Callable){
      return ($modifiesPunctuation.selectedProperty().get()) ? "有効" : "無効"
    }
    Callable<String> savesAutomaticallyFunction = (Callable){
      return ($savesAutomatically.selectedProperty().get()) ? "有効" : "無効"
    }
    Callable<String> ignoresAccentFunction = (Callable){
      return ($ignoresAccent.selectedProperty().get()) ? "有効" : "無効"
    }
    Callable<String> ignoresCaseFunction = (Callable){
      return ($ignoresCase.selectedProperty().get()) ? "有効" : "無効"
    }
    Callable<String> prefixSearchFunction = (Callable){
      return ($prefixSearch.selectedProperty().get()) ? "有効" : "無効"
    }
    StringBinding modifiesPunctuationBinding = Bindings.createStringBinding(modifiesPunctuationFunction, $modifiesPunctuation.selectedProperty())
    StringBinding savesAutomaticallyBinding = Bindings.createStringBinding(savesAutomaticallyFunction, $savesAutomatically.selectedProperty())
    StringBinding ignoresAccentBinding = Bindings.createStringBinding(ignoresAccentFunction, $ignoresAccent.selectedProperty())
    StringBinding ignoresCaseBinding = Bindings.createStringBinding(ignoresCaseFunction, $ignoresCase.selectedProperty())
    StringBinding prefixSearchBinding = Bindings.createStringBinding(prefixSearchFunction, $prefixSearch.selectedProperty())
    $modifiesPunctuation.textProperty().bind(modifiesPunctuationBinding)
    $savesAutomatically.textProperty().bind(savesAutomaticallyBinding)
    $ignoresAccent.textProperty().bind(ignoresAccentBinding)
    $ignoresCase.textProperty().bind(ignoresCaseBinding)
    $prefixSearch.textProperty().bind(prefixSearchBinding)
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