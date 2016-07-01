package ziphil.custom

import groovy.transform.CompileStatic
import javafx.application.Platform
import javafx.beans.property.BooleanProperty
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleStringProperty
import javafx.beans.property.StringProperty
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.stage.Stage
import javafx.stage.StageStyle
import javafx.stage.Modality
import javafx.stage.StageStyle


@CompileStatic @Newify
public class Dialog extends Stage {

  private static final String RESOURCE_PATH = "resource/fxml/dialog.fxml"
  private static final Double DEFAULT_WIDTH = Measurement.rpx(360)
  private static final Double DEFAULT_HEIGHT = -1

  @FXML private Label $content
  @FXML private Button $commit
  @FXML private Button $cancel
  private StringProperty $contentString = SimpleStringProperty.new()
  private StringProperty $commitString = SimpleStringProperty.new("OK")
  private StringProperty $cancelString = SimpleStringProperty.new("キャンセル")
  private BooleanProperty $allowsCancel = SimpleBooleanProperty.new(true)
  private Boolean $result = false
  private Scene $scene

  public Dialog() {
    super(StageStyle.UTILITY)
    loadResource()
    setupStage()
  }

  @FXML
  private void initialize(){
    setupContent()
    setupButtons()
  }

  public Boolean showAndWaitResult() {
    showAndWait()
    return $result
  }

  @FXML
  private void cancel() {
    $result = false
    close()
  }

  @FXML
  private void commit() {
    $result = true
    close()
  }

  private void setupStage() {
    initModality(Modality.WINDOW_MODAL)
  }

  private void setupContent() {
    $content.textProperty().bind($contentString)
  }

  private void setupButtons() {
    $commit.textProperty().bind($commitString)
    $cancel.textProperty().bind($cancelString)
    $cancel.visibleProperty().bind($allowsCancel)
    Platform.runLater() {
      $commit.requestFocus()
    }
  }

  private void loadResource() {
    FXMLLoader loader = FXMLLoader.new(getClass().getClassLoader().getResource(RESOURCE_PATH), null, CustomBuilderFactory.new())
    loader.setController(this)
    Parent root = (Parent)loader.load()
    $scene = Scene.new(root, DEFAULT_WIDTH, DEFAULT_HEIGHT)
    setScene($scene)
    sizeToScene()
  }

  public String getContentString() {
    return $contentString.get()
  }

  public void setContentString(String contentString) {
    $contentString.set(contentString)
  }

  public StringProperty contentStringProperty() {
    return $contentString
  }

  public String getCommitString() {
    return $commitString.get()
  }

  public void setCommitString(String commitString) {
    $commitString.set(commitString)
  }

  public StringProperty commitStringProperty() {
    return $commitString
  }

  public String getCancelString() {
    return $cancelString.get()
  }

  public void setCancelString(String cancelString) {
    $cancelString.set(cancelString)
  }

  public StringProperty cancelStringProperty() {
    return $cancelString
  }

  public Boolean isAllowsCancel() {
    return $allowsCancel.get()
  }

  public void setAllowsCancel(Boolean allowsCancel) {
    $allowsCancel.set(allowsCancel)
  }

  public BooleanProperty allowsCancelProperty() {
    return $allowsCancel
  }

}