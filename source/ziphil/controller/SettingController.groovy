package ziphil.controller

import groovy.transform.CompileStatic
import javafx.fxml.FXML
import javafx.scene.control.Button
import javafx.scene.control.ComboBox
import javafx.scene.control.CheckBox
import javafx.scene.control.Label
import javafx.scene.control.Spinner
import javafx.scene.control.TextField
import javafx.scene.control.TextFormatter
import javafx.scene.control.ToggleButton
import javafx.scene.layout.GridPane
import javafx.scene.layout.HBox
import javafx.scene.layout.Priority
import javafx.scene.text.Font
import javafx.stage.StageStyle
import javafx.stage.Modality
import ziphil.custom.IntegerUnaryOperator
import ziphil.custom.Measurement
import ziphil.custom.UtilityStage
import ziphil.module.CustomBindings
import ziphil.module.Setting
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class SettingController extends Controller<Boolean> {

  private static final String RESOURCE_PATH = "resource/fxml/setting.fxml"
  private static final String TITLE = "環境設定"
  private static final Double DEFAULT_WIDTH = Measurement.rpx(640)
  private static final Double DEFAULT_HEIGHT = -1

  @FXML private ComboBox<String> $contentFontFamilyControl
  @FXML private Spinner<Integer> $contentFontSizeControl
  @FXML private CheckBox $usesSystemContentFontFamilyControl
  @FXML private CheckBox $usesDefaultContentFontSizeControl
  @FXML private ComboBox<String> $editorFontFamilyControl
  @FXML private Spinner<Integer> $editorFontSizeControl
  @FXML private CheckBox $usesSystemEditorFontFamilyControl
  @FXML private CheckBox $usesDefaultEditorFontSizeControl
  @FXML private ToggleButton $modifiesPunctuationControl
  @FXML private GridPane $registeredDictionaryPane
  @FXML private List<TextField> $registeredDictionaryPathControls = ArrayList.new(10)
  @FXML private ToggleButton $savesAutomaticallyControl
  @FXML private ToggleButton $ignoresAccentControl
  @FXML private ToggleButton $ignoresCaseControl
  @FXML private ToggleButton $searchesPrefixControl
  @FXML private ToggleButton $ignoresDuplicateSlimeIdControl
  @FXML private ToggleButton $showsSlimeIdControl

  public SettingController(UtilityStage<Boolean> nextStage) {
    super(nextStage)
    loadResource(RESOURCE_PATH, TITLE, DEFAULT_WIDTH, DEFAULT_HEIGHT, false)
  }

  @FXML
  private void initialize() {
    setupRegisteredDictionaryPane()
    setupFontFamilyControls()
    setupTextFormatters()
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
    Boolean searchesPrefix = setting.getSearchesPrefix()
    Boolean ignoresDuplicateSlimeId = setting.getIgnoresDuplicateSlimeId()
    Boolean showsSlimeId = setting.getShowsSlimeId()
    List<String> registeredDictionaryPaths = setting.getRegisteredDictionaryPaths()
    if (contentFontFamily != null) {
      $contentFontFamilyControl.getSelectionModel().select(contentFontFamily)
    } else {
      $usesSystemContentFontFamilyControl.setSelected(true)
    }
    if (contentFontSize != null) {
      $contentFontSizeControl.getValueFactory().setValue(contentFontSize)
    } else {
      $usesDefaultContentFontSizeControl.setSelected(true)
    }
    if (editorFontFamily != null) {
      $editorFontFamilyControl.getSelectionModel().select(editorFontFamily)
    } else {
      $usesSystemEditorFontFamilyControl.setSelected(true)
    }
    if (editorFontSize != null) {
      $editorFontSizeControl.getValueFactory().setValue(editorFontSize)
    } else {
      $usesDefaultEditorFontSizeControl.setSelected(true)
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
    if (searchesPrefix) {
      $searchesPrefixControl.setSelected(true)
    }
    if (ignoresDuplicateSlimeId) {
      $ignoresDuplicateSlimeIdControl.setSelected(true)
    }
    if (showsSlimeId) {
      $showsSlimeIdControl.setSelected(true)
    }
    for (Integer i : 0 ..< 10) {
      $registeredDictionaryPathControls[i].setText(registeredDictionaryPaths[i])
    }
  }

  private void updateSettings() {
    Setting setting = Setting.getInstance()
    Boolean usesSystemContentFontFamily = $usesSystemContentFontFamilyControl.isSelected()
    Boolean usesDefaultContentFontSize = $usesDefaultContentFontSizeControl.isSelected()
    String contentFontFamily = (usesSystemContentFontFamily) ? null : $contentFontFamilyControl.getSelectionModel().getSelectedItem()
    Integer contentFontSize = (usesDefaultContentFontSize) ? null : $contentFontSizeControl.getValue()
    Boolean usesSystemEditorFontFamily = $usesSystemEditorFontFamilyControl.isSelected()
    Boolean usesDefaultEditorFontSize = $usesDefaultEditorFontSizeControl.isSelected()
    String editorFontFamily = (usesSystemEditorFontFamily) ? null : $editorFontFamilyControl.getSelectionModel().getSelectedItem()
    Integer editorFontSize = (usesDefaultEditorFontSize) ? null : $editorFontSizeControl.getValue()
    Boolean modifiesPunctuation = $modifiesPunctuationControl.isSelected()
    Boolean savesAutomatically = $savesAutomaticallyControl.isSelected()
    Boolean ignoresAccent = $ignoresAccentControl.isSelected()
    Boolean ignoresCase = $ignoresCaseControl.isSelected()
    Boolean searchesPrefix = $searchesPrefixControl.isSelected()
    Boolean ignoresDuplicateSlimeId = $ignoresDuplicateSlimeIdControl.isSelected()
    Boolean showsSlimeId = $showsSlimeIdControl.isSelected()
    List<String> registeredDictionaryPaths = $registeredDictionaryPathControls.collect{path -> path.getText()}
    setting.setContentFontFamily(contentFontFamily)
    setting.setContentFontSize(contentFontSize)
    setting.setEditorFontFamily(editorFontFamily)
    setting.setEditorFontSize(editorFontSize)
    setting.setModifiesPunctuation(modifiesPunctuation)
    setting.setSavesAutomatically(savesAutomatically)
    setting.setIgnoresAccent(ignoresAccent)
    setting.setIgnoresCase(ignoresCase)
    setting.setSearchesPrefix(searchesPrefix)
    setting.setIgnoresDuplicateSlimeId(ignoresDuplicateSlimeId)
    setting.setShowsSlimeId(showsSlimeId)
    for (Integer i : 0 ..< 10) {
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
    for (Integer i : 0 ..< 10) {
      Integer j = i
      Label numberLabel = Label.new("登録辞書${(i + 1) % 10}:")
      HBox box = HBox.new(Measurement.rpx(5))
      HBox innerBox = HBox.new()
      TextField dictionaryPathControl = TextField.new()
      Button browseButton = Button.new("…")
      Button deregisterButton = Button.new("解除")
      dictionaryPathControl.getStyleClass().add("left-pill")
      browseButton.getStyleClass().add("right-pill")
      deregisterButton.setPrefWidth(Measurement.rpx(70))
      deregisterButton.setMinWidth(Measurement.rpx(70))
      browseButton.setOnAction() {
        browseDictionary(j)
      }
      deregisterButton.setOnAction() {
        deregisterDictionary(j)
      }
      innerBox.getChildren().addAll(dictionaryPathControl, browseButton)
      innerBox.setHgrow(dictionaryPathControl, Priority.ALWAYS)
      box.getChildren().addAll(innerBox, deregisterButton)
      box.setHgrow(innerBox, Priority.ALWAYS)
      $registeredDictionaryPathControls[i] = dictionaryPathControl
      $registeredDictionaryPane.add(numberLabel, 0, i)
      $registeredDictionaryPane.add(box, 1, i)
    }
  }

  private void setupFontFamilyControls() {
    List<String> fontFamilies = Font.getFamilies()
    $contentFontFamilyControl.getItems().addAll(fontFamilies)
    $editorFontFamilyControl.getItems().addAll(fontFamilies)
  }

  private void setupTextFormatters() {
    $contentFontSizeControl.getEditor().setTextFormatter(TextFormatter.new(IntegerUnaryOperator.new()))
    $editorFontSizeControl.getEditor().setTextFormatter(TextFormatter.new(IntegerUnaryOperator.new()))
  }

  private void setupFontDisableBindings() {
    $contentFontFamilyControl.disableProperty().bind($usesSystemContentFontFamilyControl.selectedProperty())
    $contentFontSizeControl.disableProperty().bind($usesDefaultContentFontSizeControl.selectedProperty())
    $editorFontFamilyControl.disableProperty().bind($usesSystemEditorFontFamilyControl.selectedProperty())
    $editorFontSizeControl.disableProperty().bind($usesDefaultEditorFontSizeControl.selectedProperty())
  }

  private void setupTextBindings() {
    $modifiesPunctuationControl.textProperty().bind(CustomBindings.whichString($modifiesPunctuationControl, "有効", "無効"))
    $savesAutomaticallyControl.textProperty().bind(CustomBindings.whichString($savesAutomaticallyControl, "有効", "無効"))
    $ignoresAccentControl.textProperty().bind(CustomBindings.whichString($ignoresAccentControl, "有効", "無効"))
    $ignoresCaseControl.textProperty().bind(CustomBindings.whichString($ignoresCaseControl, "有効", "無効"))
    $searchesPrefixControl.textProperty().bind(CustomBindings.whichString($searchesPrefixControl, "有効", "無効"))
    $ignoresDuplicateSlimeIdControl.textProperty().bind(CustomBindings.whichString($ignoresDuplicateSlimeIdControl, "有効", "無効"))
    $showsSlimeIdControl.textProperty().bind(CustomBindings.whichString($showsSlimeIdControl, "有効", "無効"))
  }

}