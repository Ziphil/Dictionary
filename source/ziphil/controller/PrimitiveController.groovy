package ziphil.controller

import groovy.transform.CompileStatic
import java.util.ResourceBundle
import javafx.fxml.FXMLLoader
import javafx.stage.Modality
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.stage.Stage
import javafx.stage.StageStyle
import javafx.stage.Window
import javafx.stage.WindowEvent
import ziphil.custom.CustomBuilderFactory
import ziphil.custom.Dialog
import ziphil.custom.UtilityStage
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class PrimitiveController<S extends Stage> {

  public static final ResourceBundle FXML_RESOURCES = ResourceBundle.getBundle("resource.text.fxml")
  public static final ResourceBundle DIALOG_RESOURCES = ResourceBundle.getBundle("resource.text.dialog")
  private static final Double CENTER_X_FRACTION = 1D / 2
  private static final Double CENTER_Y_FRACTION = 1D / 3

  protected S $stage
  protected Scene $scene

  public PrimitiveController(S stage) {
    $stage = stage
  }

  protected <U> UtilityStage<U> createStage(Modality modality, Window owner) {
    UtilityStage<U> nextStage = UtilityStage.new(StageStyle.UTILITY)
    if (modality != null) {
      nextStage.initModality(modality)
    }
    if (owner != null) {
      nextStage.initOwner(owner)
    }
    nextStage.addEventFilter(WindowEvent.WINDOW_SHOWN) { WindowEvent event ->
      Double centerX = $stage.getX() + ($stage.getWidth() - nextStage.getWidth()) * CENTER_X_FRACTION
      Double centerY = $stage.getY() + ($stage.getHeight() - nextStage.getHeight()) * CENTER_Y_FRACTION
      nextStage.setX(centerX)
      nextStage.setY(centerY)
    }
    return nextStage
  }

  protected <U> UtilityStage<U> createStage(Modality modality) {
    return createStage(modality, $stage)
  }

  protected <U> UtilityStage<U> createStage() {
    return createStage(Modality.WINDOW_MODAL, $stage)
  }

  protected void showErrorDialog(String key) {
    Dialog dialog = Dialog.new() 
    dialog.initOwner($stage)
    dialog.setTitle(DIALOG_RESOURCES.getString("title." + key))
    dialog.setContentText(DIALOG_RESOURCES.getString("contentText." + key))
    dialog.setAllowsCancel(false)
    dialog.showAndWait()
  }

  protected void outputStackTrace(Throwable throwable, String path) {
    PrintWriter writer = PrintWriter.new(path)
    throwable.printStackTrace()
    throwable.printStackTrace(writer)
    writer.flush()
    writer.close()
  }

  protected void loadResource(String resourcePath, String title, Double defaultWidth, Double defaultHeight, Double minWidth, Double minHeight, Boolean resizable) {
    FXMLLoader loader = FXMLLoader.new(getClass().getClassLoader().getResource(resourcePath), FXML_RESOURCES, CustomBuilderFactory.new())
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