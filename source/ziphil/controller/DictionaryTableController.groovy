package ziphil.controller

import groovy.transform.CompileStatic
import java.util.regex.Matcher
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.fxml.Initializable
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.control.TableView
import javafx.stage.Stage
import ziphil.node.DictionaryTableModel


@CompileStatic @Newify
public class DictionaryTableController implements Initializable {

  private static final String RESOURCE_PATH = "resource/fxml/dictionary_list.fxml"
  private static final String DATA_PATH = "data/dictionaries.txt"
  private static final String TITLE = "登録辞書一覧"
  private static Integer DEFAULT_WIDTH = 640
  private static Integer DEFAULT_HEIGHT = 320

  @FXML private TableView<DictionaryTableModel> $table

  public ObservableList<DictionaryTableModel> $dictionaries = FXCollections.observableArrayList()
  
  private Stage $stage
  private Scene $scene

  public DictionaryTableController(Stage stage) {
    $stage = stage
    loadResource()
  }

  public void initialize(URL location, ResourceBundle resources) {
    loadDictionaryData()
    setupTable()
  }

  private void loadDictionaryData() {
    File file = File.new(DATA_PATH)
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