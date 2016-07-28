package ziphil.controller

import groovy.transform.CompileStatic
import javafx.fxml.FXML
import javafx.scene.control.TextArea
import javafx.scene.control.TextField
import javafx.scene.control.Spinner
import ziphil.custom.Measurement
import ziphil.custom.UtilityStage
import ziphil.dictionary.PersonalWord
import ziphil.module.Setting


@CompileStatic @Newify
public class PersonalEditorController extends Controller<Boolean> {

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

  public PersonalEditorController(UtilityStage<Boolean> stage) {
    super(stage)
    loadResource(RESOURCE_PATH, TITLE, DEFAULT_WIDTH, DEFAULT_HEIGHT)
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
  protected void commit() {
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

}