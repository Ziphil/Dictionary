package ziphil.controller

import groovy.transform.CompileStatic
import javafx.fxml.FXML
import javafx.scene.control.RadioButton
import javafx.scene.control.TextArea
import javafx.scene.layout.VBox
import ziphil.custom.ExtensionFilter
import ziphil.custom.FileChooser
import ziphil.custom.Measurement
import ziphil.custom.UtilityStage
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class SnojChooserController extends Controller<Object> {

  private static final String RESOURCE_PATH = "resource/fxml/controller/snoj_chooser.fxml"
  private static final String TITLE = "参照"
  private static final Double DEFAULT_WIDTH = Measurement.rpx(640)
  private static final Double DEFAULT_HEIGHT = Measurement.rpx(480)

  @FXML private RadioButton $loadsFromFileControl
  @FXML private RadioButton $loadsFromStringControl
  @FXML private VBox $fileBox
  @FXML private VBox $stringBox
  @FXML private FileChooser $fileChooser
  @FXML private TextArea $stringControl

  public SnojChooserController(UtilityStage<Object> stage) {
    super(stage)
    loadResource(RESOURCE_PATH, TITLE, DEFAULT_WIDTH, DEFAULT_HEIGHT)
  }

  @FXML
  private void initialize() {
    setupBoxes()
    setupChooser()
  }

  @FXML
  protected void commit() {
    if ($loadsFromFileControl.isSelected()) {
      File file = $fileChooser.getSelectedFile()
      $stage.commit(file)
    } else if ($loadsFromStringControl.isSelected()) {
      String string = $stringControl.getText()
      $stage.commit(string)
    }
  }

  private void setupBoxes() {
    $fileBox.visibleProperty().bind($loadsFromFileControl.selectedProperty())
    $stringBox.visibleProperty().bind($loadsFromStringControl.selectedProperty())
  }

  private void setupChooser() {
    ExtensionFilter filter = ExtensionFilter.new("snojファイル", "snoj")
    $fileChooser.getExtensionFilters().add(filter)
    $fileChooser.setAdjustsExtension(false)
  }

}