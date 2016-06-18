package ziphil.controller

import groovy.transform.CompileStatic
import java.util.concurrent.Callable
import javafx.application.Platform
import javafx.beans.binding.Bindings
import javafx.beans.binding.BooleanBinding
import javafx.beans.binding.StringBinding
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.Node
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.control.ComboBox
import javafx.scene.control.ContextMenu
import javafx.scene.control.Label
import javafx.scene.control.ListView
import javafx.scene.control.Menu
import javafx.scene.control.MenuItem
import javafx.scene.control.TextField
import javafx.scene.control.ToggleButton
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyCombination
import javafx.scene.input.KeyEvent
import javafx.scene.input.MouseButton
import javafx.scene.input.MouseEvent
import javafx.scene.layout.HBox
import javafx.stage.Stage
import javafx.stage.StageStyle
import javafx.stage.Modality
import ziphil.Launcher
import ziphil.custom.CustomBuilderFactory
import ziphil.custom.Measurement
import ziphil.dictionary.ShaleiaWord
import ziphil.dictionary.PersonalWord
import ziphil.dictionary.Dictionary
import ziphil.dictionary.DictionaryType
import ziphil.dictionary.ShaleiaDictionary
import ziphil.dictionary.PersonalDictionary
import ziphil.dictionary.Word
import ziphil.module.DictionarySetting
import ziphil.module.Setting
import ziphil.node.UtilityStage
import ziphil.node.WordCell


@CompileStatic @Newify
public class MainController {

  private static final String RESOURCE_PATH = "resource/fxml/main.fxml"
  private static final Double DEFAULT_WIDTH = Measurement.rpx(720)
  private static final Double DEFAULT_HEIGHT = Measurement.rpx(720)
  private static final Double MIN_WIDTH = Measurement.rpx(360)
  private static final Double MIN_HEIGHT = Measurement.rpx(240)

  @FXML private ListView<Word> $list
  @FXML private TextField $searchText
  @FXML private ComboBox $searchMode
  @FXML private ToggleButton $searchType
  @FXML private ContextMenu $editMenu
  @FXML private MenuItem $modifyWordItem
  @FXML private MenuItem $removeWordItem
  @FXML private MenuItem $addWordItem
  @FXML private MenuItem $addInheritedWordItem
  @FXML private Menu $openRegisteredDictionaryMenu
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

  @FXML
  public void initialize() {
    setupList()
    setupSearchType()
    setupOpenRegisteredDictionaryMenu()
    updateDictionaryToDefault()
  }

