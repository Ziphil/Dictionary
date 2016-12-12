package ziphil.custom

import groovy.transform.CompileStatic
import javafx.stage.Stage
import javafx.stage.StageStyle
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class UtilityStage<T> extends Stage {

  private T $result
  private Boolean $isCommitted = false

  public UtilityStage() {
    super()
  }

  public UtilityStage(StageStyle style) {
    super(style)
  }

  public void commit(T result) {
    $result = result
    $isCommitted = true
    close()
  }

  public void cancel() {
    $result = null
    $isCommitted = false
    close()
  }

  public T getResult() {
    return $result
  }

  public Boolean isCommitted() {
    return $isCommitted
  }

  public Boolean isCancelled() {
    return !$isCommitted
  }

}