package ziphil.controller

import groovy.transform.CompileStatic
import java.awt.Desktop
import java.util.concurrent.Callable
import javafx.application.Platform
import javafx.beans.binding.Bindings
import javafx.beans.binding.BooleanBinding
import javafx.beans.binding.StringBinding
import javafx.fxml.FXML
import javafx.scene.Node
import javafx.scene.Scene
import javafx.scene.control.ComboBox
import javafx.scene.control.ContextMenu
import javafx.scene.control.Label
import javafx.scene.control.ListView
import javafx.scene.control.Menu
import javafx.scene.control.MenuItem
import javafx.scene.control.TextField
import javafx.scene.control.ToggleButton
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyCodeCombination
import javafx.scene.input.KeyCombination
import javafx.scene.input.KeyEvent
import javafx.scene.input.MouseButton
import javafx.scene.input.MouseEvent
import javafx.scene.layout.HBox
import javafx.stage.Stage
import javafx.stage.StageStyle
import javafx.stage.Modality
import javafx.stage.WindowEvent
import ziphil.Launcher
import ziphil.custom.Dialog
import ziphil.custom.Measurement
import ziphil.custom.UtilityStage
import ziphil.custom.WordCell
import ziphil.dictionary.Dictionary
import ziphil.dictionary.PersonalDictionary
import ziphil.dictionary.PersonalWord
import ziphil.dictionary.ShaleiaDictionary
import ziphil.dictionary.ShaleiaSearchParameter
import ziphil.dictionary.ShaleiaWord
import ziphil.dictionary.SlimeDictionary
import ziphil.dictionary.SlimeSearchParameter
import ziphil.dictionary.SlimeWord
import ziphil.dictionary.Word
import ziphil.module.Setting


@CompileStatic @Newify
public class MainController extends PrimitiveController<Stage> {

  private static final String RESOURCE_PATH = "resource/fxml/main.fxml"
  private static final String EXCEPTION_OUTPUT_PATH = "data/log/exception.txt"
  private static final String TITLE = "ZpDIC alpha"
  private static final Double DEFAULT_WIDTH = Measurement.rpx(720)
  private static final Double DEFAULT_HEIGHT = Measurement.rpx(720)
  private static final Double MIN_WIDTH = Measurement.rpx(360)
  private static final Double MIN_HEIGHT = Measurement.rpx(240)

  @FXML private ListView<? extends Word> $wordList
  @FXML private TextField $searchText
  @FXML private ComboBox<String> $searchMode
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
  private Boolean $isDictionaryChanged = false

  public MainController(Stage stage) {
    super(stage)
    loadResource(RESOURCE_PATH, TITLE, DEFAULT_WIDTH, DEFAULT_HEIGHT, MIN_WIDTH, MIN_HEIGHT)
    setupShortcuts()
    setupCloseConfirmation()
    setupExceptionHandler()
  }

  @FXML
  public void initialize() {
    setupWordList()
    setupSearchType()
    setupOpenRegisteredDictionaryMenu()
    setupWordListShortcuts()
    updateDictionaryToDefault()
  }

  @FXML
  private void search(KeyEvent event) {
    if ($dictionary != null) {
      if (event == null || event.getCode() != KeyCode.ENTER) {
        measureDictionaryStatus() {
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
        }
      }
    }
  }

  private void search() {
    search(null)
  }

  @FXML
  private void searchDetail() {
    if ($dictionary != null) {
      if ($dictionary instanceof ShaleiaDictionary) {
        UtilityStage<ShaleiaSearchParameter> stage = UtilityStage.new(StageStyle.UTILITY)
        ShaleiaSearcherController controller = ShaleiaSearcherController.new(stage)
        stage.initOwner($stage)
        ShaleiaSearchParameter parameter = stage.showAndWaitResult()
        if (parameter != null) {
          measureDictionaryStatus() {
            $dictionary.searchDetail(parameter)
          }
        }
      } else if ($dictionary instanceof SlimeDictionary) {
        UtilityStage<SlimeSearchParameter> stage = UtilityStage.new(StageStyle.UTILITY)
        SlimeSearcherController controller = SlimeSearcherController.new(stage)
        stage.initOwner($stage)
        controller.prepare($dictionary)
        SlimeSearchParameter parameter = stage.showAndWaitResult()
        if (parameter != null) {
          measureDictionaryStatus() {
            $dictionary.searchDetail(parameter) 
          }
        }
      }
    }
  }

