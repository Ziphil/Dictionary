package ziphil.module

import groovy.transform.CompileStatic
import java.util.concurrent.Callable
import javafx.beans.binding.Bindings
import javafx.beans.binding.StringBinding
import javafx.beans.value.ObservableBooleanValue
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class CustomBindings {

  public static StringBinding whichString(ObservableBooleanValue observableValue, String trueString, String falseString) {
    Callable<String> function = (Callable){
      return (observableValue.get()) ? trueString : falseString
    }
    StringBinding binding = Bindings.createStringBinding(function, observableValue)
    return binding
  }

}