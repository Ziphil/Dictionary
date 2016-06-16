package ziphil.controller

import groovy.transform.CompileStatic
import java.util.concurrent.Callable
import javafx.beans.binding.Bindings
import javafx.beans.binding.StringBinding
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.control.ComboBox
import javafx.scene.control.CheckBox
import javafx.scene.control.Spinner
import javafx.scene.control.ToggleButton
import javafx.scene.text.Font
import javafx.stage.Stage
import ziphil.module.Setting


@CompileStatic @Newify
public class SettingController {

  private static final String RESOURCE_PATH = "resource/fxml/setting.fxml"
  private static final String TITLE = "設定"
  private static final Integer DEFAULT_WIDTH = -1
  private static final Integer DEFAULT_HEIGHT = -1

  @FXML private ComboBox<String> $contentFontNames
  @FXML private Spinner $contentFontSize
  @FXML private CheckBox $usesSystemContentFont
  @FXML private ComboBox<String> $editorFontNames
  @FXML private Spinner $editorFontSize
  @FXML private CheckBox $usesSystemEditorFont
  @FXML private ToggleButton $modifiesPunctuation
  @FXML private ToggleButton $savesAutomatically
  private Stage $stage
  private Scene $scene

  public SettingController(Stage stage) {
    $stage = stage
    loadResource()
  }

  @FXML
  private void initialize() {
    setupContentFontNames()
    setupTextBindings()
    applySetting()
  }

  private void applySetting() {
    Setting setting = Setting.getInstance()
    String contentFontFamily = setting.getContentFontFamily()
    Integer contentFontSize = setting.getContentFontSize()
    Boolean modifiesPunctuation = setting.modifiesPunctuation()
    Boolean savesAutomatically = setting.savesAutomatically()
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
    setting.save()
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

  private void setupContentFontNames() {
    List<String> fontNames = Font.getFontNames()
    $contentFontNames.getItems().addAll(fontNames)
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
    FXMLLoader loader = FXMLLoader.new(getClass().getClassLoader().getResource(RESOURCE_PATH))
    loader.setController(this)
    Parent root = (Parent)loader.load()
    $scene = Scene.new(root, DEFAULT_WIDTH, DEFAULT_HEIGHT)
    $stage.setScene($scene)
    $stage.setTitle(TITLE)
    $stage.setResizable(false)
    $stage.sizeToScene()
  }

}