  private void measureDictionaryStatus(Runnable searchFunction) {
    Long beforeTime = System.nanoTime()
    searchFunction.run()
    Long afterTime = System.nanoTime()
    Long elapsedTime = (Long)(afterTime - beforeTime).intdiv(1000000)
    Integer hitWordSize = $dictionary.getWords().size()
    Integer totalWordSize = $dictionary.getRawWords().size()
    $elapsedTime.setText(elapsedTime.toString())
    $hitWordSize.setText(hitWordSize.toString())
    $totalWordSize.setText(totalWordSize.toString())
    $wordList.scrollTo(0)
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
    if ($dictionary != null) {
      UtilityStage<Boolean> stage = UtilityStage.new(StageStyle.UTILITY)
      Boolean savesAutomatically = Setting.getInstance().getSavesAutomatically()
      Word oldWord = $dictionary.copiedWord(word)
      stage.initOwner($stage)
      if ($dictionary instanceof ShaleiaDictionary) {
        ShaleiaEditorController controller = ShaleiaEditorController.new(stage)
        controller.prepare((ShaleiaWord)word)
      } else if ($dictionary instanceof PersonalDictionary) {
        PersonalEditorController controller = PersonalEditorController.new(stage)
        controller.prepare((PersonalWord)word)
      } else if ($dictionary instanceof SlimeDictionary) {
        SlimeEditorController controller = SlimeEditorController.new(stage)
        controller.prepare((SlimeWord)word, $dictionary)
      }
      Boolean isDone = stage.showAndWaitResult()
      if (isDone != null && isDone) {
        $dictionary.modifyWord(oldWord, word)
        if (savesAutomatically) {
          $dictionary.save()
          $isDictionaryChanged = false
        } else {
          $isDictionaryChanged = true
        }
      }
    }
  }

  @FXML
  private void modifyWord() {
    Word word = $wordList.getSelectionModel().getSelectedItems()[0]
    if (word != null) {
      modifyWord(word)
    }
  }

  private void removeWord(Word word) {
    if ($dictionary != null) {
      Boolean savesAutomatically = Setting.getInstance().getSavesAutomatically()
      $dictionary.removeWord(word)
      if (savesAutomatically) {
        $dictionary.save()
        $isDictionaryChanged = false
      } else {
        $isDictionaryChanged = true
      }
    }
  }

  @FXML
  private void removeWord() {
    Word word = $wordList.getSelectionModel().getSelectedItems()[0]
    if (word != null) {
      removeWord(word)
    }
  }

  @FXML
  private void addWord() {
    if ($dictionary != null) {
      Word newWord
      UtilityStage<Boolean> stage = UtilityStage.new(StageStyle.UTILITY)
      Boolean savesAutomatically = Setting.getInstance().getSavesAutomatically()
      stage.initOwner($stage)
      if ($dictionary instanceof ShaleiaDictionary) {
        ShaleiaDictionary castedDictionary = (ShaleiaDictionary)$dictionary
        ShaleiaEditorController controller = ShaleiaEditorController.new(stage)
        newWord = castedDictionary.emptyWord()
        controller.prepare(newWord)
      } else if ($dictionary instanceof PersonalDictionary) {
        PersonalDictionary castedDictionary = (PersonalDictionary)$dictionary
        PersonalEditorController controller = PersonalEditorController.new(stage)
        newWord = castedDictionary.emptyWord()
        controller.prepare(newWord)
      } else if ($dictionary instanceof SlimeDictionary) {
        SlimeDictionary castedDictionary = (SlimeDictionary)$dictionary
        SlimeEditorController controller = SlimeEditorController.new(stage)
        newWord = castedDictionary.emptyWord()
        controller.prepare(newWord, $dictionary)
      }
      Boolean isDone = stage.showAndWaitResult()
      if (isDone != null && isDone) {
        $dictionary.addWord(newWord)
        if (savesAutomatically) {
          $dictionary.save()
          $isDictionaryChanged = false
        } else {
          $isDictionaryChanged = true
        }
      }
    }
  }

