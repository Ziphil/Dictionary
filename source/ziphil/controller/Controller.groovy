package ziphil.controller

import groovy.transform.CompileStatic
import javafx.fxml.FXML
import ziphil.custom.UtilityStage
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class Controller<T> extends PrimitiveController<UtilityStage<T>> {

  public Controller(UtilityStage<T> stage) {
    super(stage)
  }

  @FXML
  protected void commit() {
    ((UtilityStage<T>)$stage).close(null)
  }

  @FXML
  protected void cancel() {
    ((UtilityStage<T>)$stage).close(null)
  }

}