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
import javafx.scene.layout.GridPane
import javafx.scene.layout.HBox
import javafx.scene.layout.Priority
import javafx.scene.text.Font
import javafx.stage.StageStyle
import javafx.stage.Modality
import javax.script.ScriptEngineFactory
import javax.script.ScriptEngineManager
import ziphil.custom.ClickType
import ziphil.custom.FontRenderingType
import ziphil.custom.IntegerUnaryOperator
import ziphil.custom.Measurement
import ziphil.custom.ScriptEngineFactoryCell
import ziphil.custom.SwitchButton
import ziphil.custom.UtilityStage
import ziphil.module.CustomBindings
import ziphil.module.Setting
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class SettingController extends Controller<BooleanClass> {

  private static final String RESOURCE_PATH = "resource/fxml/controller/setting.fxml"
  private static final String TITLE = "環境設定"
  private static final Double DEFAULT_WIDTH = Measurement.rpx(580)
  private static final Double DEFAULT_HEIGHT = -1

  @FXML private ComboBox<String> $contentFontFamilyControl
  @FXML private Spinner<IntegerClass> $contentFontSizeControl
  @FXML private CheckBox $usesSystemContentFontFamilyControl
  @FXML private CheckBox $usesDefaultContentFontSizeControl
  @FXML private ComboBox<String> $editorFontFamilyControl
  @FXML private Spinner<IntegerClass> $editorFontSizeControl
  @FXML private CheckBox $usesSystemEditorFontFamilyControl
  @FXML private CheckBox $usesDefaultEditorFontSizeControl
  @FXML private ComboBox<String> $systemFontFamilyControl
  @FXML private CheckBox $usesDefaultSystemFontFamilyControl
  @FXML private Spinner<IntegerClass> $lineSpacingControl
  @FXML private Spinner<IntegerClass> $separativeIntervalControl
  @FXML private ComboBox<ScriptEngineFactory> $scriptControl
  @FXML private ComboBox<FontRenderingType> $fontRenderingTypeControl
  @FXML private ComboBox<ClickType> $linkClickTypeControl
  @FXML private SwitchButton $modifiesPunctuationControl
  @FXML private SwitchButton $keepsMainOnTopControl
  @FXML private SwitchButton $keepsEditorOnTopControl
  @FXML private GridPane $registeredDictionaryPane
  @FXML private List<TextField> $registeredDictionaryPathControls = ArrayList.new(10)
  @FXML private List<TextField> $registeredDictionaryNameControls = ArrayList.new(10)
  @FXML private SwitchButton $savesAutomaticallyControl
  @FXML private SwitchButton $ignoresAccentControl
  @FXML private SwitchButton $ignoresCaseControl
  @FXML private SwitchButton $searchesPrefixControl
  @FXML private SwitchButton $ignoresDuplicateSlimeIdControl
  @FXML private SwitchButton $showsSlimeIdControl
  @FXML private SwitchButton $asksMutualRelationControl
  @FXML private SwitchButton $asksDuplicateNameControl
  @FXML private SwitchButton $persistsPanesControl

  public SettingController(UtilityStage<BooleanClass> nextStage) {
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
    applySettings()
  }

  private void applySettings() {
    Setting setting = Setting.getInstance()
    String contentFontFamily = setting.getContentFontFamily()
    Int contentFontSize = setting.getContentFontSize()
    String editorFontFamily = setting.getEditorFontFamily()
    Int editorFontSize = setting.getEditorFontSize()
    String systemFontFamily = setting.getSystemFontFamily()
    Int lineSpacing = setting.getLineSpacing()
    Int separativeInterval = setting.getSeparativeInterval()
    String scriptName = setting.getScriptName()
    FontRenderingType fontRenderingType = setting.getFontRenderingType()
    ClickType linkClickType = setting.getLinkClickType()
    Boolean modifiesPunctuation = setting.getModifiesPunctuation()
    Boolean keepsMainOnTop = setting.getKeepsMainOnTop()
    Boolean keepsEditorOnTop = setting.getKeepsEditorOnTop()
    Boolean savesAutomatically = setting.getSavesAutomatically()
    Boolean ignoresAccent = setting.getIgnoresAccent()
    Boolean ignoresCase = setting.getIgnoresCase()
    Boolean searchesPrefix = setting.getSearchesPrefix()
    Boolean ignoresDuplicateSlimeId = setting.getIgnoresDuplicateSlimeId()
    Boolean showsSlimeId = setting.getShowsSlimeId()
    Boolean asksMutualRelation = setting.getAsksMutualRelation()
    Boolean asksDuplicateName = setting.getAsksDuplicateName()
    Boolean persistsPanes = setting.getPersistsPanes()
    List<String> registeredDictionaryPaths = setting.getRegisteredDictionaryPaths()
    List<String> registeredDictionaryNames = setting.getRegisteredDictionaryNames()
    if (contentFontFamily != null) {
      $contentFontFamilyControl.getSelectionModel().select(contentFontFamily)
    } else {
      $usesSystemContentFontFamilyControl.setSelected(true)
    }
    if (contentFontSize > 0) {
      $contentFontSizeControl.getValueFactory().setValue(contentFontSize)
    } else {
      $usesDefaultContentFontSizeControl.setSelected(true)
    }
    if (editorFontFamily != null) {
      $editorFontFamilyControl.getSelectionModel().select(editorFontFamily)
    } else {
      $usesSystemEditorFontFamilyControl.setSelected(true)
    }
    if (editorFontSize > 0) {
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
    $linkClickTypeControl.getSelectionModel().select(linkClickType)
    $modifiesPunctuationControl.setSelected(modifiesPunctuation)
    $keepsMainOnTopControl.setSelected(keepsMainOnTop)
    $keepsEditorOnTopControl.setSelected(keepsEditorOnTop)
    $savesAutomaticallyControl.setSelected(savesAutomatically)
    $ignoresAccentControl.setSelected(ignoresAccent)
    $ignoresCaseControl.setSelected(ignoresCase)
    $searchesPrefixControl.setSelected(searchesPrefix)
    $ignoresDuplicateSlimeIdControl.setSelected(ignoresDuplicateSlimeId)
    $showsSlimeIdControl.setSelected(showsSlimeId)
    $asksMutualRelationControl.setSelected(asksMutualRelation)
    $asksDuplicateNameControl.setSelected(asksDuplicateName)
    $persistsPanesControl.setSelected(persistsPanes)
    for (Int i = 0 ; i < 10 ; i ++) {
      $registeredDictionaryPathControls[i].setText(registeredDictionaryPaths[i])
      $registeredDictionaryNameControls[i].setText(registeredDictionaryNames[i])
    }
  }

  private void updateSettings() {
    Setting setting = Setting.getInstance()
    Boolean usesSystemContentFontFamily = $usesSystemContentFontFamilyControl.isSelected()
    Boolean usesDefaultContentFontSize = $usesDefaultContentFontSizeControl.isSelected()
    String contentFontFamily = (usesSystemContentFontFamily) ? null : $contentFontFamilyControl.getValue()
    Int contentFontSize = (usesDefaultContentFontSize) ? -1 : $contentFontSizeControl.getValue()
    Boolean usesSystemEditorFontFamily = $usesSystemEditorFontFamilyControl.isSelected()
    Boolean usesDefaultEditorFontSize = $usesDefaultEditorFontSizeControl.isSelected()
    String editorFontFamily = (usesSystemEditorFontFamily) ? null : $editorFontFamilyControl.getValue()
    Int editorFontSize = (usesDefaultEditorFontSize) ? -1 : $editorFontSizeControl.getValue()
    Boolean usesDefaultSystemFontFamily = $usesDefaultSystemFontFamilyControl.isSelected()
    String systemFontFamily = (usesDefaultSystemFontFamily) ? null : $systemFontFamilyControl.getValue()
    Int lineSpacing = $lineSpacingControl.getValue()
    Int separativeInterval = $separativeIntervalControl.getValue()
    String scriptName = $scriptControl.getValue().getNames()[0]
    FontRenderingType fontRenderingType = $fontRenderingTypeControl.getValue()
    ClickType linkClickType = $linkClickTypeControl.getValue()
    Boolean modifiesPunctuation = $modifiesPunctuationControl.isSelected()
    Boolean keepsMainOnTop = $keepsMainOnTopControl.isSelected()
    Boolean keepsEditorOnTop = $keepsEditorOnTopControl.isSelected()
    Boolean savesAutomatically = $savesAutomaticallyControl.isSelected()
    Boolean ignoresAccent = $ignoresAccentControl.isSelected()
    Boolean ignoresCase = $ignoresCaseControl.isSelected()
    Boolean searchesPrefix = $searchesPrefixControl.isSelected()
    Boolean ignoresDuplicateSlimeId = $ignoresDuplicateSlimeIdControl.isSelected()
    Boolean showsSlimeId = $showsSlimeIdControl.isSelected()
    Boolean asksMutualRelation = $asksMutualRelationControl.isSelected()
    Boolean asksDuplicateName = $asksDuplicateNameControl.isSelected()
    Boolean persistsPanes = $persistsPanesControl.isSelected()
    List<String> registeredDictionaryPaths = $registeredDictionaryPathControls.collect{it.getText()}
    List<String> registeredDictionaryNames = $registeredDictionaryNameControls.collect{it.getText()}
    setting.setContentFontFamily(contentFontFamily)
    setting.setContentFontSize(contentFontSize)
    setting.setEditorFontFamily(editorFontFamily)
    setting.setEditorFontSize(editorFontSize)
    setting.setSystemFontFamily(systemFontFamily)
    setting.setLineSpacing(lineSpacing)
    setting.setSeparativeInterval(separativeInterval)
    setting.setScriptName(scriptName)
    setting.setFontRenderingType(fontRenderingType)
    setting.setLinkClickType(linkClickType)
    setting.setModifiesPunctuation(modifiesPunctuation)
    setting.setKeepsMainOnTop(keepsMainOnTop)
    setting.setKeepsEditorOnTop(keepsEditorOnTop)
    setting.setSavesAutomatically(savesAutomatically)
    setting.setIgnoresAccent(ignoresAccent)
    setting.setIgnoresCase(ignoresCase)
    setting.setSearchesPrefix(searchesPrefix)
    setting.setIgnoresDuplicateSlimeId(ignoresDuplicateSlimeId)
    setting.setShowsSlimeId(showsSlimeId)
    setting.setAsksMutualRelation(asksMutualRelation)
    setting.setAsksDuplicateName(asksDuplicateName)
    setting.setPersistsPanes(persistsPanes)
    for (Int i = 0 ; i < 10 ; i ++) {
      String path = registeredDictionaryPaths[i]
      String name = registeredDictionaryNames[i]
      setting.getRegisteredDictionaryPaths()[i] = (path != "") ? path : null
      setting.getRegisteredDictionaryNames()[i] = (name != "") ? name : null
    }
  }

  private void browseDictionary(Int index) {
    UtilityStage<File> nextStage = UtilityStage.new(StageStyle.UTILITY)
    DictionaryChooserController controller = DictionaryChooserController.new(nextStage)
    nextStage.initModality(Modality.APPLICATION_MODAL)
    nextStage.initOwner($stage)
    String currentPath = $registeredDictionaryPathControls[index].getText()
    if (currentPath != null) {
      controller.prepare(null, File.new(currentPath).getParentFile(), false)
    }
    nextStage.showAndWait()
    if (nextStage.isCommitted()) {
      File file = nextStage.getResult()
      $registeredDictionaryPathControls[index].setText(file.getAbsolutePath())
    }
  }

  private void deregisterDictionary(Int index) {
    $registeredDictionaryPathControls[index].setText("")
    $registeredDictionaryNameControls[index].setText("")
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
    for (Int i = 0 ; i < 10 ; i ++) {
      Int j = i
      Label numberLabel = Label.new("登録辞書${(i + 1) % 10}:")
      HBox box = HBox.new(Measurement.rpx(5))
      HBox innerBox = HBox.new()
      TextField dictionaryPathControl = TextField.new()
      TextField dictionaryNameControl = TextField.new()
      Button browseButton = Button.new("…")
      Button deregisterButton = Button.new("解除")
      dictionaryPathControl.getStyleClass().add("left-pill")
      dictionaryNameControl.setPrefWidth(Measurement.rpx(130))
      dictionaryNameControl.setMinWidth(Measurement.rpx(130))
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

}