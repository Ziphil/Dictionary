package ziphil.controller

import groovy.transform.CompileStatic
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.stage.Stage
import ziphil.custom.CustomBuilderFactory


@CompileStatic @Newify
public class PrimitiveController<S extends Stage> {

  protected S $stage
  protected Scene $scene

  public PrimitiveController(S stage) {
    $stage = stage
  }

  protected void loadResource(String resourcePath, String title, Double defaultWidth, Double defaultHeight, Double minWidth, Double minHeight, Boolean resizable) {
    FXMLLoader loader = FXMLLoader.new(getClass().getClassLoader().getResource(resourcePath), null, CustomBuilderFactory.new())
    loader.setController(this)
    Parent root = (Parent)loader.load()
    $scene = Scene.new(root, defaultWidth, defaultHeight)
    $stage.setScene($scene)
    $stage.setTitle(title)
    $stage.setResizable(resizable)
    if (minWidth != null) {
      $stage.setMinWidth(minWidth)
    }
    if (minHeight != null) {
      $stage.setMinHeight(minHeight)
    }
    $stage.sizeToScene()
  }

  protected void loadResource(String resourcePath, String title, Double defaultWidth, Double defaultHeight, Double minWidth, Double minHeight) {
    loadResource(resourcePath, title, defaultWidth, defaultHeight, minWidth, minHeight, true)
  }

  protected void loadResource(String resourcePath, String title, Double defaultWidth, Double defaultHeight, Boolean resizable) {
    loadResource(resourcePath, title, defaultWidth, defaultHeight, null, null, resizable)
  }

  protected void loadResource(String resourcePath, String title, Double defaultWidth, Double defaultHeight) {
    loadResource(resourcePath, title, defaultWidth, defaultHeight, null, null, true)
  }

}