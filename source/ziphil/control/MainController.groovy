package ziphil.control

import groovy.transform.CompileStatic
import javafx.fxml.Initializable
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.Node
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.stage.Stage
import javafx.scene.control.ComboBox
import javafx.scene.control.ContextMenu
import javafx.scene.control.Label
import javafx.scene.control.ListView
import javafx.scene.control.MenuItem
import javafx.scene.control.TextField
import javafx.scene.control.ToggleButton
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.scene.input.MouseButton
import javafx.scene.input.MouseEvent
import javafx.scene.layout.HBox
import javafx.stage.Stage
import javafx.stage.StageStyle
import ziphil.control.ShaleiaEditorController
import ziphil.dictionary.ShaleiaWord
import ziphil.dictionary.Dictionary
import ziphil.dictionary.ShaleiaDictionary
import ziphil.dictionary.PersonalDictionary
import ziphil.dictionary.Word


@CompileStatic @Newify
public class MainController implements Initializable {

  private static final String RESOURCE_PATH = "resource/fxml/main.fxml"
  private static final String TITLE = "ZpDIC alpha"
  private static final String VERSION = "0.0.0α"
  private static final Integer DEFAULT_WIDTH = 720
  private static final Integer DEFAULT_HEIGHT = 720
  private static final Integer MIN_WIDTH = 360
  private static final Integer MIN_HEIGHT = 240

  @FXML private ListView<Word> $list
  @FXML private TextField $searchText
  @FXML private ComboBox $searchMode
  @FXML private ToggleButton $searchType

  @FXML private ContextMenu $editMenu
  @FXML private MenuItem $modifyMenuItem
  @FXML private MenuItem $removeMenuItem
  @FXML private MenuItem $addMenuItem
  @FXML private MenuItem $addInheritMenuItem

  @FXML private HBox $footer
  @FXML private Label $dictionaryName
  @FXML private Label $hitWordSize
  @FXML private Label $totalWordSize
  @FXML private Label $elapsedTime

  private Dictionary $dictionary

  private Stage $stage
  private Scene $scene

  public MainController(Stage stage) {
    $stage = stage
    loadResource()
  }

  public void initialize(URL location, ResourceBundle resources) {
    setupDictionary()
    setupList()
    setupFooter()
    changeSearchMode()
  }

  private void loadResource() {
    FXMLLoader loader = FXMLLoader.new(getClass().getClassLoader().getResource(RESOURCE_PATH))
    loader.setController(this)
    Parent root = (Parent)loader.load()
    $scene = Scene.new(root, DEFAULT_WIDTH, DEFAULT_HEIGHT)
    $stage.setScene($scene)
    $stage.setTitle("${TITLE} (ver ${VERSION})")
    $stage.setMinWidth(MIN_WIDTH)
    $stage.setMinHeight(MIN_HEIGHT)
    $stage.sizeToScene()
  }

