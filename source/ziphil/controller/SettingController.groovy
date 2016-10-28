package ziphil.controller

import groovy.transform.CompileStatic
import javafx.fxml.FXML
import javafx.scene.control.Button
import javafx.scene.control.ComboBox
import javafx.scene.control.CheckBox
import javafx.scene.control.Label
import javafx.scene.control.Spinner
import javafx.scene.control.TextField
import javafx.scene.control.ToggleButton
import javafx.scene.layout.GridPane
import javafx.scene.layout.HBox
import javafx.scene.layout.Priority
import javafx.scene.text.Font
import javafx.stage.StageStyle
import javafx.stage.Modality
import ziphil.custom.Measurement
import ziphil.custom.UtilityStage
import ziphil.module.CustomBindings
import ziphil.module.Setting


@CompileStatic @Newify
public class SettingController extends Controller<Boolean> {

  private static final String RESOURCE_PATH = "resource/fxml/setting.fxml"
  private static final String TITLE = "環境設定"
  private static final Double DEFAULT_WIDTH = Measurement.rpx(640)
  private static final Double DEFAULT_HEIGHT = -1

  @FXML private ComboBox<String> $contentFontFamilyControl
  @FXML private Spinner $contentFontSizeControl
  @FXML private CheckBox $usesSystemContentFontControl
  @FXML private ComboBox<String> $editorFontFamilyControl
  @FXML private Spinner $editorFontSizeControl
  @FXML private CheckBox $usesSystemEditorFontControl
  @FXML private ToggleButton $modifiesPunctuationControl
  @FXML private GridPane $registeredDictionaryPane
  @FXML private List<TextField> $registeredDictionaryPathControls = ArrayList.new(10)
  @FXML private ToggleButton $savesAutomaticallyControl
  @FXML private ToggleButton $ignoresAccentControl
  @FXML private ToggleButton $ignoresCaseControl
  @FXML private ToggleButton $prefixSearchControl
  @FXML private ToggleButton $ignoresDuplicateSlimeIdControl
  @FXML private ToggleButton $showsSlimeIdControl

  public SettingController(UtilityStage<Boolean> nextStage) {
    super(nextStage)
    loadResource(RESOURCE_PATH, TITLE, DEFAULT_WIDTH, DEFAULT_HEIGHT, false)
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
    Boolean ignoresDuplicateSlimeId = setting.getIgnoresDuplicateSlimeId()
    Boolean showsSlimeId = setting.getShowsSlimeId()
    List<String> registeredDictionaryPaths = setting.getRegisteredDictionaryPaths()
    if (contentFontFamily != null) {
      $contentFontFamilyControl.getSelectionModel().select(contentFontFamily)
    } else {
      $usesSystemContentFontControl.setSelected(true)
    }
    if (contentFontSize != null) {
      $contentFontSizeControl.getValueFactory().setValue(contentFontSize)
    }
    if (editorFontFamily != null) {
      $editorFontFamilyControl.getSelectionModel().select(editorFontFamily)
    } else {
      $usesSystemEditorFontControl.setSelected(true)
    }
    if (editorFontSize != null) {
      $editorFontSizeControl.getValueFactory().setValue(editorFontSize)
    }
    if (modifiesPunctuation) {
      $modifiesPunctuationControl.setSelected(true)
    }
    if (savesAutomatically) {
      $savesAutomaticallyControl.setSelected(true)
    }
    if (ignoresAccent) {
      $ignoresAccentControl.setSelected(true)
    }
    if (ignoresCase) {
      $ignoresCaseControl.setSelected(true)
    }
    if (prefixSearch) {
      $prefixSearchControl.setSelected(true)
    }
    if (ignoresDuplicateSlimeId) {
      $ignoresDuplicateSlimeIdControl.setSelected(true)
    }
    if (showsSlimeId) {
      $showsSlimeIdControl.setSelected(true)
    }
    (0 ..< 10).each() { Integer i ->
      $registeredDictionaryPathControls[i].setText(registeredDictionaryPaths[i])
    }
  }

