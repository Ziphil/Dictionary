package ziphil.controller

import groovy.transform.CompileStatic
import java.util.ResourceBundle
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.stage.Stage
import ziphil.custom.CustomBuilderFactory
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class PrimitiveController<S extends Stage> {

  private static final String RESOUCE_BASE_NAME = "resource.text.fxml"

  protected S $stage
  protected Scene $scene

  public PrimitiveController(S stage) {
    $stage = stage
  }

  protected void loadResource(String resourcePath, String title, Double defaultWidth, Double defaultHeight, Double minWidth, Double minHeight, Boolean resizable) {
    ResourceBundle resources = ResourceBundle.getBundle(RESOUCE_BASE_NAME)
    FXMLLoader loader = FXMLLoader.new(getClass().getClassLoader().getResource(resourcePath), resources, CustomBuilderFactory.new())
    loader.setController(this)
    Parent root = (Parent)loader.load()
    $scene = Scene.new(root, defaultWidth, defaultHeight)
    $stage.setScene($scene)
    $stage.setTitle(title)
    $stage.setResizable(resizable)
    if (minWidth >= 0) {
      $stage.setMinWidth(minWidth)
    }
    if (minHeight >= 0) {
      $stage.setMinHeight(minHeight)
    }
    $stage.sizeToScene()
  }

  protected void loadResource(String resourcePath, String title, Double defaultWidth, Double defaultHeight, Double minWidth, Double minHeight) {
    loadResource(resourcePath, title, defaultWidth, defaultHeight, minWidth, minHeight, true)
  }

  protected void loadResource(String resourcePath, String title, Double defaultWidth, Double defaultHeight, Boolean resizable) {
    loadResource(resourcePath, title, defaultWidth, defaultHeight, -1, -1, resizable)
  }

  protected void loadResource(String resourcePath, String title, Double defaultWidth, Double defaultHeight) {
    loadResource(resourcePath, title, defaultWidth, defaultHeight, -1, -1, true)
  }

  public S getStage() {
    return $stage
  }

  public Scene getScene() {
    return $scene
  }

}