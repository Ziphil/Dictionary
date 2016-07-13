package ziphil.module

import groovy.transform.CompileStatic
import java.util.concurrent.Callable
import javafx.beans.binding.Bindings
import javafx.beans.binding.StringBinding
import javafx.scene.control.ToggleButton


@CompileStatic @Newify
public class CustomBindings {

  public static StringBinding whichString(ToggleButton button, String trueString, String falseString) {
    Callable<String> function = (Callable){
      return (button.selectedProperty().get()) ? trueString : falseString
    }
    StringBinding binding = Bindings.createStringBinding(function, button.selectedProperty())
    return binding
  }

}