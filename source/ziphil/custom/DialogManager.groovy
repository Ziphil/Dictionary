package ziphil.custom

import groovy.transform.CompileStatic
import javafx.beans.value.ObservableValue
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.stage.Stage
import javafx.stage.Modality
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class DialogManager {

  private static final String RESOURCE_PATH = "resource/fxml/custom/dialog.fxml"
  private static final String TITLE = "通知"
  private static final Double DEFAULT_WIDTH = Measurement.rpx(360)
  private static final Double DEFAULT_HEIGHT = -1

  @FXML private Label $contentLabel
  @FXML private Button $commitButton
  @FXML private Button $negateButton
  @FXML private Button $cancelButton
  private Scene $scene
  private Dialog $stage

  public DialogManager(Dialog stage) {
    $stage = stage
    loadResource(RESOURCE_PATH, TITLE, DEFAULT_WIDTH, DEFAULT_HEIGHT)
    setupStage()
  }

  @FXML
  private void initialize() {
    bindButtonTextProperties()
    bindButtonVisibleProperties()
    setupCommitButton()
  }

  @FXML
  private void commit() {
    $stage.commit()
    $stage.close()
  }

  @FXML
  private void negate() {
    $stage.negate()
    $stage.close()
  }

  @FXML
  private void cancel() {
    $stage.cancel()
    $stage.close()
  }

  private void setupStage() {
    $stage.initModality(Modality.APPLICATION_MODAL)
  }

  private void bindButtonTextProperties() {
    $contentLabel.textProperty().bind($stage.contentTextProperty())
    $commitButton.textProperty().bind($stage.commitTextProperty())
    $negateButton.textProperty().bind($stage.negateTextProperty())
    $cancelButton.textProperty().bind($stage.cancelTextProperty())
  }

  private void bindButtonVisibleProperties() {
    $negateButton.visibleProperty().bind($stage.allowsNegateProperty())
    $negateButton.managedProperty().bind($stage.allowsNegateProperty())
    $cancelButton.visibleProperty().bind($stage.allowsCancelProperty())
    $cancelButton.managedProperty().bind($stage.allowsCancelProperty())
  }

  private void setupCommitButton() {
    $commitButton.sceneProperty().addListener() { ObservableValue<? extends Scene> observableValue, Scene oldValue, Scene newValue ->
      if (oldValue == null && newValue != null) {
        $commitButton.requestFocus()
      }
    }
  }

  private void loadResource(String resourcePath, String title, Double defaultWidth, Double defaultHeight) {
    FXMLLoader loader = FXMLLoader.new(getClass().getClassLoader().getResource(resourcePath), null, CustomBuilderFactory.new())
    loader.setController(this)
    Parent root = (Parent)loader.load()
    $scene = Scene.new(root, defaultWidth, defaultHeight)
    $stage.setScene($scene)
    $stage.setTitle(title)
    $stage.sizeToScene()
  }

}