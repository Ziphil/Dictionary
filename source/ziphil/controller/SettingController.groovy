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
public class SettingController extends Controller<Void> {

  private static final String RESOURCE_PATH = "resource/fxml/setting.fxml"
  private static final String TITLE = "設定"
  private static final Double DEFAULT_WIDTH = Measurement.rpx(640)
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
  @FXML private ToggleButton $ignoresDuplicateSlimeId
  @FXML private ToggleButton $showsSlimeId

  public SettingController(UtilityStage<Void> stage) {
    super(stage)
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
    if (ignoresDuplicateSlimeId) {
      $ignoresDuplicateSlimeId.setSelected(true)
    }
    if (showsSlimeId) {
      $showsSlimeId.setSelected(true)
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
    Boolean ignoresDuplicateSlimeId = $ignoresDuplicateSlimeId.isSelected()
    Boolean showsSlimeId = $showsSlimeId.isSelected()
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
    setting.setIgnoresDuplicateSlimeId(ignoresDuplicateSlimeId)
    setting.setShowsSlimeId(showsSlimeId)
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
  protected void commit() {
    saveSettings()
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
    $modifiesPunctuation.textProperty().bind(CustomBindings.whichString($modifiesPunctuation, "有効", "無効"))
    $savesAutomatically.textProperty().bind(CustomBindings.whichString($savesAutomatically, "有効", "無効"))
    $ignoresAccent.textProperty().bind(CustomBindings.whichString($ignoresAccent, "有効", "無効"))
    $ignoresCase.textProperty().bind(CustomBindings.whichString($ignoresCase, "有効", "無効"))
    $prefixSearch.textProperty().bind(CustomBindings.whichString($prefixSearch, "有効", "無効"))
    $ignoresDuplicateSlimeId.textProperty().bind(CustomBindings.whichString($ignoresDuplicateSlimeId, "有効", "無効"))
    $showsSlimeId.textProperty().bind(CustomBindings.whichString($showsSlimeId, "有効", "無効"))
  }

}