package ziphil.controller

import groovy.transform.CompileStatic
import java.util.function.Consumer
import java.util.regex.Matcher
import javafx.fxml.FXML
import javafx.scene.control.TextArea
import javafx.scene.control.TextField
import org.fxmisc.richtext.CodeArea
import org.fxmisc.richtext.model.RichTextChange
import ziphil.custom.CodeAreaWrapper
import ziphil.custom.Measurement
import ziphil.custom.RichTextChangeConsumer
import ziphil.custom.RichTextLanguage
import ziphil.custom.UtilityStage
import ziphil.dictionary.WordEditResult
import ziphil.dictionary.shaleia.ShaleiaDictionary
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
  private ShaleiaDictionary $dictionary

  public ShaleiaEditorController(UtilityStage<? super WordEditResult> stage) {
    super(stage)
    loadResource(RESOURCE_PATH, TITLE, DEFAULT_WIDTH, DEFAULT_HEIGHT)
  }

  @FXML
  private void initialize() {
    setupDescriptionControl()
  }

  public void prepare(ShaleiaWord word, ShaleiaDictionary dictionary, Boolean empty) {
    $word = word
    $dictionary = dictionary
    $nameControl.setText(word.getUniqueName())
    $descriptionControl.getCodeArea().replaceText(0, 0, word.getDescription())
    if (empty) {
      $nameControl.requestFocus()
    } else {
      $descriptionControl.requestFocus()
    }
  }

  public void prepare(ShaleiaWord word, ShaleiaDictionary dictionary) {
    prepare(word, dictionary, false)
  }

  @FXML
  protected void commit() {
    String uniqueName = $nameControl.getText()
    if (!$dictionary.containsUniqueName(uniqueName, $word)) {
      $word.setUniqueName(uniqueName)
      $word.setDescription($descriptionControl.getCodeArea().textProperty().getValue())
      $word.update()
      WordEditResult result = WordEditResult.new($word)
      $stage.commit(result)
    } else {
      showErrorDialog("duplicateName")
    }
  }

  private void setupDescriptionControl() {
    CodeArea codeArea = $descriptionControl.getCodeArea()
    Consumer<RichTextChange> consumer = RichTextLanguage.SHALEIA_DICTIONARY.createConsumer(codeArea)
    codeArea.richChanges().filter{it.getInserted() != it.getRemoved()}.subscribe(consumer)
    codeArea.setWrapText(true)
  }

}