  private void addInheritedWord(Word word) {
    if ($dictionary != null) {
      Word newWord
      UtilityStage<Boolean> stage = UtilityStage.new(StageStyle.UTILITY)
      Boolean savesAutomatically = Setting.getInstance().getSavesAutomatically()
      stage.initOwner($stage)
      if ($dictionary instanceof ShaleiaDictionary) {
        ShaleiaDictionary castedDictionary = (ShaleiaDictionary)$dictionary
        ShaleiaEditorController controller = ShaleiaEditorController.new(stage)
        newWord = castedDictionary.inheritedWord((ShaleiaWord)word)
        controller.prepare(newWord)
      } else if ($dictionary instanceof PersonalDictionary) {
        PersonalDictionary castedDictionary = (PersonalDictionary)$dictionary
        PersonalEditorController controller = PersonalEditorController.new(stage)
        newWord = castedDictionary.inheritedWord((PersonalWord)word)
        controller.prepare(newWord)
      } else if ($dictionary instanceof SlimeDictionary) {
        SlimeDictionary castedDictionary = (SlimeDictionary)$dictionary
        SlimeEditorController controller = SlimeEditorController.new(stage)
        newWord = castedDictionary.inheritedWord((SlimeWord)word)
        controller.prepare(newWord, $dictionary)
      }
      Boolean isDone = stage.showAndWaitResult()
      if (isDone != null && isDone) {
        $dictionary.addWord(newWord)
        if (savesAutomatically) {
          $dictionary.save()
          $isDictionaryChanged = false
        } else {
          $isDictionaryChanged = true
        }
      }
    }
  }

  @FXML
  private void addInheritedWord() {
    Word word = $wordList.getSelectionModel().getSelectedItems()[0]
    if (word != null) {
      addInheritedWord(word)
    }
  }

  @FXML
  private void openDictionary() {
    Boolean allowsOpen = checkDictionaryChange()
    if (allowsOpen) {
      UtilityStage<File> stage = UtilityStage.new(StageStyle.UTILITY)
      DictionaryChooserController controller = DictionaryChooserController.new(stage)
      stage.initModality(Modality.WINDOW_MODAL)
      stage.initOwner($stage)
      File file = stage.showAndWaitResult()
      if (file != null && file.isFile()) {
        Dictionary dictionary = Dictionary.loadDictionary(file)
        if (dictionary != null) {
          updateDictionary(dictionary)
          $isDictionaryChanged = false
          Setting.getInstance().setDefaultDictionaryPath(file.getAbsolutePath())
        }
      }
    }
  }

  private void openRegisteredDictionary(File file) {
    Boolean allowsOpen = checkDictionaryChange()
    if (allowsOpen) {
      Dictionary dictionary = Dictionary.loadDictionary(file)
      updateDictionary(dictionary)
      $isDictionaryChanged = false
      Setting.getInstance().setDefaultDictionaryPath(file.getAbsolutePath())
    }
  }

