package ziphil.controller

import groovy.transform.CompileStatic
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.control.TextArea
import javafx.scene.control.TextField
import ziphil.custom.CustomBuilderFactory
import ziphil.custom.ExtensionFilter
import ziphil.custom.FileChooser
import ziphil.custom.DirectoryItem
import ziphil.custom.Measurement
import ziphil.custom.UtilityStage
import ziphil.dictionary.ShaleiaWord


@CompileStatic @Newify
public class DictionaryChooserController {

  private static final String RESOURCE_PATH = "resource/fxml/dictionary_chooser.fxml"
  private static final String TITLE = "参照"
  private static final Double DEFAULT_WIDTH = Measurement.rpx(640)
  private static final Double DEFAULT_HEIGHT = Measurement.rpx(480)

  @FXML private FileChooser $chooser
  private UtilityStage<File> $stage
  private Scene $scene

  public DictionaryChooserController(UtilityStage<File> stage) {
    $stage = stage
    loadResource()
  }

  @FXML
  private void initialize() {
    ExtensionFilter shaleiaFilter = ExtensionFilter.new("シャレイア語辞典形式", "xdc")
    ExtensionFilter personalFilter = ExtensionFilter.new("PDIC-CSV形式", "csv")
    ExtensionFilter slimeFilter = ExtensionFilter.new("OneToMany-JSON形式", "json")
    $chooser.getExtensionFilters().addAll(shaleiaFilter, personalFilter, slimeFilter)
  }

  public void prepare(Boolean adjustsExtension) {
    $chooser.setAdjustsExtension(adjustsExtension)
  }

  @FXML
  private void commitChoose() {
    File file = $chooser.getSelectedFile()
    $stage.close(file)
  }

  @FXML
  private void cancelChoose() {
    $stage.close(null)
  }

  private void loadResource() {
    FXMLLoader loader = FXMLLoader.new(getClass().getClassLoader().getResource(RESOURCE_PATH), null, CustomBuilderFactory.new())
    loader.setController(this)
    Parent root = (Parent)loader.load()
    $scene = Scene.new(root, DEFAULT_WIDTH, DEFAULT_HEIGHT)
    $stage.setScene($scene)
    $stage.setTitle(TITLE)
    $stage.sizeToScene()
  }

}