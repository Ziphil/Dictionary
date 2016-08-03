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
  @FXML private Button $negate
  @FXML private Button $cancel
  private BooleanProperty $allowsNegate = SimpleBooleanProperty.new(false)
  private BooleanProperty $allowsCancel = SimpleBooleanProperty.new(true)
  private Boolean $result = false
  private Scene $scene

  public Dialog() {
    super(StageStyle.UTILITY)
    loadResource()
    setupStage()
  }

  public Dialog(String title) {
    this()
    setTitle(title)
  }

  public Dialog(String title, String contentText) {
    this()
    setTitle(title)
    setContentText(contentText)
  }

  @FXML
  private void initialize(){
    setupButtons()
  }

  public Boolean showAndWaitResult() {
    showAndWait()
    return $result
  }

  @FXML
  private void commit() {
    $result = true
    close()
  }

  @FXML
  private void negate() {
    $result = false
    close()
  }

  @FXML
  private void cancel() {
    $result = null
    close()
  }

  private void setupStage() {
    initModality(Modality.WINDOW_MODAL)
  }

  private void setupButtons() {
    $negate.visibleProperty().bind($allowsNegate)
    $negate.managedProperty().bind($allowsNegate)
    $cancel.visibleProperty().bind($allowsCancel)
    $cancel.managedProperty().bind($allowsCancel)
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

  public String getContentText() {
    return $content.getText()
  }

  public void setContentText(String contentText) {
    $content.setText(contentText)
  }

  public StringProperty contentTextProperty() {
    return $content.textProperty()
  }

  public String getCommitText() {
    return $commit.getText()
  }

  public void setCommitText(String commitText) {
    $commit.setText(commitText)
  }

  public StringProperty commitTextProperty() {
    return $commit.textProperty()
  }

  public String getNegateText() {
    return $negate.getText()
  }

  public void setNegateText(String negateText) {
    $negate.setText(negateText)
  }

  public StringProperty negateTextProperty() {
    return $negate.textProperty()
  }

  public String getCancelText() {
    return $cancel.getText()
  }

  public void setCancelText(String cancelText) {
    $cancel.setText(cancelText)
  }

  public StringProperty cancelTextProperty() {
    return $cancel.textProperty()
  }

  public Boolean isAllowsNegate() {
    return $allowsNegate.get()
  }

  public void setAllowsNegate(Boolean allowsNegate) {
    $allowsNegate.set(allowsNegate)
  }

  public BooleanProperty allowsNegateProperty() {
    return $allowsNegate
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