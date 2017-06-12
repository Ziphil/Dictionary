package ziphil.custom

import groovy.transform.CompileStatic
import java.util.concurrent.Callable
import javafx.beans.binding.Bindings
import javafx.beans.binding.StringBinding
import javafx.beans.property.StringProperty
import javafx.beans.property.SimpleStringProperty
import javafx.scene.control.ToggleButton
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class SwitchButton extends ToggleButton {

  private StringProperty $trueText = SimpleStringProperty.new("有効")
  private StringProperty $falseText = SimpleStringProperty.new("無効")

  public SwitchButton() {
    super()
    bindProperty()
  }

  public SwitchButton(String trueText, String falseText) {
    super()
    $trueText.set(trueText)
    $falseText.set(falseText)
    bindProperty()
  }

  private void bindProperty() {
    Callable<String> function = (Callable){
      return (selectedProperty().get()) ? $trueText.get() : $falseText.get()
    }
    StringBinding binding = Bindings.createStringBinding(function, selectedProperty(), $trueText, $falseText)
    textProperty().bind(binding)
  }

  public String getTrueText() {
    return $trueText.get()
  }

  public void setTrueText(String trueText) {
    $trueText.set(trueText)
  }

  public StringProperty trueTextProperty() {
    return $trueText
  }

  public String getFalseText() {
    return $falseText.get()
  }

  public void setFalseText(String falseText) {
    $falseText.set(falseText)
  }

  public StringProperty falseTextProperty() {
    return $falseText
  }

}