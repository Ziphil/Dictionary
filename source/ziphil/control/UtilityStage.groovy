package ziphil.control

import groovy.transform.CompileStatic
import javafx.stage.Stage
import javafx.stage.StageStyle


@CompileStatic @Newify
public class UtilityStage<T> extends Stage {

  private T $result

  public UtilityStage() {
    super()
  }

  public UtilityStage(StageStyle style) {
    super(style)
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