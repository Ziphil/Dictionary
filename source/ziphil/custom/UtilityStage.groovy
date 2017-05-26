package ziphil.custom

import groovy.transform.CompileStatic
import javafx.stage.Stage
import javafx.stage.StageStyle
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class UtilityStage<T> extends Stage {

  private T $result
  private Boolean $committed = false

  public UtilityStage() {
    super()
  }

  public UtilityStage(StageStyle style) {
    super(style)
  }

  public void commit(T result) {
    $result = result
    $committed = true
    close()
  }

  public void cancel() {
    $result = null
    $committed = false
    close()
  }

  public T getResult() {
    return $result
  }

  public Boolean isCommitted() {
    return $committed
  }

  public Boolean isCancelled() {
    return !$committed
  }

}