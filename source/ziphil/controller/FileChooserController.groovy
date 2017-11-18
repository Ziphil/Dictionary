package ziphil.controller

import groovy.transform.CompileStatic
import javafx.fxml.FXML
import javafx.scene.control.ComboBox
import javafx.scene.control.ListView
import javafx.scene.control.TextArea
import javafx.scene.control.TextField
import ziphil.custom.ExtensionFilter
import ziphil.custom.DictionaryFileCell
import ziphil.custom.FileChooser
import ziphil.custom.Measurement
import ziphil.custom.UtilityStage
import ziphil.dictionary.DictionaryType
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class FileChooserController extends Controller<File> {

  private static final String RESOURCE_PATH = "resource/fxml/controller/file_chooser.fxml"
  private static final String TITLE = "参照"
  private static final Double DEFAULT_WIDTH = Measurement.rpx(640)
  private static final Double DEFAULT_HEIGHT = Measurement.rpx(480)

  @FXML private FileChooser $chooser

  public FileChooserController(UtilityStage<? super File> stage) {
    super(stage)
    loadResource(RESOURCE_PATH, TITLE, DEFAULT_WIDTH, DEFAULT_HEIGHT)
  }

  public void prepare(List<ExtensionFilter> extensionFilters, ExtensionFilter defaultFileType, Boolean adjustsExtension) {
    $chooser.getExtensionFilters().addAll(extensionFilters)
    if (defaultFileType != null) {
      $chooser.setCurrentFileType(defaultFileType)
    }
    $chooser.setAdjustsExtension(adjustsExtension)
  }

  public void prepare(Boolean adjustsExtension) {
    prepare(ArrayList.new(), null, adjustsExtension)
  }

  @FXML
  protected void commit() {
    File file = $chooser.getSelectedFile()
    $stage.commit(file)
  }

}