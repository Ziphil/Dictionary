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
public class SnojChooserController extends Controller<SnojChooserController.Result> {

  private static final String RESOURCE_PATH = "resource/fxml/controller/snoj_chooser.fxml"
  private static final String TITLE = "参照"
  private static final Double DEFAULT_WIDTH = Measurement.rpx(640)
  private static final Double DEFAULT_HEIGHT = Measurement.rpx(480)

  @FXML private RadioButton $isFileSelectedControl
  @FXML private RadioButton $isStringSelectedControl
  @FXML private VBox $fileBox
  @FXML private VBox $stringBox
  @FXML private FileChooser $fileChooser
  @FXML private TextArea $stringControl

  public SnojChooserController(UtilityStage<SnojChooserController.Result> stage) {
    super(stage)
    loadResource(RESOURCE_PATH, TITLE, DEFAULT_WIDTH, DEFAULT_HEIGHT)
  }

  @FXML
  private void initialize() {
    setupBoxes()
    setupChooser()
  }

  public void prepare(Result previousResult) {
    if (previousResult != null) {
      File file = previousResult.getFile()
      String string = previousResult.getString()
      if (file != null) {
        $fileChooser.setCurrentDirectory(file.getParentFile())
      }
      if (string != null) {
        $stringControl.setText(string)
      }
    }
    if (previousResult.isFileSelected()) {
      $isFileSelectedControl.setSelected(true)
    } else {
      $isStringSelectedControl.setSelected(true)
    }
  }

  @FXML
  protected void commit() {
    File file = $fileChooser.getSelectedFile()
    String string = $stringControl.getText()
    Boolean isFileSelected = $isFileSelectedControl.isSelected()
    Result result = Result.new(file, string, isFileSelected)
    $stage.commit(result)
  }

  private void setupBoxes() {
    $fileBox.visibleProperty().bind($isFileSelectedControl.selectedProperty())
    $stringBox.visibleProperty().bind($isStringSelectedControl.selectedProperty())
  }

  private void setupChooser() {
    ExtensionFilter filter = ExtensionFilter.new("snojファイル", "snoj")
    $fileChooser.getExtensionFilters().add(filter)
    $fileChooser.setCurrentFileType(filter)
    $fileChooser.setAdjustsExtension(false)
  }

}


@InnerClass(SnojChooserController)
public static class Result {

  private File $file
  private String $string
  private Boolean $isFileSelected

  public Result(File file, String string, Boolean isFileSelected) {
    $file = file
    $string = string
    $isFileSelected = isFileSelected
  }

  public Boolean isFileSelected() {
    return $isFileSelected
  }

  public Boolean isStringSelected() {
    return !$isFileSelected
  }

  public File getFile() {
    return $file
  }

  public String getString() {
    return $string
  }

}