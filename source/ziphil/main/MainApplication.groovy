package ziphil.main

import groovy.transform.CompileStatic
import javafx.application.Application
import javafx.stage.Stage
import ziphil.Launcher
import ziphil.controller.MainController


@CompileStatic @Newify
public class MainApplication extends Application {

  public void start(Stage stage) {
    load(stage)
    setupStylesheet()
  }

  private void load(Stage stage) {
    MainController controller = MainController.new(stage)
    stage.show()
  }

  private void setupStylesheet() {
    setUserAgentStylesheet(STYLESHEET_MODENA)
  }

}