  @FXML
  private void search() {
    if ($dictionary != null) {
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
  }

  @FXML
  private void changeSearchMode() {
    $hitWordSize.setText($totalWordSize.getText())
    $searchText.setText("")
    $searchText.requestFocus()
    search()
  }

  @FXML
  private void changeSearchModeToWord() {
    $searchMode.setValue("単語")
    changeSearchMode()
  }

  @FXML
  private void changeSearchModeToEquivalent() {
    $searchMode.setValue("訳語")
    changeSearchMode()
  }

  @FXML
  private void changeSearchModeToContent() {
    $searchMode.setValue("全文")
    changeSearchMode()
  }

  @FXML
  private void changeSearchType() {
    if (!$searchType.isDisable()) {
      $searchType.setSelected(!$searchType.isSelected())
    }
  }

  @FXML
  private void toggleSearchType() {
    $searchText.requestFocus()
    search()
  }

  private void modifyWord(Word word) {
    UtilityStage<Boolean> stage = UtilityStage.new(StageStyle.UTILITY)
    DictionaryType dictionaryType = $dictionary.getType()
    Boolean savesAutomatically = Setting.getInstance().savesAutomatically()
    stage.initOwner($stage)
    if (dictionaryType == DictionaryType.SHALEIA) {
      ShaleiaEditorController controller = ShaleiaEditorController.new(stage)
      controller.prepare((ShaleiaWord)word)
    } else if (dictionaryType == DictionaryType.PERSONAL) {
      PersonalEditorController controller = PersonalEditorController.new(stage)
      controller.prepare((PersonalWord)word)
    }
    Boolean isDone = stage.showAndWaitResult()
    if (isDone != null && isDone) {
      if (savesAutomatically) {
        $dictionary.save()
      }
    }
  }

  private void removeWord(Word word) {
    Boolean savesAutomatically = Setting.getInstance().savesAutomatically()
    $dictionary.getRawWords().remove(word)
    if (savesAutomatically) {
      $dictionary.save()
    }
  }

  @FXML
  private void addWord() {
    Word newWord
    UtilityStage<Boolean> stage = UtilityStage.new(StageStyle.UTILITY)
    DictionaryType dictionaryType = $dictionary.getType()
    Boolean savesAutomatically = Setting.getInstance().savesAutomatically()
    stage.initOwner($stage)
    if (dictionaryType == DictionaryType.SHALEIA) {
      newWord = ShaleiaWord.emptyWord()
      ShaleiaEditorController controller = ShaleiaEditorController.new(stage)
      controller.prepare(newWord)
    } else if (dictionaryType == DictionaryType.PERSONAL) {
      newWord = PersonalWord.emptyWord()
      PersonalEditorController controller = PersonalEditorController.new(stage)
      controller.prepare(newWord)
    }
    Boolean isDone = stage.showAndWaitResult()
    if (isDone != null && isDone) {
      $dictionary.getRawWords().add(newWord)
      if (savesAutomatically) {
        $dictionary.save()
      }
    }
  }

  private void addInheritedWord(Word word) {
    Word newWord
    UtilityStage<Boolean> stage = UtilityStage.new(StageStyle.UTILITY)
    DictionaryType dictionaryType = $dictionary.getType()
    Boolean savesAutomatically = Setting.getInstance().savesAutomatically()
    stage.initOwner($stage)
    if (dictionaryType == DictionaryType.SHALEIA) {
      newWord = ShaleiaWord.copyFrom((ShaleiaWord)word)
      ShaleiaEditorController controller = ShaleiaEditorController.new(stage)
      controller.prepare(newWord)
    } else if (dictionaryType == DictionaryType.PERSONAL) {
      newWord = PersonalWord.copyFrom((PersonalWord)word)
      PersonalEditorController controller = PersonalEditorController.new(stage)
      controller.prepare(newWord)
    }
    Boolean isDone = stage.showAndWaitResult()
    if (isDone != null && isDone) {
      $dictionary.getRawWords().add(newWord)
      if (savesAutomatically) {
        $dictionary.save()
      }
    }
  }

  @FXML
  private void openDictionary() {
    UtilityStage<File> stage = UtilityStage.new(StageStyle.UTILITY)
    DictionaryChooserController controller = DictionaryChooserController.new(stage)
    stage.initModality(Modality.WINDOW_MODAL)
    stage.initOwner($stage)
    File file = stage.showAndWaitResult()
    if (file != null && file.isFile()) {
      Dictionary dictionary = Dictionary.loadDictionary(file)
      if (dictionary != null) {
        updateDictionary(dictionary)
        Setting.getInstance().setDefaultDictionaryPath(file.getAbsolutePath())
      }
    }
  }

  @FXML
  private void createDictionary() {
    UtilityStage<File> stage = UtilityStage.new(StageStyle.UTILITY)
    DictionaryChooserController controller = DictionaryChooserController.new(stage)
    stage.initModality(Modality.WINDOW_MODAL)
    stage.initOwner($stage)
    controller.prepare(true)
    File file = stage.showAndWaitResult()
    if (file != null) {
      Dictionary dictionary = Dictionary.loadEmptyDictionary(file)
      if (dictionary != null) {
        updateDictionary(dictionary)
        Setting.getInstance().setDefaultDictionaryPath(file.getAbsolutePath())
      }
    }
  }

  @FXML
  private void saveDictionary() {
    if ($dictionary != null) {
      $dictionary.save()
    }
  }

  @FXML
  private void saveAndRenameDictionary() {
    if ($dictionary != null)
      UtilityStage<File> stage = UtilityStage.new(StageStyle.UTILITY)
      DictionaryChooserController controller = DictionaryChooserController.new(stage)
      stage.initModality(Modality.WINDOW_MODAL)
      stage.initOwner($stage)
      controller.prepare(true)
      File file = stage.showAndWaitResult()
      if (file != null) {
        $dictionary.setPath(file.getAbsolutePath())
        $dictionary.save()
      }
    }
  }

