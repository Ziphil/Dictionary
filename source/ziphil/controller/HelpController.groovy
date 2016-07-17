package ziphil.controller

import groovy.transform.CompileStatic
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.control.ListCell
import javafx.scene.control.ListView
import javafx.scene.input.MouseButton
import javafx.scene.input.MouseEvent
import javafx.scene.web.WebView
import javafx.stage.Stage
import ziphil.Launcher
import ziphil.custom.CustomBuilderFactory


@CompileStatic @Newify
public class HelpController {

  private static final String RESOURCE_PATH = "resource/fxml/help.fxml"
  private static final String SHORTCUT_HTML_PATH = "resource/help/shortcut.html"
  private static final String TITLE = "ヘルプ"
  private static final Double DEFAULT_WIDTH = 640
  private static final Double DEFAULT_HEIGHT = 480

  @FXML private ListView<String> $sectionList
  @FXML private WebView $help
  private Stage $stage
  private Scene $scene

  public HelpController(Stage stage) {
    $stage = stage
    loadResource()
  }

  @FXML
  private void initialize() {
    setupContentList()
  }

  private void changeHelp(String section) {
    String url = ""
    if (section == "ショートカットキー") {
      url = getClass().getClassLoader().getResource(SHORTCUT_HTML_PATH).toExternalForm()
    }
    $help.getEngine().load(url)
  }

  private void setupContentList() {
    $sectionList.setOnMouseClicked() { MouseEvent event ->
      if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
        String section = $sectionList.getSelectionModel().getSelectedItems()[0]
        changeHelp(section)
      }
    }
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