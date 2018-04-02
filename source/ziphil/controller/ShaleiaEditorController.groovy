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
import org.fxmisc.richtext.model.RichTextChange
import org.fxmisc.richtext.model.StyleSpansBuilder
import ziphil.custom.CodeAreaWrapper
import ziphil.custom.Measurement
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
    codeArea.richChanges().filter{!it.getInserted().equals(it.getRemoved())}.subscribe() { RichTextChange change ->
      StyleSpansBuilder<Collection<String>> builder = StyleSpansBuilder.new()
      String text = codeArea.textProperty().getValue()
      for (HighlightType type : HighlightType.values()) {
        type.updateMatcher(text)
      }
      Int index = 0
      while (true) {
        HighlightType matchedType = null
        Int startIndex = text.length()
        for (HighlightType type : HighlightType.values()) {
          Matcher matcher = type.getMatcher()
          if (matcher.find(index)) {
            if (matcher.start() < startIndex) {
              startIndex = matcher.start()
              matchedType = type
            }
          }
        }
        if (matchedType != null) {
          Matcher matcher = matchedType.getMatcher()
          builder.add([], matcher.start() - index)
          for (int i = 1 ; i <= matcher.groupCount() ; i ++) {
            if (i == 1) {
              builder.add([], matcher.start(i) - matcher.start())
            } else {
              builder.add([], matcher.start(i) - matcher.end(i - 1))
            }
            builder.add([matchedType.getNames()[i - 1]], matcher.end(i) - matcher.start(i))
          }
          builder.add([], matcher.end() - matcher.end(matcher.groupCount()))
          index = matcher.end()
        } else {
          builder.add([], text.length() - index)
          break
        }
      }
      codeArea.setStyleSpans(0, builder.create())
    }
  }

  private void setupShortcuts() {
    $scene.addEventHandler(KeyEvent.KEY_PRESSED) { KeyEvent event ->
      if (KeyCodeCombination.new(KeyCode.ENTER, KeyCombination.SHORTCUT_DOWN).match(event)) {
        commit()
      }
    }
  }

}


@InnerClass(ShaleiaEditorController)
@CompileStatic @Ziphilify
private static enum HighlightType {

  CREATION_DATE(/(?m)^(\+)\s*(\d+)(\s*〈.*〉|)/, "shaleia-creation-date-marker", "shaleia-creation-date", "shaleia-total-part"),
  CONTENT(/(?m)^([A-Z]>)/, "shaleia-content-marker"),
  NOTE(/(?m)^([A-Z]~)/, "shaleia-note-marker"),
  EQUIVALENT(/(?m)^(\=:?)(\s*〈.*〉|)/, "shaleia-equivalent-marker", "shaleia-part"),
  SYNONYM(/(?m)^(\-)(\s*〈.*〉|)/, "shaleia-synonym-marker", "shaleia-part"),
  SYMBOL(/(\{|\}|\[|\]|\/)(\*|)/, "shaleia-symbol", "shaleia-reference-mark")

  private String[] $names
  private String $regex
  private Matcher $matcher

  private HighlightType(String regex, String... names) {
    $names = names
    $regex = regex
  }

  public void updateMatcher(String text) {
    $matcher = text =~ $regex
  }

  public String[] getNames() {
    return $names
  }

  public Matcher getMatcher() {
    return $matcher
  }

}