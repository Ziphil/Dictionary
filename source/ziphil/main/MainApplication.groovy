package ziphil.main

import groovy.transform.CompileStatic
import javafx.application.Application
import javafx.application.Platform
import javafx.stage.Stage
import ziphil.Launcher
import ziphil.controller.MainController
import ziphil.module.Setting


@CompileStatic @Newify
public class MainApplication extends Application {

  public void start(Stage stage) {
    createDataDirectory()
    setupFontRendering()
    load(stage)
    setupExceptionHandler()
    setupStylesheet()
  }

  private void load(Stage stage) {
    MainController controller = MainController.new(stage)
    stage.show()
  }

  public void stop() {
    Setting.getInstance().save()
  }

  private void createDataDirectory() {
    File.new(Launcher.BASE_PATH + "data/setting").mkdirs()
  }

  private void setupFontRendering() {
    Setting setting = Setting.getInstance()
    if (setting.getFontRenderingType() == 1) {
      System.setProperty("prism.lcdtext", "true")
      System.setProperty("prism.text", "t2k")
    } else if (setting.getFontRenderingType() == 2) {
      System.setProperty("prism.lcdtext", "false")
      System.setProperty("prism.text", "t2k")
    }
  }

  private void setupExceptionHandler() {
    Thread.currentThread().setUncaughtExceptionHandler() { Thread thread, Throwable throwable ->
      throwable.printStackTrace()
      Platform.exit()
    }
  }

  private void setupStylesheet() {
    setUserAgentStylesheet(STYLESHEET_MODENA)
  }

}