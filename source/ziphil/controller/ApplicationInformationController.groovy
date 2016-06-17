package ziphil.controller

import groovy.transform.CompileStatic
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.control.Label
import javafx.stage.Stage
import javafx.stage.Modality
import javafx.stage.StageStyle
import ziphil.Launcher
import ziphil.custom.CustomBuilderFactory


@CompileStatic @Newify
public class ApplicationInformationController {

  private static final String RESOURCE_PATH = "resource/fxml/application_information.fxml"
  private static final String TITLE = "ZpDICについて"
  private static final Double DEFAULT_WIDTH = -1
  private static final Double DEFAULT_HEIGHT = -1

  @FXML private Label $version
  private Stage $stage
  private Scene $scene

  public ApplicationInformationController(Stage stage) {
    $stage = stage
    loadResource()
  }

  @FXML
  private void initialize() {
    setupVersion()
  }

  private void setupVersion() {
    String version = "version ${Launcher.VERSION}-${Launcher.DATE}"
    $version.setText(version)
  }

  private void loadResource() {
    FXMLLoader loader = FXMLLoader.new(getClass().getClassLoader().getResource(RESOURCE_PATH), null, CustomBuilderFactory.new())
    loader.setController(this)
    Parent root = (Parent)loader.load()
    $scene = Scene.new(root, DEFAULT_WIDTH, DEFAULT_HEIGHT)
    $stage.setScene($scene)
    $stage.setTitle(TITLE)
    $stage.setResizable(false)
    $stage.sizeToScene()
  }

}