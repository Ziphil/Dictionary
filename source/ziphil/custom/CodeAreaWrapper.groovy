package ziphil.custom

import groovy.transform.CompileStatic
import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.value.ObservableValue
import javafx.css.PseudoClass
import javafx.scene.control.Control
import javafx.scene.control.Skin
import org.fxmisc.richtext.CodeArea
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
    $codeArea.get().focusedProperty().addListener() { ObservableValue<? extends BooleanClass> observableValue, BooleanClass oldValue, BooleanClass newValue ->
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