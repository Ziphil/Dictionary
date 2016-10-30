package ziphil.controller

import groovy.transform.CompileStatic
import javafx.fxml.FXML
import javafx.scene.control.ComboBox
import javafx.scene.control.TextArea
import javafx.scene.control.TextField
import ziphil.custom.ExtensionFilter
import ziphil.custom.FileChooser
import ziphil.custom.Measurement
import ziphil.custom.UtilityStage
import ziphil.dictionary.ShaleiaWord


@CompileStatic @Newify
public class DictionaryChooserController extends Controller<File> {

  private static final String RESOURCE_PATH = "resource/fxml/dictionary_chooser.fxml"
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
    ExtensionFilter slimeFilter = ExtensionFilter.new("OneToMany-JSON形式", "json")
    ExtensionFilter personalFilter = ExtensionFilter.new("PDIC-CSV形式", "csv")
    ExtensionFilter shaleiaFilter = ExtensionFilter.new("シャレイア語辞典形式", "xdc")
    $chooser.getExtensionFilters().addAll(slimeFilter, personalFilter, shaleiaFilter)
  }

  public void prepare(Boolean adjustsExtension, String extension) {
    if (extension != null) {
      ComboBox<ExtensionFilter> fileTypeControl = $chooser.getFileTypeControl()
      Integer index = fileTypeControl.getItems().findIndexOf{filter -> ((ExtensionFilter)filter).getExtension() == extension}
      if (index >= 0) {
        fileTypeControl.getSelectionModel().select(index)
      }
    }
    $chooser.setAdjustsExtension(adjustsExtension)
  }

  public void prepare(Boolean adjustsExtension) {
    prepare(adjustsExtension, null)
  }

  @FXML
  protected void commit() {
    File file = $chooser.getSelectedFile()
    $stage.close(file)
  }

}