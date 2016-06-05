package ziphil.main

import org.scenicview.ScenicView
import groovy.transform.CompileStatic
import javafx.application.Application
import javafx.stage.Stage
import ziphil.Launcher
import ziphil.control.MainController


@CompileStatic @Newify
public class MainApplication extends Application {

  public void start(Stage stage) {
    load(stage)
    setupStylesheet()
  }

  private void load(Stage stage) {
    MainController controller = MainController.new(stage)
    stage.show()
    if (Launcher.DEBUG) {
      ScenicView.show(controller.getScene())
    }
  }

  private void setupStylesheet() {
    setUserAgentStylesheet(STYLESHEET_MODENA)
  }

}