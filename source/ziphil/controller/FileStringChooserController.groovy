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
import ziphilib.transform.InnerClass
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class FileStringChooserController extends Controller<FileStringChooserController.Result> {

  private static final String RESOURCE_PATH = "resource/fxml/controller/file_string_chooser.fxml"
  private static final String TITLE = "参照"
  private static final Double DEFAULT_WIDTH = Measurement.rpx(640)
  private static final Double DEFAULT_HEIGHT = Measurement.rpx(480)

  @FXML private RadioButton $fileSelectedControl
  @FXML private RadioButton $stringSelectedControl
  @FXML private VBox $fileBox
  @FXML private VBox $stringBox
  @FXML private FileChooser $fileChooser
  @FXML private TextArea $stringControl

  public FileStringChooserController(UtilityStage<? super FileStringChooserController.Result> stage) {
    super(stage)
    loadResource(RESOURCE_PATH, TITLE, DEFAULT_WIDTH, DEFAULT_HEIGHT)
  }

  @FXML
  private void initialize() {
    setupBoxes()
    setupChooser()
  }

  public void prepare(ExtensionFilter filter, Result previousResult) {
    if (previousResult != null) {
      File file = previousResult.getFile()
      String string = previousResult.getString()
      if (file != null) {
        $fileChooser.setCurrentDirectory(file.getParentFile())
      }
      if (string != null) {
        $stringControl.setText(string)
      }
      if (previousResult.isFileSelected()) {
        $fileSelectedControl.setSelected(true)
      } else {
        $stringSelectedControl.setSelected(true)
      }
    }
    if (filter != null) {  
      $fileChooser.getExtensionFilters().add(filter)
      $fileChooser.setCurrentFileType(filter)
    }
  }

  @FXML
  protected void commit() {
    File file = $fileChooser.getSelectedFile()
    String string = $stringControl.getText()
    Boolean fileSelected = $fileSelectedControl.isSelected()
    if (fileSelected) {
      Result result = Result.ofFile(file)
      $stage.commit(result)
    } else {
      Result result = Result.ofString(string)
      $stage.commit(result)
    }
  }

  private void setupBoxes() {
    $fileBox.visibleProperty().bind($fileSelectedControl.selectedProperty())
    $stringBox.visibleProperty().bind($stringSelectedControl.selectedProperty())
  }

  private void setupChooser() {
    $fileChooser.setAdjustsExtension(false)
  }

}


@InnerClass(FileStringChooserController)
@CompileStatic @Ziphilify
public static class Result {

  private File $file
  private String $string
  private Boolean $fileSelected

  private Result(File file, String string, Boolean fileSelected) {
    $file = file
    $string = string
    $fileSelected = fileSelected
  }

  public static Result ofFile(File file) {
    return Result.new(file, null, true)
  }

  public static Result ofString(String string) {
    return Result.new(null, string, false)
  }

  public Boolean isFileSelected() {
    return $fileSelected
  }

  public Boolean isStringSelected() {
    return !$fileSelected
  }

  public File getFile() {
    return $file
  }

  public String getString() {
    return $string
  }

}