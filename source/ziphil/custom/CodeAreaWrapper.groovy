package ziphil.custom

import groovy.transform.CompileStatic
import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.value.ObservableValue
import javafx.css.PseudoClass
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
  private Int $codeAreaStart = 0
  private Int $codeAreaLength = 0
  private List<Shape> $codeAreaAttributes = ArrayList.new()

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
    codeArea.focusedProperty().addListener() { ObservableValue<? extends BooleanClass> observableValue, BooleanClass oldValue, BooleanClass newValue ->
      pseudoClassStateChanged(CONTENT_FOCUSED_PSEUDO_CLASS, newValue)
    }
    codeArea.setOnInputMethodTextChanged() { InputMethodEvent event ->
      if (codeArea.isEditable() && !codeArea.isDisabled()) {
        if ($codeAreaLength != 0) {
          $codeAreaAttributes.clear()
          codeArea.selectRange($codeAreaStart, $codeAreaStart + $codeAreaLength)
        }
        if (event.getCommitted().length() != 0) {
          String committed = event.getCommitted()
          codeArea.replaceText(codeArea.getSelection().getStart(), codeArea.getSelection().getEnd(), committed)
        }
        $codeAreaStart = codeArea.getSelection().getStart()
        StringBuilder composed = StringBuilder.new()
        for (InputMethodTextRun run : event.getComposed()) {
          composed.append(run.getText())
        }
        codeArea.replaceText(codeArea.getSelection().getStart(), codeArea.getSelection().getEnd(), composed.toString())
        $codeAreaLength = composed.length()
        if ($codeAreaLength != 0) {
          Int position = $codeAreaStart
          for (InputMethodTextRun run : event.getComposed()) {
            Int endPosition = position + run.getText().length()
            position = endPosition
          }
          Int caretPosition = event.getCaretPosition()
          if (caretPosition >= 0 && caretPosition < $codeAreaLength) {
            codeArea.selectRange($codeAreaStart + caretPosition, $codeAreaStart + caretPosition)
          }
        }
      }
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