  private void setupList() {
    $list.setCellFactory() { ListView<Word> list ->
      WordCell cell = WordCell.new()
      cell.setOnMouseClicked() { MouseEvent event ->
        if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
          modifyWord(cell.getItem())
        }
        if (event.getButton() == MouseButton.SECONDARY) {
          $editMenu.show(cell, event.getScreenX(), event.getScreenY())
          $modifyMenuItem.setOnAction() {
            modifyWord(cell.getItem())
          }
          $removeMenuItem.setOnAction() {
            removeWord(cell.getItem())
          }
          $addMenuItem.setOnAction() {
            addWord()
          }
          $addInheritMenuItem.setOnAction() {
            addInheritWord(cell.getItem())
          }
        }
      }
      return cell
    }
    $list.setId("dictionary-list")
  }

  private void setupDictionary() {
    $dictionary = PersonalDictionary.new()
    $totalWordSize.setText($dictionary.getRawWords().size().toString())
    $dictionaryName.setText($dictionary.getName())
    $list.setItems($dictionary.getWords())
  }

  private void setupFooter() {
    $footer.getChildren().each() { Node node ->
      if (node instanceof Label) {
        Label label = (Label)node
        label.getStyleClass().add("footer")
      }
    }
  }

  @FXML
  private void search() {
    Long beforeTime = System.nanoTime()
    String search = $searchText.getText()
    String searchMode = $searchMode.getValue()
    Boolean isStrict = $searchType.getText() == "完全一致"
    if (searchMode == "単語") {
      $dictionary.searchByName(search, isStrict)
    } else if (searchMode == "訳語") {
      $dictionary.searchByEquivalent(search, isStrict)
    } else if (searchMode == "全文") {
      $dictionary.searchByContent(search)
    }
    Long afterTime = System.nanoTime()
    Long elapsedTime = (Long)(afterTime - beforeTime).intdiv(1000000)
    Integer hitWordSize = $dictionary.getWords().size()
    Integer totalWordSize = $dictionary.getRawWords().size()
    $elapsedTime.setText(elapsedTime.toString())
    $hitWordSize.setText(hitWordSize.toString())
    $totalWordSize.setText(totalWordSize.toString())
    $list.scrollTo(0)
  }

  @FXML
  private void changeSearchMode() {
    String searchMode = $searchMode.getValue()
    if (searchMode == "単語") {
      $searchType.setDisable(false)
    } else if (searchMode == "訳語") {
      $searchType.setDisable(false)
    } else if (searchMode == "全文") {
      $searchType.setDisable(true)
    }
    $hitWordSize.setText($totalWordSize.getText())
    $searchText.setText("")
    $searchText.requestFocus()
    search()
  }

  @FXML
  private void toggleSearchType() {
    if ($searchType.isSelected()) {
      $searchType.setText("完全一致")
    } else {
      $searchType.setText("部分一致")
    }
    $searchText.requestFocus()
    search()
  }

  private void modifyWord(Word word) {
    UtilityStage<Boolean> stage = UtilityStage.new(StageStyle.UTILITY)
    stage.initOwner($stage)
    if (word instanceof ShaleiaWord) {
      ShaleiaEditorController controller = ShaleiaEditorController.new(stage)
      controller.prepare(word)
    }
    stage.showAndWait()
    if (stage.getResult() != null && stage.getResult()) {
      $dictionary.save()
    }
  }

  private void removeWord(Word word) {
    $dictionary.getRawWords().remove(word)
    $dictionary.save()
  }

  private void addWord() {
    Word newWord
    UtilityStage<Boolean> stage = UtilityStage.new(StageStyle.UTILITY)
    stage.initOwner($stage)
    if ($dictionary.getRawWords()[0] instanceof ShaleiaWord) {
      newWord = ShaleiaWord.new("* \n")
      ShaleiaEditorController controller = ShaleiaEditorController.new(stage)
      controller.prepare(newWord)
    }
    stage.showAndWait()
    if (stage.getResult() != null && stage.getResult()) {
      $dictionary.getRawWords().add(newWord)
      $dictionary.save()
    }
  }

  private void addInheritWord(Word word) {
    Word newWord
    UtilityStage<Boolean> stage = UtilityStage.new(StageStyle.UTILITY)
    stage.initOwner($stage)
    if (word instanceof ShaleiaWord) {
      newWord = ShaleiaWord.new(word.getData())
      ShaleiaEditorController controller = ShaleiaEditorController.new(stage)
      controller.prepare(newWord)
    }
    stage.showAndWait()
    if (stage.getResult() != null && stage.getResult()) {
      $dictionary.getRawWords().add(newWord)
      $dictionary.save()
    }
  }

  @FXML
  private void showDictionaryList() {
    Stage stage = Stage.new(StageStyle.UTILITY)
    stage.initOwner($stage)
    DictionaryTableController controller = DictionaryTableController.new(stage)
    stage.show()
  }

  public Scene getScene() {
    return $scene
  }

}