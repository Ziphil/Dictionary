package ziphil.main

import groovy.transform.CompileStatic
import javafx.application.Application
import javafx.application.Platform
import javafx.stage.Stage
import ziphil.Launcher
import ziphil.controller.MainController
import ziphil.module.Setting
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class MainApplication extends Application {

  public void start(Stage stage) {
    createDirectories()
    setupFontRendering()
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

  private void createDirectories() {
    File.new(Launcher.BASE_PATH + "data/setting").mkdirs()
    File.new(Launcher.BASE_PATH + "data/log").mkdirs()
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

  private void setupStylesheet() {
    setUserAgentStylesheet(STYLESHEET_MODENA)
  }

}