  private void updateSettings() {
    Setting setting = Setting.getInstance()
    String contentFontFamily = $contentFontFamilyControl.getSelectionModel().getSelectedItem()
    Integer contentFontSize = $contentFontSizeControl.getValue()
    Boolean usesSystemContentFont = $usesSystemContentFontControl.isSelected()
    String editorFontFamily = $editorFontFamilyControl.getSelectionModel().getSelectedItem()
    Integer editorFontSize = $editorFontSizeControl.getValue()
    Boolean usesSystemEditorFont = $usesSystemEditorFontControl.isSelected()
    Boolean modifiesPunctuation = $modifiesPunctuationControl.isSelected()
    Boolean savesAutomatically = $savesAutomaticallyControl.isSelected()
    Boolean ignoresAccent = $ignoresAccentControl.isSelected()
    Boolean ignoresCase = $ignoresCaseControl.isSelected()
    Boolean prefixSearch = $prefixSearchControl.isSelected()
    Boolean ignoresDuplicateSlimeId = $ignoresDuplicateSlimeIdControl.isSelected()
    Boolean showsSlimeId = $showsSlimeIdControl.isSelected()
    List<String> registeredDictionaryPaths = $registeredDictionaryPathControls.collect{path -> path.getText()}
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
    setting.setIgnoresDuplicateSlimeId(ignoresDuplicateSlimeId)
    setting.setShowsSlimeId(showsSlimeId)
    (0 ..< 10).each() { Integer i ->
      String path = registeredDictionaryPaths[i]
      setting.getRegisteredDictionaryPaths()[i] = (path != "") ? path : null
    }
  }

  private void browseDictionary(Integer i) {
    UtilityStage<File> nextStage = UtilityStage.new(StageStyle.UTILITY)
    DictionaryChooserController controller = DictionaryChooserController.new(nextStage)
    nextStage.initModality(Modality.WINDOW_MODAL)
    nextStage.initOwner($stage)
    File file = nextStage.showAndWaitResult()
    if (file != null) {
      $registeredDictionaryPathControls[i].setText(file.getAbsolutePath())
    }
  }

  private void deregisterDictionary(Integer i) {
    $registeredDictionaryPathControls[i].setText("")
  }

  @FXML
  protected void commit() {
    updateSettings()
    $stage.close(true)
  }

  private void setupRegisteredDictionaryPane() {
    (0 ..< 10).each() { Integer i ->
      Label number = Label.new("登録辞書${(i + 1) % 10}:")
      HBox box = HBox.new(Measurement.rpx(5))
      HBox innerBox = HBox.new()
      TextField dictionaryPath = TextField.new()
      Button browse = Button.new("…")
      Button deregister = Button.new("解除")
      dictionaryPath.getStyleClass().add("left-pill")
      browse.getStyleClass().add("right-pill")
      deregister.setPrefWidth(Measurement.rpx(70))
      deregister.setMinWidth(Measurement.rpx(70))
      browse.setOnAction() {
        browseDictionary(i)
      }
      deregister.setOnAction() {
        deregisterDictionary(i)
      }
      innerBox.getChildren().addAll(dictionaryPath, browse)
      innerBox.setHgrow(dictionaryPath, Priority.ALWAYS)
      box.getChildren().addAll(innerBox, deregister)
      box.setHgrow(innerBox, Priority.ALWAYS)
      $registeredDictionaryPathControls[i] = dictionaryPath
      $registeredDictionaryPane.add(number, 0, i)
      $registeredDictionaryPane.add(box, 1, i)
    }
  }

  private void setupFontFamilies() {
    List<String> fontFamilies = Font.getFamilies()
    $contentFontFamilyControl.getItems().addAll(fontFamilies)
    $editorFontFamilyControl.getItems().addAll(fontFamilies)
  }

  private void setupFontDisableBindings() {
    $contentFontFamilyControl.disableProperty().bind($usesSystemContentFontControl.selectedProperty())
    $contentFontSizeControl.disableProperty().bind($usesSystemContentFontControl.selectedProperty())
    $editorFontFamilyControl.disableProperty().bind($usesSystemEditorFontControl.selectedProperty())
    $editorFontSizeControl.disableProperty().bind($usesSystemEditorFontControl.selectedProperty())
  }

  private void setupTextBindings() {
    $modifiesPunctuationControl.textProperty().bind(CustomBindings.whichString($modifiesPunctuationControl, "有効", "無効"))
    $savesAutomaticallyControl.textProperty().bind(CustomBindings.whichString($savesAutomaticallyControl, "有効", "無効"))
    $ignoresAccentControl.textProperty().bind(CustomBindings.whichString($ignoresAccentControl, "有効", "無効"))
    $ignoresCaseControl.textProperty().bind(CustomBindings.whichString($ignoresCaseControl, "有効", "無効"))
    $prefixSearchControl.textProperty().bind(CustomBindings.whichString($prefixSearchControl, "有効", "無効"))
    $ignoresDuplicateSlimeIdControl.textProperty().bind(CustomBindings.whichString($ignoresDuplicateSlimeIdControl, "有効", "無効"))
    $showsSlimeIdControl.textProperty().bind(CustomBindings.whichString($showsSlimeIdControl, "有効", "無効"))
  }

}