  @FXML
  private void createDictionary() {
    Boolean allowsCreate = checkDictionaryChange()
    if (allowsCreate) {
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
          $isDictionaryChanged = true
          Setting.getInstance().setDefaultDictionaryPath(file.getAbsolutePath())
        }
      }
    }
  }

  @FXML
  private void saveDictionary() {
    if ($dictionary != null) {
      $dictionary.save()
      $isDictionaryChanged = false
    }
  }

  @FXML
  private void saveAndRenameDictionary() {
    if ($dictionary != null) {
      UtilityStage<File> stage = UtilityStage.new(StageStyle.UTILITY)
      DictionaryChooserController controller = DictionaryChooserController.new(stage)
      stage.initModality(Modality.WINDOW_MODAL)
      stage.initOwner($stage)
      controller.prepare(true)
      File file = stage.showAndWaitResult()
      if (file != null) {
        $dictionary.setPath(file.getAbsolutePath())
        $dictionary.save()
        $isDictionaryChanged = false
        Setting.getInstance().setDefaultDictionaryPath(file.getAbsolutePath())
      }
    }
  }

  private void focusWordList() {
    $wordList.requestFocus()
    if ($wordList.getSelectionModel().getSelectedItems().isEmpty()) {
      $wordList.getSelectionModel().selectFirst()
      $wordList.scrollTo(0)
    }
  }

  private Boolean checkDictionaryChange() {
    if ($dictionary != null) {
      if ($isDictionaryChanged) {
        Dialog dialog = Dialog.new("確認", "辞書は変更されています。保存しますか?")
        dialog.initOwner($stage)
        dialog.setCommitString("保存する")
        dialog.setNegateString("保存しない")
        dialog.setAllowsNegate(true)
        Boolean result = dialog.showAndWaitResult()
        if (result == true) {
          $dictionary.save()
          return true
        } else if (result == false) {
          return true
        } else {
          return false
        }
      } else {
        return true
      }
    } else {
      return true
    }
  }

  private void handleException(Throwable throwable) {
    PrintStream stream = PrintStream.new(Launcher.BASE_PATH + EXCEPTION_OUTPUT_PATH)
    String name = throwable.getClass().getSimpleName()
    Dialog dialog = Dialog.new("エラー", "エラーが発生しました(${name})。詳細はエラーログを確認してください。")
    dialog.initOwner($stage)
    dialog.setAllowsCancel(false)
    throwable.printStackTrace(stream)
    dialog.showAndWait()
    Platform.exit()
  }

  @FXML
  private void showHelp() {
    UtilityStage<Void> stage = UtilityStage.new(StageStyle.UTILITY)
    HelpController controller = HelpController.new(stage)
    stage.initModality(Modality.WINDOW_MODAL)
    stage.initOwner($stage)
    stage.showAndWait()
  }

  @FXML
  private void showOfficialSite() {
    Desktop desktop = Desktop.getDesktop()
    URI uri = URI.new("http://ziphil.s2.adexd.net/application/download/2.html")
    desktop.browse(uri)
  }

  @FXML
  private void showApplicationInformation() {
    UtilityStage<Void> stage = UtilityStage.new(StageStyle.UTILITY)
    ApplicationInformationController controller = ApplicationInformationController.new(stage)
    stage.initModality(Modality.WINDOW_MODAL)
    stage.initOwner($stage)
    stage.showAndWait()
  }

  @FXML
  private void showSetting() {
    UtilityStage<Void> stage = UtilityStage.new(StageStyle.UTILITY)
    SettingController controller = SettingController.new(stage)
    stage.initModality(Modality.WINDOW_MODAL)
    stage.initOwner($stage)
    stage.showAndWait() 
  }

  private void updateDictionary(Dictionary dictionary) {
    $dictionary = dictionary
    $totalWordSize.setText($dictionary.getRawWords().size().toString())
    $dictionaryName.setText($dictionary.getName())
    $wordList.setItems($dictionary.getWords())
    $searchText.setText("")
    $searchText.requestFocus()
    if ($dictionary instanceof ShaleiaDictionary) {
      $dictionary.setOnLinkClicked() { String name ->
        $searchMode.setValue("単語")
        $searchType.setSelected(true)
        $searchText.setText(name)
        $searchText.requestFocus()
        search()
      }
    }
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

  private void setupWordList() {
    $wordList.setCellFactory() { ListView<Word> list ->
      WordCell cell = WordCell.new()
      cell.setOnMouseClicked() { MouseEvent event ->
        if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
          modifyWord(cell.getItem())
        }
        if (event.getButton() == MouseButton.SECONDARY) {
          cell.setContextMenu($editMenu)
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
          openRegisteredDictionary(file)
        }
      } else {
        item.setText("")
        item.setDisable(true)
      }
      Image icon = Image.new(getClass().getClassLoader().getResourceAsStream("resource/icon/dictionary_${(i + 1) % 10}.png"))
      item.setGraphic(ImageView.new(icon))
      item.setAccelerator(KeyCodeCombination.new(KeyCode.valueOf("DIGIT${(i + 1) % 10}"), KeyCombination.SHORTCUT_DOWN))
      $openRegisteredDictionaryMenu.getItems().add(item)
    }
  }

  private void setupWordListShortcuts() {
    $wordList.setOnKeyPressed() { KeyEvent event ->
      if (event.getCode() == KeyCode.ENTER) {
        modifyWord()
      }
    }
  }

  private void setupShortcuts() {
    $scene.setOnKeyPressed() { KeyEvent event ->
      if (KeyCodeCombination.new(KeyCode.L, KeyCombination.SHORTCUT_DOWN).match(event)) {
        focusWordList()
      }
    }
  }

  private void setupCloseConfirmation() {
    $stage.setOnCloseRequest() { WindowEvent event ->
      Boolean allowsClose = checkDictionaryChange()
      if (!allowsClose) {
        event.consume()
      }
    }
  }

  private void setupExceptionHandler() {
    Thread.currentThread().setUncaughtExceptionHandler() { Thread thread, Throwable throwable ->
      handleException(throwable)
    }
  }

}