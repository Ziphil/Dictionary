package ziphil.controller

import groovy.transform.CompileStatic
import javafx.fxml.FXML
import javafx.scene.control.Button
import javafx.scene.control.ComboBox
import javafx.scene.control.CheckBox
import javafx.scene.control.Label
import javafx.scene.control.ListView
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
import javax.script.ScriptEngineFactory
import javax.script.ScriptEngineManager
import ziphil.custom.IntegerUnaryOperator
import ziphil.custom.Measurement
import ziphil.custom.ScriptEngineFactoryCell
import ziphil.custom.UtilityStage
import ziphil.module.CustomBindings
import ziphil.module.FontRenderingType
import ziphil.module.Setting
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class SettingController extends Controller<Boolean> {

  private static final String RESOURCE_PATH = "resource/fxml/controller/setting.fxml"
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
  @FXML private ComboBox<String> $systemFontFamilyControl
  @FXML private CheckBox $usesDefaultSystemFontFamilyControl
  @FXML private Spinner<Integer> $lineSpacingControl
  @FXML private Spinner<Integer> $separativeIntervalControl
  @FXML private ComboBox<ScriptEngineFactory> $scriptControl
  @FXML private ComboBox<FontRenderingType> $fontRenderingTypeControl
  @FXML private ToggleButton $modifiesPunctuationControl
  @FXML private ToggleButton $keepsMainOnTopControl
  @FXML private ToggleButton $keepsEditorOnTopControl
  @FXML private GridPane $registeredDictionaryPane
  @FXML private List<TextField> $registeredDictionaryPathControls = ArrayList.new(10)
  @FXML private List<TextField> $registeredDictionaryNameControls = ArrayList.new(10)
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
    setupScriptControl()
    setupRegisteredDictionaryPane()
    setupFontFamilyControls()
    setupIntegerControls()
    bindFontControlProperties()
    bindOtherProperties()
    applySettings()
  }

  private void applySettings() {
    Setting setting = Setting.getInstance()
    String contentFontFamily = setting.getContentFontFamily()
    Integer contentFontSize = setting.getContentFontSize()
    String editorFontFamily = setting.getEditorFontFamily()
    Integer editorFontSize = setting.getEditorFontSize()
    String systemFontFamily = setting.getSystemFontFamily()
    Integer lineSpacing = setting.getLineSpacing()
    Integer separativeInterval = setting.getSeparativeInterval()
    String scriptName = setting.getScriptName()
    FontRenderingType fontRenderingType = setting.getFontRenderingType()
    Boolean modifiesPunctuation = setting.getModifiesPunctuation() == true
    Boolean keepsMainOnTop = setting.getKeepsMainOnTop() == true
    Boolean keepsEditorOnTop = setting.getKeepsEditorOnTop() == true
    Boolean savesAutomatically = setting.getSavesAutomatically() == true
    Boolean ignoresAccent = setting.getIgnoresAccent() == true
    Boolean ignoresCase = setting.getIgnoresCase() == true
    Boolean searchesPrefix = setting.getSearchesPrefix() == true
    Boolean ignoresDuplicateSlimeId = setting.getIgnoresDuplicateSlimeId() == true
    Boolean showsSlimeId = setting.getShowsSlimeId() == true
    List<String> registeredDictionaryPaths = setting.getRegisteredDictionaryPaths()
    List<String> registeredDictionaryNames = setting.getRegisteredDictionaryNames()
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
    if (systemFontFamily != null) {
      $systemFontFamilyControl.getSelectionModel().select(systemFontFamily)
    } else {
      $usesDefaultSystemFontFamilyControl.setSelected(true)
    }
    for (ScriptEngineFactory scriptEngineFactory : $scriptControl.getItems()) {
      if (scriptEngineFactory.getNames().contains(scriptName)) {
        $scriptControl.getSelectionModel().select(scriptEngineFactory)
      }
    }
    $lineSpacingControl.getValueFactory().setValue(lineSpacing)
    $separativeIntervalControl.getValueFactory().setValue(separativeInterval)
    $fontRenderingTypeControl.getSelectionModel().select(fontRenderingType)
    $modifiesPunctuationControl.setSelected(modifiesPunctuation)
    $keepsMainOnTopControl.setSelected(keepsMainOnTop)
    $keepsEditorOnTopControl.setSelected(keepsEditorOnTop)
    $savesAutomaticallyControl.setSelected(savesAutomatically)
    $ignoresAccentControl.setSelected(ignoresAccent)
    $ignoresCaseControl.setSelected(ignoresCase)
    $searchesPrefixControl.setSelected(searchesPrefix)
    $ignoresDuplicateSlimeIdControl.setSelected(ignoresDuplicateSlimeId)
    $showsSlimeIdControl.setSelected(showsSlimeId)
    for (Integer i : 0 ..< 10) {
      $registeredDictionaryPathControls[i].setText(registeredDictionaryPaths[i])
      $registeredDictionaryNameControls[i].setText(registeredDictionaryNames[i])
    }
  }

  private void updateSettings() {
    Setting setting = Setting.getInstance()
    Boolean usesSystemContentFontFamily = $usesSystemContentFontFamilyControl.isSelected()
    Boolean usesDefaultContentFontSize = $usesDefaultContentFontSizeControl.isSelected()
    String contentFontFamily = (usesSystemContentFontFamily) ? null : $contentFontFamilyControl.getValue()
    Integer contentFontSize = (usesDefaultContentFontSize) ? null : $contentFontSizeControl.getValue()
    Boolean usesSystemEditorFontFamily = $usesSystemEditorFontFamilyControl.isSelected()
    Boolean usesDefaultEditorFontSize = $usesDefaultEditorFontSizeControl.isSelected()
    String editorFontFamily = (usesSystemEditorFontFamily) ? null : $editorFontFamilyControl.getValue()
    Integer editorFontSize = (usesDefaultEditorFontSize) ? null : $editorFontSizeControl.getValue()
    Boolean usesDefaultSystemFontFamily = $usesDefaultSystemFontFamilyControl.isSelected()
    String systemFontFamily = (usesDefaultSystemFontFamily) ? null : $systemFontFamilyControl.getValue()
    Integer lineSpacing = $lineSpacingControl.getValue()
    Integer separativeInterval = $separativeIntervalControl.getValue()
    String scriptName = $scriptControl.getValue().getNames()[0]
    FontRenderingType fontRenderingType = $fontRenderingTypeControl.getValue()
    Boolean modifiesPunctuation = $modifiesPunctuationControl.isSelected()
    Boolean keepsMainOnTop = $keepsMainOnTopControl.isSelected()
    Boolean keepsEditorOnTop = $keepsEditorOnTopControl.isSelected()
    Boolean savesAutomatically = $savesAutomaticallyControl.isSelected()
    Boolean ignoresAccent = $ignoresAccentControl.isSelected()
    Boolean ignoresCase = $ignoresCaseControl.isSelected()
    Boolean searchesPrefix = $searchesPrefixControl.isSelected()
    Boolean ignoresDuplicateSlimeId = $ignoresDuplicateSlimeIdControl.isSelected()
    Boolean showsSlimeId = $showsSlimeIdControl.isSelected()
    List<String> registeredDictionaryPaths = $registeredDictionaryPathControls.collect{control -> control.getText()}
    List<String> registeredDictionaryNames = $registeredDictionaryNameControls.collect{control -> control.getText()}
    setting.setContentFontFamily(contentFontFamily)
    setting.setContentFontSize(contentFontSize)
    setting.setEditorFontFamily(editorFontFamily)
    setting.setEditorFontSize(editorFontSize)
    setting.setSystemFontFamily(systemFontFamily)
    setting.setLineSpacing(lineSpacing)
    setting.setSeparativeInterval(separativeInterval)
    setting.setScriptName(scriptName)
    setting.setFontRenderingType(fontRenderingType)
    setting.setModifiesPunctuation(modifiesPunctuation)
    setting.setKeepsMainOnTop(keepsMainOnTop)
    setting.setKeepsEditorOnTop(keepsEditorOnTop)
    setting.setSavesAutomatically(savesAutomatically)
    setting.setIgnoresAccent(ignoresAccent)
    setting.setIgnoresCase(ignoresCase)
    setting.setSearchesPrefix(searchesPrefix)
    setting.setIgnoresDuplicateSlimeId(ignoresDuplicateSlimeId)
    setting.setShowsSlimeId(showsSlimeId)
    for (Integer i : 0 ..< 10) {
      String path = registeredDictionaryPaths[i]
      String name = registeredDictionaryNames[i]
      setting.getRegisteredDictionaryPaths()[i] = (path != "") ? path : null
      setting.getRegisteredDictionaryNames()[i] = (name != "") ? name : null
    }
  }

  private void browseDictionary(Integer i) {
    UtilityStage<File> nextStage = UtilityStage.new(StageStyle.UTILITY)
    DictionaryChooserController controller = DictionaryChooserController.new(nextStage)
    nextStage.initModality(Modality.APPLICATION_MODAL)
    nextStage.initOwner($stage)
    String currentPath = $registeredDictionaryPathControls[i].getText()
    if (currentPath != null) {
      controller.prepare(null, File.new(currentPath).getParentFile(), false)
    }
    nextStage.showAndWait()
    if (nextStage.isCommitted()) {
      File file = nextStage.getResult()
      $registeredDictionaryPathControls[i].setText(file.getAbsolutePath())
    }
  }

  private void deregisterDictionary(Integer i) {
    $registeredDictionaryPathControls[i].setText("")
    $registeredDictionaryNameControls[i].setText("")
  }

  @FXML
  protected void commit() {
    updateSettings()
    $stage.commit(true)
  }

  private void setupScriptControl() {
    ScriptEngineManager scriptEngineManager = ScriptEngineManager.new()
    $scriptControl.getItems().addAll(scriptEngineManager.getEngineFactories())
    $scriptControl.setButtonCell(ScriptEngineFactoryCell.new())
    $scriptControl.setCellFactory() { ListView<ScriptEngineFactory> view ->
      return ScriptEngineFactoryCell.new()
    }
  }

  private void setupRegisteredDictionaryPane() {
    for (Integer i : 0 ..< 10) {
      Integer j = i
      Label numberLabel = Label.new("登録辞書${(i + 1) % 10}:")
      HBox box = HBox.new(Measurement.rpx(5))
      HBox innerBox = HBox.new()
      TextField dictionaryPathControl = TextField.new()
      TextField dictionaryNameControl = TextField.new()
      Button browseButton = Button.new("…")
      Button deregisterButton = Button.new("解除")
      dictionaryPathControl.getStyleClass().add("left-pill")
      dictionaryNameControl.setPrefWidth(Measurement.rpx(150))
      dictionaryNameControl.setMinWidth(Measurement.rpx(150))
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
      box.getChildren().addAll(dictionaryNameControl, innerBox, deregisterButton)
      box.setHgrow(innerBox, Priority.ALWAYS)
      $registeredDictionaryPathControls[i] = dictionaryPathControl
      $registeredDictionaryNameControls[i] = dictionaryNameControl
      $registeredDictionaryPane.add(numberLabel, 0, i)
      $registeredDictionaryPane.add(box, 1, i)
    }
  }

  private void setupFontFamilyControls() {
    List<String> fontFamilies = Font.getFamilies()
    $contentFontFamilyControl.getItems().addAll(fontFamilies)
    $editorFontFamilyControl.getItems().addAll(fontFamilies)
    $systemFontFamilyControl.getItems().addAll(fontFamilies)
  }

  private void setupIntegerControls() {
    $contentFontSizeControl.getEditor().setTextFormatter(TextFormatter.new(IntegerUnaryOperator.new()))
    $editorFontSizeControl.getEditor().setTextFormatter(TextFormatter.new(IntegerUnaryOperator.new()))
    $lineSpacingControl.getEditor().setTextFormatter(TextFormatter.new(IntegerUnaryOperator.new()))
  }

  private void bindFontControlProperties() {
    $contentFontFamilyControl.disableProperty().bind($usesSystemContentFontFamilyControl.selectedProperty())
    $contentFontSizeControl.disableProperty().bind($usesDefaultContentFontSizeControl.selectedProperty())
    $editorFontFamilyControl.disableProperty().bind($usesSystemEditorFontFamilyControl.selectedProperty())
    $editorFontSizeControl.disableProperty().bind($usesDefaultEditorFontSizeControl.selectedProperty())
    $systemFontFamilyControl.disableProperty().bind($usesDefaultSystemFontFamilyControl.selectedProperty())
  }

  private void bindOtherProperties() {
    $modifiesPunctuationControl.textProperty().bind(CustomBindings.whichString($modifiesPunctuationControl, "有効", "無効"))
    $keepsMainOnTopControl.textProperty().bind(CustomBindings.whichString($keepsMainOnTopControl, "有効", "無効"))
    $keepsEditorOnTopControl.textProperty().bind(CustomBindings.whichString($keepsEditorOnTopControl, "有効", "無効"))
    $savesAutomaticallyControl.textProperty().bind(CustomBindings.whichString($savesAutomaticallyControl, "有効", "無効"))
    $ignoresAccentControl.textProperty().bind(CustomBindings.whichString($ignoresAccentControl, "有効", "無効"))
    $ignoresCaseControl.textProperty().bind(CustomBindings.whichString($ignoresCaseControl, "有効", "無効"))
    $searchesPrefixControl.textProperty().bind(CustomBindings.whichString($searchesPrefixControl, "有効", "無効"))
    $ignoresDuplicateSlimeIdControl.textProperty().bind(CustomBindings.whichString($ignoresDuplicateSlimeIdControl, "有効", "無効"))
    $showsSlimeIdControl.textProperty().bind(CustomBindings.whichString($showsSlimeIdControl, "有効", "無効"))
  }

}