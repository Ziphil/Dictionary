package ziphil.main

import groovy.transform.CompileStatic
import javafx.application.Application
import javafx.stage.Stage
import ziphil.controller.MainController
import ziphil.module.Setting


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

  public void stop() {
    Setting.getInstance().save()
  }

  private void setupStylesheet() {
    setUserAgentStylesheet(STYLESHEET_MODENA)
  }

}