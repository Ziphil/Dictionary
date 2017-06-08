package ziphil.controller

import groovy.transform.CompileStatic
import javafx.fxml.FXML
import javafx.scene.control.ComboBox
import javafx.scene.control.ListView
import javafx.scene.control.TextArea
import javafx.scene.control.TextField
import javafx.scene.control.TreeView
import ziphil.custom.DirectoryCell
import ziphil.custom.ExtensionFilter
import ziphil.custom.FileCell
import ziphil.custom.FileChooser
import ziphil.custom.Measurement
import ziphil.custom.UtilityStage
import ziphil.dictionary.DictionaryType
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class DictionaryChooserController extends Controller<File> {

  private static final String RESOURCE_PATH = "resource/fxml/controller/dictionary_chooser.fxml"
  private static final String TITLE = "参照"
  private static final Double DEFAULT_WIDTH = Measurement.rpx(640)
  private static final Double DEFAULT_HEIGHT = Measurement.rpx(480)

  @FXML private FileChooser $chooser

  public DictionaryChooserController(UtilityStage<File> stage) {
    super(stage)
    loadResource(RESOURCE_PATH, TITLE, DEFAULT_WIDTH, DEFAULT_HEIGHT)
  }

  @FXML
  private void initialize() {
    setupChooser()
  }

  public void prepare(DictionaryType type, File directory, Boolean adjustsExtension) {
    if (type != null) {
      ExtensionFilter extensionFilter = type.createExtensionFilter()
      $chooser.getExtensionFilters().clear()
      $chooser.getExtensionFilters().add(extensionFilter)
      $chooser.setCurrentFileType(extensionFilter)
    }
    if (directory != null) {
      if (directory.isDirectory()) {
        $chooser.setCurrentDirectory(directory)
      }
    }
    $chooser.setAdjustsExtension(adjustsExtension)
  }

  public void prepare(Boolean adjustsExtension) {
    prepare(null, null, adjustsExtension)
  }

  @FXML
  protected void commit() {
    File file = $chooser.getSelectedFile()
    $stage.commit(file)
  }

  private void setupChooser() {
    for (DictionaryType type : DictionaryType.values()) {
      ExtensionFilter extensionFilter = type.createExtensionFilter()
      $chooser.getExtensionFilters().add(extensionFilter)
    }
    $chooser.setDirectoryCellFactory() { TreeView<File> view ->
      return DirectoryCell.new()
    }
    $chooser.setFileCellFactory() { ListView<File> view ->
      return FileCell.new()
    }
  }

}