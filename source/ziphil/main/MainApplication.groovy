package ziphil.main

import groovy.transform.CompileStatic
import javafx.application.Application
import javafx.application.Platform
import javafx.stage.Stage
import ziphil.Launcher
import ziphil.controller.MainController
import ziphil.module.FontRenderingType
import ziphil.module.Setting
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class MainApplication extends Application {

  private static final String DATABASE_LOG_PATH = "data/log/derby.log"

  public void start(Stage stage) {
    makeDirectories()
    setupFontRendering()
    setupDatabaseLogPath()
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

  private void makeDirectories() {
    File.new(Launcher.BASE_PATH + "data/setting").mkdirs()
    File.new(Launcher.BASE_PATH + "data/setting/individual").mkdirs()
    File.new(Launcher.BASE_PATH + "data/log").mkdirs()
  }

  private void setupFontRendering() {
    FontRenderingType fontRenderingType = Setting.getInstance().getFontRenderingType()
    if (fontRenderingType == FontRenderingType.DEFAULT_GRAY) {
      System.setProperty("prism.lcdtext", "false")
    } else if (fontRenderingType == FontRenderingType.PRISM_LCD) {
      System.setProperty("prism.lcdtext", "true")
      System.setProperty("prism.text", "t2k")
    } else if (fontRenderingType == FontRenderingType.PRISM_GRAY) {
      System.setProperty("prism.lcdtext", "false")
      System.setProperty("prism.text", "t2k")
    }
  }

  private void setupDatabaseLogPath() {
    System.setProperty("derby.stream.error.file", Launcher.BASE_PATH + DATABASE_LOG_PATH)
  }

  private void setupStylesheet() {
    setUserAgentStylesheet(STYLESHEET_MODENA)
  }

}