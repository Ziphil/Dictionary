package ziphil.controller

import groovy.transform.CompileStatic
import java.util.regex.Matcher
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.control.TableView
import javafx.scene.control.TextField
import javafx.stage.Modality
import javafx.stage.StageStyle
import ziphil.dictionary.Dictionary
import ziphil.dictionary.ShaleiaDictionary
import ziphil.dictionary.PersonalDictionary
import ziphil.node.DictionaryTableModel
import ziphil.node.UtilityStage


@CompileStatic @Newify
public class DictionaryTableController {

  private static final String RESOURCE_PATH = "resource/fxml/dictionary_table.fxml"
  private static final String DICTIONARY_DATA_PATH = "data/dictionaries.zpdt"
  private static final String TITLE = "登録辞書一覧"
  private static final Integer DEFAULT_WIDTH = 640
  private static final Integer DEFAULT_HEIGHT = 320

  @FXML private TableView<DictionaryTableModel> $table
  private ObservableList<DictionaryTableModel> $dictionaries = FXCollections.observableArrayList()
  private UtilityStage<Dictionary> $stage
  private Scene $scene

  public DictionaryTableController(UtilityStage<Dictionary> stage) {
    $stage = stage
    loadResource()
  }

  @FXML
  public void initialize() {
    loadDictionaryData()
    setupTable()
  }

  @FXML
  private void loadNewDictionary() {
    UtilityStage<Boolean> stage = UtilityStage.new(StageStyle.UTILITY)
    DictionaryLoaderController controller = DictionaryLoaderController.new(stage)
    stage.initModality(Modality.WINDOW_MODAL)
    stage.initOwner($stage)
    Boolean isDone = stage.showAndWaitResult()
    if (isDone != null && isDone) {
      loadDictionaryData()
    }
  }

  @FXML
  private void commitShow() {
    DictionaryTableModel selectedModel = $table.getSelectionModel().getSelectedItem()
    if (selectedModel != null) {
      String type = selectedModel.getType()
      Dictionary dictionary
      if (type == "shaleia") {
        dictionary = ShaleiaDictionary.new(selectedModel.getName(), selectedModel.getPath())
      } else if (type == "personal") {
        dictionary = PersonalDictionary.new(selectedModel.getName(), selectedModel.getPath())
      }
      $stage.close(dictionary)
    } else {
      $stage.close()
    }
  }

  @FXML
  private void cancelShow() {
    $stage.close()
  }

  private void loadDictionaryData() {
    File file = File.new(DICTIONARY_DATA_PATH)
    $dictionaries.clear()
    if (file.exists()) {
      file.eachLine() { String line ->
        Matcher matcher = line =~ /^"(.*)",\s*"(.*)",\s*"(.*)"$/
        if (matcher.matches()) {
          DictionaryTableModel model = DictionaryTableModel.new(matcher.group(1), matcher.group(2), matcher.group(3))
          $dictionaries.add(model)
        }
      }
    }
  }

  private void setupTable() {
    $table.setItems($dictionaries)
  }

  private void loadResource() {
    FXMLLoader loader = FXMLLoader.new(getClass().getClassLoader().getResource(RESOURCE_PATH))
    loader.setController(this)
    Parent root = (Parent)loader.load()
    $scene = Scene.new(root, DEFAULT_WIDTH, DEFAULT_HEIGHT)
    $stage.setScene($scene)
    $stage.setTitle(TITLE)
    $stage.sizeToScene()
  }

}