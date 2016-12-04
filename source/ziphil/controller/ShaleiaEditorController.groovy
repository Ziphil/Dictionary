package ziphil.controller

import groovy.transform.CompileStatic
import javafx.fxml.FXML
import javafx.scene.control.TextArea
import javafx.scene.control.TextField
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyCodeCombination
import javafx.scene.input.KeyCombination
import javafx.scene.input.KeyEvent
import ziphil.custom.Measurement
import ziphil.custom.UtilityStage
import ziphil.dictionary.shaleia.ShaleiaWord
import ziphil.module.Setting
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class ShaleiaEditorController extends Controller<Boolean> {

  private static final String RESOURCE_PATH = "resource/fxml/shaleia_editor.fxml"
  private static final String TITLE = "単語編集"
  private static final Double DEFAULT_WIDTH = Measurement.rpx(640)
  private static final Double DEFAULT_HEIGHT = Measurement.rpx(320)

  @FXML private TextField $nameControl
  @FXML private TextArea $dataControl
  private ShaleiaWord $word

  public ShaleiaEditorController(UtilityStage<Boolean> stage) {
    super(stage)
    loadResource(RESOURCE_PATH, TITLE, DEFAULT_WIDTH, DEFAULT_HEIGHT)
    setupShortcuts()
  }

  public void prepare(ShaleiaWord word, Boolean editsEmptyWord) {
    $word = word
    $nameControl.setText(word.getUniqueName())
    $dataControl.setText(word.getData())
    if (editsEmptyWord) {
      $nameControl.requestFocus()
    } else {
      $dataControl.requestFocus()
    }
  }

  public void prepare(ShaleiaWord word) {
    prepare(word, false)
  }

  @FXML
  protected void commit() {
    String name = $nameControl.getText()
    String data = $dataControl.getText()
    $word.update(name, data)
    $stage.commit(true)
  }

  private void setupShortcuts() {
    $scene.addEventHandler(KeyEvent.KEY_PRESSED) { KeyEvent event ->
      if (KeyCodeCombination.new(KeyCode.ENTER, KeyCombination.SHORTCUT_DOWN).match(event)) {
        commit()
      }
    }
  }

}