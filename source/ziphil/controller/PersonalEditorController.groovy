package ziphil.controller

import groovy.transform.CompileStatic
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.control.TextArea
import javafx.scene.control.TextField
import javafx.scene.control.Spinner
import ziphil.custom.CustomBuilderFactory
import ziphil.custom.Measurement
import ziphil.custom.UtilityStage
import ziphil.dictionary.PersonalWord
import ziphil.module.Setting


@CompileStatic @Newify
public class PersonalEditorController {

  private static final String RESOURCE_PATH = "resource/fxml/personal_editor.fxml"
  private static final String TITLE = "単語編集"
  private static final Double DEFAULT_WIDTH = Measurement.rpx(640)
  private static final Double DEFAULT_HEIGHT = Measurement.rpx(480)

  @FXML private TextField $name
  @FXML private TextField $pronunciation
  @FXML private TextArea $translation
  @FXML private TextArea $usage
  @FXML private Spinner $level
  @FXML private Spinner $memory
  @FXML private Spinner $modification
  private PersonalWord $word
  private UtilityStage<Boolean> $stage
  private Scene $scene

  public PersonalEditorController(UtilityStage<Boolean> stage) {
    $stage = stage
    loadResource()
  }

  public void prepare(PersonalWord word) {
    $word = word
    $name.setText(word.getName())
    $pronunciation.setText(word.getPronunciation())
    $translation.setText(word.getTranslation())
    $usage.setText(word.getUsage())
    $level.getValueFactory().setValue(word.getLevel())
    $memory.getValueFactory().setValue(word.getMemory())
    $modification.getValueFactory().setValue(word.getModification())
    $translation.requestFocus()
  }

  @FXML
  private void commitEdit() {
    String name = $name.getText()
    String pronunciation = $pronunciation.getText()
    String translation = $translation.getText()
    String usage = $usage.getText()
    Integer level = $level.getValue()
    Integer memory = $memory.getValue()
    Integer modification = $modification.getValue()
    $word.update(name, pronunciation, translation, usage, level, memory, modification)
    $stage.close(true)
  }

  @FXML
  private void cancelEdit() {
    $stage.close(false)
  }

  private void loadResource() {
    FXMLLoader loader = FXMLLoader.new(getClass().getClassLoader().getResource(RESOURCE_PATH), null, CustomBuilderFactory.new())
    loader.setController(this)
    Parent root = (Parent)loader.load()
    $scene = Scene.new(root, DEFAULT_WIDTH, DEFAULT_HEIGHT)
    $stage.setScene($scene)
    $stage.setTitle(TITLE)
    $stage.sizeToScene()
  }

}