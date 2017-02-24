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

  public void start(Stage stage) {
    makeDirectories()
    setupFontRendering()
    setupScriptProperty()
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

  private void setupScriptProperty() {
    System.setProperty("org.jruby.embed.localvariable.behavior", "persistent")
  }

  private void setupStylesheet() {
    setUserAgentStylesheet(STYLESHEET_MODENA)
  }

}