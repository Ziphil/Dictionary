package ziphil.controller

import groovy.transform.CompileStatic
import javafx.fxml.FXML
import javafx.scene.control.ListView
import javafx.scene.control.RadioButton
import javafx.scene.control.TextArea
import javafx.scene.control.TreeView
import javafx.scene.layout.VBox
import ziphil.custom.DirectoryCell
import ziphil.custom.ExtensionFilter
import ziphil.custom.FileCell
import ziphil.custom.FileChooser
import ziphil.custom.Measurement
import ziphil.custom.UtilityStage
import ziphilib.transform.InnerClass
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class SnojChooserController extends Controller<SnojChooserController.Result> {

  private static final String RESOURCE_PATH = "resource/fxml/controller/snoj_chooser.fxml"
  private static final String TITLE = "参照"
  private static final Double DEFAULT_WIDTH = Measurement.rpx(640)
  private static final Double DEFAULT_HEIGHT = Measurement.rpx(480)

  @FXML private RadioButton $fileSelectedControl
  @FXML private RadioButton $sourceSelectedControl
  @FXML private VBox $fileBox
  @FXML private VBox $sourceBox
  @FXML private FileChooser $fileChooser
  @FXML private TextArea $sourceControl

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
      String source = previousResult.getSource()
      if (file != null) {
        $fileChooser.setCurrentDirectory(file.getParentFile())
      }
      if (source != null) {
        $sourceControl.setText(source)
      }
      if (previousResult.isFileSelected()) {
        $fileSelectedControl.setSelected(true)
      } else {
        $sourceSelectedControl.setSelected(true)
      }
    }
  }

  @FXML
  protected void commit() {
    File file = $fileChooser.getSelectedFile()
    String source = $sourceControl.getText()
    Boolean fileSelected = $fileSelectedControl.isSelected()
    Result result = Result.new(file, source, fileSelected)
    $stage.commit(result)
  }

  private void setupBoxes() {
    $fileBox.visibleProperty().bind($fileSelectedControl.selectedProperty())
    $sourceBox.visibleProperty().bind($sourceSelectedControl.selectedProperty())
  }

  private void setupChooser() {
    ExtensionFilter filter = ExtensionFilter.new("snojファイル", "snoj")
    $fileChooser.getExtensionFilters().add(filter)
    $fileChooser.setCurrentFileType(filter)
    $fileChooser.setAdjustsExtension(false)
    $fileChooser.setDirectoryCellFactory() { TreeView<File> view ->
      return DirectoryCell.new()
    }
    $fileChooser.setFileCellFactory() { ListView<File> view ->
      return FileCell.new()
    }
  }

}


@InnerClass(SnojChooserController)
@Ziphilify
public static class Result {

  private File $file
  private String $source
  private Boolean $fileSelected

  public Result(File file, String source, Boolean fileSelected) {
    $file = file
    $source = source
    $fileSelected = fileSelected
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

  public String getSource() {
    return $source
  }

}