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

  public void prepare(Boolean adjustsExtension, File directory, DictionaryType type) {
    if (type != null) {
      List<ExtensionFilter> fileTypes = $chooser.getExtensionFilters()
      ExtensionFilter fileType = fileTypes.find{fileType -> ((ExtensionFilter)fileType).getExtension() == type.getExtension()}
      if (fileType != null) {
        $chooser.setCurrentFileType(fileType)
      }
    }
    if (directory != null) {
      if (directory.isDirectory()) {
        $chooser.setCurrentDirectory(directory)
      }
    }
    $chooser.setAdjustsExtension(adjustsExtension)
  }

  public void prepare(Boolean adjustsExtension) {
    prepare(adjustsExtension, null, null)
  }

  @FXML
  protected void commit() {
    File file = $chooser.getSelectedFile()
    $stage.commit(file)
  }

  private void setupChooser() {
    ExtensionFilter slimeFilter = ExtensionFilter.new("OneToMany-JSON形式", "json")
    ExtensionFilter personalFilter = ExtensionFilter.new("PDIC-CSV形式", "csv")
    ExtensionFilter shaleiaFilter = ExtensionFilter.new("シャレイア語辞典形式", "xdc")
    $chooser.getExtensionFilters().addAll(slimeFilter, personalFilter, shaleiaFilter)
  }

}