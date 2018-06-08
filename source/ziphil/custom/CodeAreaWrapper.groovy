package ziphil.custom

import groovy.transform.CompileStatic
import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.value.ObservableValue
import javafx.css.PseudoClass
import javafx.event.EventHandler
import javafx.geometry.Bounds
import javafx.geometry.Point2D
import javafx.scene.control.Control
import javafx.scene.control.IndexRange
import javafx.scene.control.Skin
import javafx.scene.input.InputMethodEvent
import javafx.scene.input.InputMethodRequests
import javafx.scene.input.InputMethodTextRun
import javafx.scene.shape.Shape
import org.fxmisc.richtext.CodeArea
import ziphilib.transform.InnerClass
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class CodeAreaWrapper extends Control {

  private static final String STYLE_CLASS = "code-area-wrapper"
  private static final PseudoClass CONTENT_FOCUSED_PSEUDO_CLASS = PseudoClass.getPseudoClass("content-focused")

  private ObjectProperty<CodeArea> $codeArea = SimpleObjectProperty.new(CodeArea.new())

  public CodeAreaWrapper() {
    setup()
    setupCodeArea()
  }

  protected Skin<CodeAreaWrapper> createDefaultSkin() {
    return CodeAreaWrapperSkin.new(this)
  }

  private void setup() {
    getStyleClass().add(STYLE_CLASS)
  }

  private void setupCodeArea() {
    CodeArea codeArea = $codeArea.get()
    codeArea.setInputMethodRequests(EditorInputMethodRequests.new(codeArea))
    codeArea.setOnInputMethodTextChanged(EditorInputMethodEventHandler.new(codeArea))
    codeArea.focusedProperty().addListener() { ObservableValue<? extends BooleanClass> observableValue, BooleanClass oldValue, BooleanClass newValue ->
      pseudoClassStateChanged(CONTENT_FOCUSED_PSEUDO_CLASS, newValue)
    }
  }

  public CodeArea getCodeArea() {
    return $codeArea.get()
  }

  public void setCodeArea(CodeArea codeArea) {
    $codeArea.set(codeArea)
  }

  public ObjectProperty<CodeArea> codeAreaProperty() {
    return $codeArea
  }

}


@InnerClass(CodeAreaWrapper)
@CompileStatic @Ziphilify
private static class EditorInputMethodRequests implements InputMethodRequests {

  private CodeArea $codeArea

  public EditorInputMethodRequests(CodeArea codeArea) {
    $codeArea = codeArea
  }

  public Point2D getTextLocation(Int offset) {
    Optional<Bounds> optionalCaretBounds = $codeArea.getCaretBounds()
    if (optionalCaretBounds.isPresent()) {
      Bounds caretBounds = optionalCaretBounds.get()
      return Point2D.new(caretBounds.getMinX(), caretBounds.getMinY() + 20)
    } else {
      return null
    }
  }

  public int getLocationOffset(Int x, Int y) {
    return 0
  }

  public void cancelLatestCommittedText() {
  }

  public String getSelectedText() {
    IndexRange selection = $codeArea.getSelection()
    return $codeArea.getText(selection.getStart(), selection.getEnd())
  }

}


@InnerClass(CodeAreaWrapper)
@CompileStatic @Ziphilify
private static class EditorInputMethodEventHandler implements EventHandler<InputMethodEvent> {

  private CodeArea $codeArea
  private Int $start = 0
  private Int $length = 0
  private List<Shape> $attributes = ArrayList.new()

  public EditorInputMethodEventHandler(CodeArea codeArea) {
    $codeArea = codeArea
  }

  public void handle(InputMethodEvent event) {
    if ($codeArea.isEditable() && !$codeArea.isDisabled()) {
      if ($length != 0) {
        $attributes.clear()
        $codeArea.selectRange($start, $start + $length)
      }
      if (event.getCommitted().length() != 0) {
        String committed = event.getCommitted()
        $codeArea.replaceText($codeArea.getSelection().getStart(), $codeArea.getSelection().getEnd(), committed)
      }
      $start = $codeArea.getSelection().getStart()
      StringBuilder composed = StringBuilder.new()
      for (InputMethodTextRun run : event.getComposed()) {
        composed.append(run.getText())
      }
      $codeArea.replaceText($codeArea.getSelection().getStart(), $codeArea.getSelection().getEnd(), composed.toString())
      $length = composed.length()
      if ($length != 0) {
        Int position = $start
        for (InputMethodTextRun run : event.getComposed()) {
          Int endPosition = position + run.getText().length()
          position = endPosition
        }
        Int caretPosition = event.getCaretPosition()
        if (caretPosition >= 0 && caretPosition < $length) {
          $codeArea.selectRange($start + caretPosition, $start + caretPosition)
        }
      }
    }
  }

}