  @FXML
  private void showApplicationInformation() {
    Stage stage = Stage.new(StageStyle.UTILITY)
    ApplicationInformationController controller = ApplicationInformationController.new(stage)
    stage.initModality(Modality.WINDOW_MODAL)
    stage.initOwner($stage)
    stage.showAndWait()
  }

  @FXML
  private void showSetting() {
    Stage stage = Stage.new(StageStyle.UTILITY)
    SettingController controller = SettingController.new(stage)
    stage.initModality(Modality.WINDOW_MODAL)
    stage.initOwner($stage)
    stage.showAndWait() 
  }

  private void updateDictionary(Dictionary dictionary) {
    $dictionary = dictionary
    $totalWordSize.setText($dictionary.getRawWords().size().toString())
    $dictionaryName.setText($dictionary.getName())
    $list.setItems($dictionary.getWords())
    $searchText.setText("")
    $searchText.requestFocus()
    search()
  }

  private void updateDictionaryToDefault() {
    String filePath = Setting.getInstance().getDefaultDictionaryPath()
    if (filePath != null) {
      File file = File.new(filePath)
      if (file.exists() && file.isFile()) {
        Dictionary dictionary = Dictionary.loadDictionary(file)
        if (dictionary != null) {
          updateDictionary(dictionary)
        }
      }
    }
  }

  @FXML
  private void exit() {
    Platform.exit()
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
          $modifyWordItem.setOnAction() {
            modifyWord(cell.getItem())
          }
          $removeWordItem.setOnAction() {
            removeWord(cell.getItem())
          }
          $addWordItem.setOnAction() {
            addWord()
          }
          $addInheritedWordItem.setOnAction() {
            addInheritedWord(cell.getItem())
          }
        }
      }
      return cell
    }
  }

  private void setupSearchType() {
    Callable<String> textFunction = (Callable){
      return ($searchType.selectedProperty().get()) ? "完全一致" : "部分一致"
    }
    Callable<Boolean> disableFunction = (Callable){
      String searchMode = $searchMode.getValue()
      if (searchMode == "単語") {
        return false
      } else if (searchMode == "訳語") {
        return false
      } else if (searchMode == "全文") {
        return true
      } else {
        return true
      }
    }
    StringBinding textBinding = Bindings.createStringBinding(textFunction, $searchType.selectedProperty())
    BooleanBinding disableBinding = Bindings.createBooleanBinding(disableFunction, $searchMode.valueProperty())    
    $searchType.textProperty().bind(textBinding)
    $searchType.disableProperty().bind(disableBinding)
  }

  private void setupOpenRegisteredDictionaryMenu() {
    List<String> dictionaryPaths = Setting.getInstance().getRegisteredDictionaryPaths()
    (0 ..< 10).each() { Integer i ->
      String dictionaryPath = dictionaryPaths[i]
      MenuItem item = MenuItem.new()
      if (dictionaryPath != null) {
        File file = File.new(dictionaryPath)
        item.setText(file.getName())
        item.setOnAction() {
          Dictionary dictionary = Dictionary.loadDictionary(file)
          updateDictionary(dictionary)
          Setting.getInstance().setDefaultDictionaryPath(file.getAbsolutePath())
        }
      } else {
        item.setText("")
        item.setDisable(true)
      }
      item.setAccelerator(KeyCombination.valueOf("Shortcut+${(i + 1) % 10}"))
      $openRegisteredDictionaryMenu.getItems().add(item)
    }
  }

  private void loadResource() {
    FXMLLoader loader = FXMLLoader.new(getClass().getClassLoader().getResource(RESOURCE_PATH), null, CustomBuilderFactory.new())
    loader.setController(this)
    Parent root = (Parent)loader.load()
    $scene = Scene.new(root, DEFAULT_WIDTH, DEFAULT_HEIGHT)
    $stage.setScene($scene)
    $stage.setTitle("${Launcher.TITLE} (ver ${Launcher.VERSION})")
    $stage.setMinWidth(MIN_WIDTH)
    $stage.setMinHeight(MIN_HEIGHT)
    $stage.sizeToScene()
  }

  public Scene getScene() {
    return $scene
  }

}