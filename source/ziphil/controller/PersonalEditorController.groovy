package ziphil.controller

import groovy.transform.CompileStatic
import javafx.fxml.FXML
import javafx.scene.control.CheckBox
import javafx.scene.control.TextArea
import javafx.scene.control.TextField
import javafx.scene.control.TextFormatter
import javafx.scene.control.Spinner
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyCodeCombination
import javafx.scene.input.KeyCombination
import javafx.scene.input.KeyEvent
import ziphil.custom.IntegerUnaryOperator
import ziphil.custom.Measurement
import ziphil.custom.UtilityStage
import ziphil.dictionary.personal.PersonalWord
import ziphil.module.Setting
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class PersonalEditorController extends Controller<BooleanClass> {

  private static final String RESOURCE_PATH = "resource/fxml/controller/personal_editor.fxml"
  private static final String TITLE = "単語編集"
  private static final Double DEFAULT_WIDTH = Measurement.rpx(640)
  private static final Double DEFAULT_HEIGHT = Measurement.rpx(480)

  @FXML private TextField $nameControl
  @FXML private TextField $pronunciationControl
  @FXML private TextArea $translationControl
  @FXML private TextArea $usageControl
  @FXML private Spinner<IntegerClass> $levelControl
  @FXML private CheckBox $memoryControl
  @FXML private CheckBox $modificationControl
  private PersonalWord $word

  public PersonalEditorController(UtilityStage<BooleanClass> stage) {
    super(stage)
    loadResource(RESOURCE_PATH, TITLE, DEFAULT_WIDTH, DEFAULT_HEIGHT)
    setupShortcuts()
  }

  @FXML
  private void initialize() {
    setupLevelControl()
  }

  public void prepare(PersonalWord word, Boolean editsEmptyWord) {
    $word = word
    $nameControl.setText(word.getName())
    $pronunciationControl.setText(word.getPronunciation())
    $translationControl.setText(word.getTranslation())
    $usageControl.setText(word.getUsage())
    $levelControl.getValueFactory().setValue(word.getLevel())
    $memoryControl.setSelected(word.getMemory() == 1)
    $modificationControl.setSelected(word.getModification() == 1)
    if (editsEmptyWord) {
      $nameControl.requestFocus()
    } else {
      $translationControl.requestFocus()
    }
  }

  public void prepare(PersonalWord word) {
    prepare(word, false)
  }

  @FXML
  protected void commit() {
    $word.setName($nameControl.getText())
    $word.setPronunciation($pronunciationControl.getText())
    $word.setTranslation($translationControl.getText())
    $word.setUsage($usageControl.getText())
    $word.setLevel($levelControl.getValue())
    $word.setMemory(($memoryControl.isSelected()) ? 1 : 0)
    $word.setModification(($modificationControl.isSelected()) ? 1 : 0)
    $word.update()
    $stage.commit(true)
  }

  private void setupShortcuts() {
    $scene.addEventHandler(KeyEvent.KEY_PRESSED) { KeyEvent event ->
      if (KeyCodeCombination.new(KeyCode.ENTER, KeyCombination.SHORTCUT_DOWN).match(event)) {
        commit()
      }
    }
  }

  private void setupLevelControl() {
    $levelControl.getEditor().setTextFormatter(TextFormatter.new(IntegerUnaryOperator.new()))
  }

}