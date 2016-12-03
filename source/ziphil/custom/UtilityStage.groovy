package ziphil.custom

import groovy.transform.CompileStatic
import javafx.stage.Stage
import javafx.stage.StageStyle
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class UtilityStage<T> extends Stage {

  private T $result

  public UtilityStage() {
    super()
  }

  public UtilityStage(StageStyle style) {
    super(style)
  }

  public T showAndWaitResult() {
    showAndWait()
    return $result
  }

  public void close(T result) {
    $result = result
    close()
  }

  public T getResult() {
    return $result
  }

  public void setResult(T result) {
    $result = result
  }

}