package ziphil.controller

import groovy.transform.CompileStatic
import java.util.regex.Matcher
import javafx.fxml.FXML
import javafx.scene.control.TextArea
import javafx.scene.control.TextField
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyCodeCombination
import javafx.scene.input.KeyCombination
import javafx.scene.input.KeyEvent
import org.fxmisc.richtext.CodeArea
import ziphil.custom.CodeAreaWrapper
import ziphil.custom.Measurement
import ziphil.custom.RichTextChangeConsumer
import ziphil.custom.UtilityStage
import ziphil.dictionary.WordEditResult
import ziphil.dictionary.shaleia.ShaleiaWord
import ziphil.module.Setting
import ziphilib.transform.InnerClass
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class ShaleiaEditorController extends Controller<WordEditResult> {

  private static final String RESOURCE_PATH = "resource/fxml/controller/shaleia_editor.fxml"
  private static final String TITLE = "単語編集"
  private static final Double DEFAULT_WIDTH = Measurement.rpx(640)
  private static final Double DEFAULT_HEIGHT = Measurement.rpx(320)

  @FXML private TextField $nameControl
  @FXML private CodeAreaWrapper $descriptionControl
  private ShaleiaWord $word

  public ShaleiaEditorController(UtilityStage<? super WordEditResult> stage) {
    super(stage)
    loadResource(RESOURCE_PATH, TITLE, DEFAULT_WIDTH, DEFAULT_HEIGHT)
    setupShortcuts()
  }

  @FXML
  private void initialize() {
    setupDescriptionControl()
  }

  public void prepare(ShaleiaWord word, Boolean empty) {
    $word = word
    $nameControl.setText(word.getUniqueName())
    $descriptionControl.getCodeArea().replaceText(0, 0, word.getDescription())
    if (empty) {
      $nameControl.requestFocus()
    } else {
      $descriptionControl.requestFocus()
    }
  }

  public void prepare(ShaleiaWord word) {
    prepare(word, false)
  }

  @FXML
  protected void commit() {
    $word.setUniqueName($nameControl.getText())
    $word.setDescription($descriptionControl.getCodeArea().textProperty().getValue())
    $word.update()
    WordEditResult result = WordEditResult.new($word)
    $stage.commit(result)
  }

  private void setupDescriptionControl() {
    CodeArea codeArea = $descriptionControl.getCodeArea()
    RichTextChangeConsumer consumer = RichTextChangeConsumer.new(codeArea)
    consumer.addSyntax(/(?m)^(\+)\s*(\d+)(\s*〈.*〉|)/, "shaleia-creation-date-marker", "shaleia-creation-date", "shaleia-total-part")
    consumer.addSyntax(/(?m)^([A-Z]>)/, "shaleia-content-marker")
    consumer.addSyntax(/(?m)^(\=:?)(\s*〈.*〉|)/, "shaleia-equivalent-marker", "shaleia-part")
    consumer.addSyntax(/(?m)^(\-)(\s*〈.*〉|)/, "shaleia-synonym-marker", "shaleia-part")
    consumer.addSyntax(/(\{|\}|\[|\]|\/)(\*|)/, "shaleia-symbol", "shaleia-reference-mark")
    codeArea.richChanges().filter{it.getInserted() != it.getRemoved()}.subscribe(consumer)
  }

  private void setupShortcuts() {
    $scene.addEventHandler(KeyEvent.KEY_PRESSED) { KeyEvent event ->
      if (KeyCodeCombination.new(KeyCode.ENTER, KeyCombination.SHORTCUT_DOWN).match(event)) {
        commit()
      }
    }
  }

}