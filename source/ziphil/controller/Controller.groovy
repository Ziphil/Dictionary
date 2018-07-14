package ziphil.controller

import groovy.transform.CompileStatic
import javafx.fxml.FXML
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyCodeCombination
import javafx.scene.input.KeyCombination
import javafx.scene.input.KeyEvent
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

  protected void loadResource(String resourcePath, String title, Double defaultWidth, Double defaultHeight, Double minWidth, Double minHeight, Boolean resizable) {
    super.loadResource(resourcePath, title, defaultWidth, defaultHeight, minWidth, minHeight, resizable)
    $scene.addEventHandler(KeyEvent.KEY_PRESSED) { KeyEvent event ->
      if (KeyCodeCombination.new(KeyCode.ENTER, KeyCombination.SHORTCUT_DOWN).match(event)) {
        commit()
      }
    }
  }

}