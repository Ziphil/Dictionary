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
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class Dialog extends Stage {

  private static final String RESOURCE_PATH = "resource/fxml/dialog.fxml"
  private static final Double DEFAULT_WIDTH = Measurement.rpx(360)
  private static final Double DEFAULT_HEIGHT = -1

  @FXML private Label $contentLabel
  @FXML private Button $commitButton
  @FXML private Button $negateButton
  @FXML private Button $cancelButton
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
    bindButtonVisibleProperties()
    setupCommitButton()
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

  private void bindButtonVisibleProperties() {
    $negateButton.visibleProperty().bind($allowsNegate)
    $negateButton.managedProperty().bind($allowsNegate)
    $cancelButton.visibleProperty().bind($allowsCancel)
    $cancelButton.managedProperty().bind($allowsCancel)
  }

  private void setupCommitButton() {
    Platform.runLater() {
      $commitButton.requestFocus()
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
    return $contentLabel.getText()
  }

  public void setContentText(String contentText) {
    $contentLabel.setText(contentText)
  }

  public StringProperty contentTextProperty() {
    return $contentLabel.textProperty()
  }

  public String getCommitText() {
    return $commitButton.getText()
  }

  public void setCommitText(String commitText) {
    $commitButton.setText(commitText)
  }

  public StringProperty commitTextProperty() {
    return $commitButton.textProperty()
  }

  public String getNegateText() {
    return $negateButton.getText()
  }

  public void setNegateText(String negateText) {
    $negateButton.setText(negateText)
  }

  public StringProperty negateTextProperty() {
    return $negateButton.textProperty()
  }

  public String getCancelText() {
    return $cancelButton.getText()
  }

  public void setCancelText(String cancelText) {
    $cancelButton.setText(cancelText)
  }

  public StringProperty cancelTextProperty() {
    return $cancelButton.textProperty()
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