package ziphil.controller

import groovy.transform.CompileStatic
import javafx.fxml.FXML
import ziphil.custom.UtilityStage
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class Controller<T> extends PrimitiveController<UtilityStage<? super T>> {

  public Controller(UtilityStage<? super T> stage) {
    super(stage)
  }

  @FXML
  protected void commit() {
    ((UtilityStage<? super T>)$stage).commit(null)
  }

  @FXML
  protected void cancel() {
    ((UtilityStage<? super T>)$stage).cancel()
  }

}