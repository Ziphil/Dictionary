package ziphil.controller

import groovy.transform.CompileStatic
import java.awt.Desktop
import java.lang.Thread.UncaughtExceptionHandler
import java.util.concurrent.Callable
import javafx.application.Platform
import javafx.beans.binding.Bindings
import javafx.beans.binding.BooleanBinding
import javafx.beans.binding.StringBinding
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.concurrent.Task
import javafx.concurrent.Worker
import javafx.concurrent.WorkerStateEvent
import javafx.fxml.FXML
import javafx.scene.Node
import javafx.scene.Scene
import javafx.scene.control.ComboBox
import javafx.scene.control.ContextMenu
import javafx.scene.control.Label
import javafx.scene.control.ListView
import javafx.scene.control.Menu
import javafx.scene.control.MenuItem
import javafx.scene.control.ProgressIndicator
import javafx.scene.control.TextField
import javafx.scene.control.ToggleButton
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.input.Dragboard
import javafx.scene.input.DragEvent
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyCodeCombination
import javafx.scene.input.KeyCombination
import javafx.scene.input.KeyEvent
import javafx.scene.input.MouseButton
import javafx.scene.input.MouseEvent
import javafx.scene.input.TransferMode
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
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
import ziphil.dictionary.SearchType
import ziphil.dictionary.Suggestion
import ziphil.dictionary.Word
import ziphil.dictionary.personal.PersonalDictionary
import ziphil.dictionary.personal.PersonalWord
import ziphil.dictionary.shaleia.ShaleiaDictionary
import ziphil.dictionary.shaleia.ShaleiaSearchParameter
import ziphil.dictionary.shaleia.ShaleiaWord
import ziphil.dictionary.slime.SlimeDictionary
import ziphil.dictionary.slime.SlimeSearchParameter
import ziphil.dictionary.slime.SlimeWord
import ziphil.module.Setting
import ziphilib.transform.ReturnVoidClosure


@CompileStatic @Newify
public class MainController extends PrimitiveController<Stage> {

  private static final String RESOURCE_PATH = "resource/fxml/main.fxml"
  private static final String EXCEPTION_OUTPUT_PATH = "data/log/exception.txt"
  private static final String TITLE = "ZpDIC shalnif"
  private static final Double DEFAULT_WIDTH = Measurement.rpx(720)
  private static final Double DEFAULT_HEIGHT = Measurement.rpx(720)
  private static final Double MIN_WIDTH = Measurement.rpx(360)
  private static final Double MIN_HEIGHT = Measurement.rpx(240)

  @FXML private ListView<? extends Word> $wordList
  @FXML private TextField $searchControl
  @FXML private ComboBox<String> $searchModeControl
  @FXML private ToggleButton $searchTypeControl
  @FXML private ContextMenu $editMenu
  @FXML private MenuItem $modifyWordItem
  @FXML private MenuItem $removeWordItem
  @FXML private MenuItem $addWordItem
  @FXML private MenuItem $addInheritedWordItem
  @FXML private MenuItem $showIndividualSettingItem
  @FXML private Menu $openRegisteredDictionaryMenu
  @FXML private Menu $registerCurrentDictionaryMenu
  @FXML private Menu $searchMenu
  @FXML private HBox $footerBox
  @FXML private Label $dictionaryNameLabel
  @FXML private Label $hitWordSizeLabel
  @FXML private Label $totalWordSizeLabel
  @FXML private Label $elapsedTimeLabel
  @FXML private VBox $loadingBox
  @FXML private ProgressIndicator $progressIndicator
  private Dictionary $dictionary
  private String $previousSearch

  public MainController(Stage nextStage) {
    super(nextStage)
    loadResource(RESOURCE_PATH, TITLE, DEFAULT_WIDTH, DEFAULT_HEIGHT, MIN_WIDTH, MIN_HEIGHT)
    setupDragAndDrop()
    setupShortcuts()
    setupCloseConfirmation()
    setupExceptionHandler()
  }

  @FXML
  public void initialize() {
    setupWordList()
    setupSearchTypeControl()
    setupOpenRegisteredDictionaryMenu()
    setupRegisterCurrentDictionaryMenu()
    setupDebugMenu()
    setupWordListShortcuts()
    updateDictionaryToDefault()
  }

  @FXML
  private void search(KeyEvent event) {
    if ($dictionary != null) {
      String search = $searchControl.getText()
      String searchMode = $searchModeControl.getValue()
      Boolean isStrict = $searchTypeControl.getText() == "完全一致"
      if (search != $previousSearch) {
        if (event == null || event.getCode() != KeyCode.ENTER) {
          measureDictionaryStatus() {
            if (searchMode == "単語") {
              $dictionary.searchByName(search, isStrict)
            } else if (searchMode == "訳語") {
              $dictionary.searchByEquivalent(search, isStrict)
            } else if (searchMode == "全文") {
              $dictionary.searchByContent(search)
            }
          }
          $previousSearch = search
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
        UtilityStage<ShaleiaSearchParameter> nextStage = UtilityStage.new(StageStyle.UTILITY)
        ShaleiaSearcherController controller = ShaleiaSearcherController.new(nextStage)
        nextStage.initOwner($stage)
        ShaleiaSearchParameter parameter = nextStage.showAndWaitResult()
        if (parameter != null) {
          measureDictionaryStatus() {
            $dictionary.searchDetail(parameter)
          }
        }
      } else if ($dictionary instanceof SlimeDictionary) {
        UtilityStage<SlimeSearchParameter> nextStage = UtilityStage.new(StageStyle.UTILITY)
        SlimeSearcherController controller = SlimeSearcherController.new(nextStage)
        nextStage.initOwner($stage)
        controller.prepare($dictionary)
        SlimeSearchParameter parameter = nextStage.showAndWaitResult()
        if (parameter != null) {
          measureDictionaryStatus() {
            $dictionary.searchDetail(parameter) 
          }
        }
      } else {
        Dialog dialog = Dialog.new("通知", "この辞書形式では高度な検索をすることはできません。")
        dialog.initOwner($stage)
        dialog.setAllowsCancel(false)
        dialog.showAndWait()
      }
    }
  }

  private void searchDetailBy(Object parameter) {
    if ($dictionary instanceof ShaleiaDictionary && parameter instanceof ShaleiaSearchParameter) {
      measureDictionaryStatus() {
        $dictionary.searchDetail(parameter)
      }
    } else if ($dictionary instanceof SlimeDictionary && parameter instanceof SlimeSearchParameter) {
      measureDictionaryStatus() {
        $dictionary.searchDetail(parameter)
      }
    }
  }

  @FXML
  private void searchScript() {
    if ($dictionary != null) {
      UtilityStage<String> nextStage = UtilityStage.new(StageStyle.UTILITY)
      ScriptController controller = ScriptController.new(nextStage)
      nextStage.initOwner($stage)
      String script = nextStage.showAndWaitResult()
      if (script != null) {
        measureDictionaryStatus() {
          $dictionary.searchScript(script)
        }
      }
    }
  }

  @FXML
  private void shuffleWords() {
    if ($dictionary != null) {
      $dictionary.shuffleWords()
    }
  }

  private void measureDictionaryStatus(Runnable searchFunction) {
    Long beforeTime = System.nanoTime()
    searchFunction.run()
    Long afterTime = System.nanoTime()
    Long elapsedTime = (Long)(afterTime - beforeTime).intdiv(1000000)
    $elapsedTimeLabel.setText(elapsedTime.toString())
    $hitWordSizeLabel.setText($dictionary.hitSize().toString())
    $totalWordSizeLabel.setText($dictionary.totalSize().toString())
    $wordList.scrollTo(0)
  }

  @FXML
  private void changeSearchMode() {
    $hitWordSizeLabel.setText($totalWordSizeLabel.getText())
    $searchControl.requestFocus()
    search()
  }

  @FXML
  private void changeSearchModeToWord() {
    $searchModeControl.setValue("単語")
    changeSearchMode()
  }

  @FXML
  private void changeSearchModeToEquivalent() {
    $searchModeControl.setValue("訳語")
    changeSearchMode()
  }

  @FXML
  private void changeSearchModeToContent() {
    $searchModeControl.setValue("全文")
    changeSearchMode()
  }

  @FXML
  private void changeSearchType() {
    if (!$searchTypeControl.isDisable()) {
      $searchTypeControl.setSelected(!$searchTypeControl.isSelected())
    }
  }

  @FXML
  private void toggleSearchType() {
    $searchControl.requestFocus()
    search()
  }

  private void modifyWord(Word word) {
    if ($dictionary != null) {
      if (word != null && !(word instanceof Suggestion)) {
        UtilityStage<Boolean> nextStage = UtilityStage.new(StageStyle.UTILITY)
        Boolean savesAutomatically = Setting.getInstance().getSavesAutomatically()
        Word oldWord = $dictionary.copiedWord(word)
        nextStage.initOwner($stage)
        if ($dictionary instanceof ShaleiaDictionary) {
          ShaleiaEditorController controller = ShaleiaEditorController.new(nextStage)
          controller.prepare((ShaleiaWord)word)
        } else if ($dictionary instanceof PersonalDictionary) {
          PersonalEditorController controller = PersonalEditorController.new(nextStage)
          controller.prepare((PersonalWord)word)
        } else if ($dictionary instanceof SlimeDictionary) {
          SlimeEditorController controller = SlimeEditorController.new(nextStage)
          controller.prepare((SlimeWord)word, $dictionary)
        }
        Boolean isDone = nextStage.showAndWaitResult()
        if (isDone != null && isDone) {
          $dictionary.modifyWord(oldWord, word)
          if (savesAutomatically) {
            $dictionary.save()
          }
        }
      }
    }
  }

  @FXML
  private void modifyWord() {
    Word word = $wordList.getSelectionModel().getSelectedItems()[0]
    modifyWord(word)
  }

  private void removeWord(Word word) {
    if ($dictionary != null) {
      if (word != null && !(word instanceof Suggestion)) {
        Boolean savesAutomatically = Setting.getInstance().getSavesAutomatically()
        $dictionary.removeWord(word)
        if (savesAutomatically) {
          $dictionary.save()
        }
      }
    }
  }

  @FXML
  private void removeWord() {
    Word word = $wordList.getSelectionModel().getSelectedItems()[0]
    removeWord(word)
  }

  @FXML
  private void addWord() {
    if ($dictionary != null) {
      Word newWord
      UtilityStage<Boolean> nextStage = UtilityStage.new(StageStyle.UTILITY)
      Boolean savesAutomatically = Setting.getInstance().getSavesAutomatically()
      String defaultName = $searchControl.getText()
      nextStage.initOwner($stage)
      if ($dictionary instanceof ShaleiaDictionary) {
        ShaleiaEditorController controller = ShaleiaEditorController.new(nextStage)
        newWord = $dictionary.emptyWord()
        controller.prepare((ShaleiaWord)newWord, defaultName)
      } else if ($dictionary instanceof PersonalDictionary) {
        PersonalEditorController controller = PersonalEditorController.new(nextStage)
        newWord = $dictionary.emptyWord()
        controller.prepare((PersonalWord)newWord, defaultName)
      } else if ($dictionary instanceof SlimeDictionary) {
        SlimeEditorController controller = SlimeEditorController.new(nextStage)
        newWord = $dictionary.emptyWord()
        controller.prepare((SlimeWord)newWord, $dictionary, defaultName)
      }
      Boolean isDone = nextStage.showAndWaitResult()
      if (isDone != null && isDone) {
        $dictionary.addWord(newWord)
        if (savesAutomatically) {
          $dictionary.save()
        }
      }
    }
  }

  private void addInheritedWord(Word word) {
    if ($dictionary != null) {
      if (word != null && !(word instanceof Suggestion)) {
        Word newWord
        UtilityStage<Boolean> nextStage = UtilityStage.new(StageStyle.UTILITY)
        Boolean savesAutomatically = Setting.getInstance().getSavesAutomatically()
        nextStage.initOwner($stage)
        if ($dictionary instanceof ShaleiaDictionary) {
          ShaleiaEditorController controller = ShaleiaEditorController.new(nextStage)
          newWord = $dictionary.inheritedWord((ShaleiaWord)word)
          controller.prepare((ShaleiaWord)newWord)
        } else if ($dictionary instanceof PersonalDictionary) {
          PersonalEditorController controller = PersonalEditorController.new(nextStage)
          newWord = $dictionary.inheritedWord((PersonalWord)word)
          controller.prepare((PersonalWord)newWord)
        } else if ($dictionary instanceof SlimeDictionary) {
          SlimeEditorController controller = SlimeEditorController.new(nextStage)
          newWord = $dictionary.inheritedWord((SlimeWord)word)
          controller.prepare((SlimeWord)newWord, $dictionary)
        }
        Boolean isDone = nextStage.showAndWaitResult()
        if (isDone != null && isDone) {
          $dictionary.addWord(newWord)
          if (savesAutomatically) {
            $dictionary.save()
          }
        }
      }
    }
  }

  @FXML
  private void addInheritedWord() {
    Word word = $wordList.getSelectionModel().getSelectedItems()[0]
    addInheritedWord(word)
  }

  @FXML
  private void openDictionary() {
    Boolean allowsOpen = checkDictionaryChange()
    if (allowsOpen) {
      UtilityStage<File> nextStage = UtilityStage.new(StageStyle.UTILITY)
      DictionaryChooserController controller = DictionaryChooserController.new(nextStage)
      nextStage.initModality(Modality.WINDOW_MODAL)
      nextStage.initOwner($stage)
      File file = nextStage.showAndWaitResult()
      if (file != null) {
        Dictionary dictionary = Dictionary.loadDictionary(file)
        updateDictionary(dictionary)
        if (dictionary != null) {
          Setting.getInstance().setDefaultDictionaryPath(file.getAbsolutePath())
        } else {
          Setting.getInstance().setDefaultDictionaryPath(null)
          Dialog dialog = Dialog.new("読み込みエラー", "辞書データが読み込めませんでした。正しいファイルかどうか確認してください。")
          dialog.initOwner($stage)
          dialog.setAllowsCancel(false)
          dialog.showAndWait()
        }
      }
    }
  }

  private void openRegisteredDictionary(File file) {
    Boolean allowsOpen = checkDictionaryChange()
    if (allowsOpen) {
      Dictionary dictionary = Dictionary.loadDictionary(file)
      updateDictionary(dictionary)
      if (dictionary != null) {
        Setting.getInstance().setDefaultDictionaryPath(file.getAbsolutePath())
      } else {
        Setting.getInstance().setDefaultDictionaryPath(null)
        Dialog dialog = Dialog.new("読み込みエラー", "辞書データが読み込めませんでした。正しいファイルかどうか確認してください。")
        dialog.initOwner($stage)
        dialog.setAllowsCancel(false)
        dialog.showAndWait()
      }
    }
  }

  @FXML
  private void createDictionary() {
    Boolean allowsCreate = checkDictionaryChange()
    if (allowsCreate) {
      UtilityStage<File> nextStage = UtilityStage.new(StageStyle.UTILITY)
      DictionaryChooserController controller = DictionaryChooserController.new(nextStage)
      nextStage.initModality(Modality.WINDOW_MODAL)
      nextStage.initOwner($stage)
      controller.prepare(true)
      File file = nextStage.showAndWaitResult()
      if (file != null) {
        Dictionary dictionary = Dictionary.loadEmptyDictionary(file)
        updateDictionary(dictionary)
        if (dictionary != null) {
          Setting.getInstance().setDefaultDictionaryPath(file.getAbsolutePath())
        } else {
          Setting.getInstance().setDefaultDictionaryPath(null)
          Dialog dialog = Dialog.new("新規作成エラー", "辞書の新規作成ができませんでした。辞書形式を正しく選択したか確認してください。")
          dialog.initOwner($stage)
          dialog.setAllowsCancel(false)
          dialog.showAndWait()
        }
      }
    }
  }

  @FXML
  private void saveDictionary() {
    if ($dictionary != null) {
      $dictionary.save()
    } else {
      Dialog dialog = Dialog.new("保存エラー", "辞書が開かれていません。")
      dialog.initOwner($stage)
      dialog.setAllowsCancel(false)
      dialog.showAndWait()
    }
  }

  @FXML
  private void saveAndRenameDictionary() {
    if ($dictionary != null) {
      UtilityStage<File> nextStage = UtilityStage.new(StageStyle.UTILITY)
      DictionaryChooserController controller = DictionaryChooserController.new(nextStage)
      nextStage.initModality(Modality.WINDOW_MODAL)
      nextStage.initOwner($stage)
      controller.prepare(true, Dictionary.extensionOf($dictionary))
      File file = nextStage.showAndWaitResult()
      if (file != null) {
        $dictionary.setName(file.getName())
        $dictionary.setPath(file.getAbsolutePath())
        $dictionary.save()
        $dictionaryNameLabel.setText($dictionary.getName())
        Setting.getInstance().setDefaultDictionaryPath(file.getAbsolutePath())
      }
    } else {
      Dialog dialog = Dialog.new("保存エラー", "辞書が開かれていません。")
      dialog.initOwner($stage)
      dialog.setAllowsCancel(false)
      dialog.showAndWait()
    }
  }

  private void updateDictionary(Dictionary dictionary) {
    if ($dictionary != null) {
      Task<?> oldLoader = $dictionary.getLoader()
      if (oldLoader.isRunning()) {
        oldLoader.cancel()
      }
    }
    $dictionary = dictionary
    if ($dictionary != null) {
      $totalWordSizeLabel.setText($dictionary.totalSize().toString())
      $dictionaryNameLabel.setText($dictionary.getName())
      $wordList.setItems($dictionary.getWholeWords())
      $searchControl.setText("")
      $searchControl.requestFocus()
      Task<?> loader = $dictionary.getLoader()
      $loadingBox.visibleProperty().unbind()
      $progressIndicator.progressProperty().unbind()
      $loadingBox.visibleProperty().bind(Bindings.notEqual(Worker.State.SUCCEEDED, loader.stateProperty()))
      $progressIndicator.progressProperty().bind(loader.progressProperty())
      loader.addEventHandler(WorkerStateEvent.WORKER_STATE_SUCCEEDED) { WorkerStateEvent event ->
        $wordList.scrollTo(0)
      }
      loader.addEventHandler(WorkerStateEvent.WORKER_STATE_FAILED) { WorkerStateEvent event ->
        failUpdateDictionary()
      }
      if ($dictionary instanceof ShaleiaDictionary) {
        $dictionary.setOnLinkClicked() { String name ->
          ShaleiaSearchParameter parameter = ShaleiaSearchParameter.new()
          parameter.setName(name)
          parameter.setNameSearchType(SearchType.EXACT)
          searchDetailBy(parameter)
        }
      } else if ($dictionary instanceof SlimeDictionary) {
        $dictionary.setOnLinkClicked() { Integer id ->
          SlimeSearchParameter parameter = SlimeSearchParameter.new()
          parameter.setId(id)
          searchDetailBy(parameter)
        }
      }
      if ($dictionary instanceof ShaleiaDictionary || $dictionary instanceof SlimeDictionary) {
        $showIndividualSettingItem.setDisable(false)
      } else {
        $showIndividualSettingItem.setDisable(true)
      }
    } else {
      ObservableList<Word> emptyWords = FXCollections.observableArrayList()
      $totalWordSizeLabel.setText("0")
      $dictionaryNameLabel.setText("")
      $wordList.setItems(emptyWords)
      $searchControl.setText("")
      $searchControl.requestFocus()
      $loadingBox.visibleProperty().unbind()
      $loadingBox.setVisible(false)
      $progressIndicator.progressProperty().unbind()
      $progressIndicator.setProgress(0)
    }
    search()
  }

  private void updateDictionaryToDefault() {
    String filePath = Setting.getInstance().getDefaultDictionaryPath()
    if (filePath != null) {
      File file = File.new(filePath)
      Dictionary dictionary = Dictionary.loadDictionary(file)
      updateDictionary(dictionary)
      if (dictionary == null) {
        Setting.getInstance().setDefaultDictionaryPath(null)
        Dialog dialog = Dialog.new("読み込みエラー", "辞書データが読み込めませんでした。正しいファイルかどうか確認してください。")
        dialog.initOwner($stage)
        dialog.setAllowsCancel(false)
        dialog.showAndWait()
      }
    } else {
      updateDictionary(null)
    }
  }

  private void failUpdateDictionary() {
    updateDictionary(null)
    Dialog dialog = Dialog.new("読み込みエラー", "辞書データの読み込み中にエラーが発生しました。データが壊れている可能性があります。")
    dialog.initOwner($stage)
    dialog.setAllowsCancel(false)
    dialog.showAndWait()
  }

  private void registerCurrentDictionary(Integer i) {
    Setting setting = Setting.getInstance()
    setting.getRegisteredDictionaryPaths()[i] = $dictionary.getPath()
    setting.save()
    setupOpenRegisteredDictionaryMenu()
    setupRegisterCurrentDictionaryMenu()
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
      if ($dictionary.isChanged()) {
        Dialog dialog = Dialog.new("確認", "辞書データは変更されています。保存しますか?")
        dialog.initOwner($stage)
        dialog.setCommitText("保存する")
        dialog.setNegateText("保存しない")
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
    throwable.printStackTrace()
    throwable.printStackTrace(stream)
    stream.close()
    dialog.showAndWait()
    Platform.exit()
  }

  @FXML
  private void showHelp() {
    UtilityStage<Void> nextStage = UtilityStage.new(StageStyle.UTILITY)
    HelpController controller = HelpController.new(nextStage)
    nextStage.initModality(Modality.WINDOW_MODAL)
    nextStage.initOwner($stage)
    nextStage.showAndWait()
  }

  @FXML
  private void showOfficialSite() {
    Desktop desktop = Desktop.getDesktop()
    URI uri = URI.new("http://ziphil.s2.adexd.net/application/download/2.html")
    desktop.browse(uri)
  }

  @FXML
  private void showApplicationInformation() {
    UtilityStage<Void> nextStage = UtilityStage.new(StageStyle.UTILITY)
    ApplicationInformationController controller = ApplicationInformationController.new(nextStage)
    nextStage.initModality(Modality.WINDOW_MODAL)
    nextStage.initOwner($stage)
    nextStage.showAndWait()
  }

  @FXML
  private void showIndividualSetting() {
    if ($dictionary != null) {
      UtilityStage<Boolean> nextStage = UtilityStage.new(StageStyle.UTILITY)
      nextStage.initModality(Modality.WINDOW_MODAL)
      nextStage.initOwner($stage)
      if ($dictionary instanceof SlimeDictionary) {
        SlimeIndividualSettingController controller = SlimeIndividualSettingController.new(nextStage)
        controller.prepare($dictionary)
      } else if ($dictionary instanceof ShaleiaDictionary) {
        ShaleiaIndividualSettingController controller = ShaleiaIndividualSettingController.new(nextStage)
        controller.prepare($dictionary)
      }
      Boolean isDone = nextStage.showAndWaitResult()
    }
  }

  @FXML
  private void showSetting() {
    UtilityStage<Boolean> nextStage = UtilityStage.new(StageStyle.UTILITY)
    SettingController controller = SettingController.new(nextStage)
    nextStage.initModality(Modality.WINDOW_MODAL)
    nextStage.initOwner($stage)
    Boolean isDone = nextStage.showAndWaitResult()
    if (isDone != null) {
      Setting.getInstance().save()
      setupOpenRegisteredDictionaryMenu()
      setupRegisterCurrentDictionaryMenu()
    }
  }

  @FXML
  private void exit() {
    Platform.exit()
  }

  @ReturnVoidClosure
  private void setupWordList() {
    $wordList.setCellFactory() { ListView<Word> list ->
      WordCell cell = WordCell.new()
      cell.addEventHandler(MouseEvent.MOUSE_CLICKED) { MouseEvent event ->
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

  private void setupSearchTypeControl() {
    Callable<String> textFunction = (Callable){
      return ($searchTypeControl.selectedProperty().get()) ? "完全一致" : "部分一致"
    }
    Callable<Boolean> disableFunction = (Callable){
      String searchMode = $searchModeControl.getValue()
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
    StringBinding textBinding = Bindings.createStringBinding(textFunction, $searchTypeControl.selectedProperty())
    BooleanBinding disableBinding = Bindings.createBooleanBinding(disableFunction, $searchModeControl.valueProperty())    
    $searchTypeControl.textProperty().bind(textBinding)
    $searchTypeControl.disableProperty().bind(disableBinding)
  }

  private void setupOpenRegisteredDictionaryMenu() {
    List<String> dictionaryPaths = Setting.getInstance().getRegisteredDictionaryPaths()
    $openRegisteredDictionaryMenu.getItems().clear()
    for (Integer i : 0 ..< 10) {
      String dictionaryPath = dictionaryPaths[i]
      MenuItem item = MenuItem.new()
      if (dictionaryPath != null) {
        File file = File.new(dictionaryPath)
        item.setText(file.getName())
        item.setOnAction() {
          openRegisteredDictionary(file)
        }
      } else {
        item.setText("未登録")
        item.setDisable(true)
      }
      Image icon = Image.new(getClass().getClassLoader().getResourceAsStream("resource/icon/dictionary_${(i + 1) % 10}.png"))
      item.setGraphic(ImageView.new(icon))
      item.setAccelerator(KeyCodeCombination.new(KeyCode.valueOf("DIGIT${(i + 1) % 10}"), KeyCombination.SHORTCUT_DOWN))
      $openRegisteredDictionaryMenu.getItems().add(item)
    }
  }

  private void setupRegisterCurrentDictionaryMenu() {
    List<String> dictionaryPaths = Setting.getInstance().getRegisteredDictionaryPaths()
    $registerCurrentDictionaryMenu.getItems().clear()
    for (Integer i : 0 ..< 10) {
      Integer j = i
      String dictionaryPath = dictionaryPaths[i]
      MenuItem item = MenuItem.new()
      if (dictionaryPath == null) {
        item.setText("辞書${(i + 1) % 10}に登録")
        item.setOnAction() {
          registerCurrentDictionary(j)
        }
      } else {
        item.setText("登録済み")
        item.setDisable(true)
      }
      Image icon = Image.new(getClass().getClassLoader().getResourceAsStream("resource/icon/dictionary_${(i + 1) % 10}.png"))
      item.setGraphic(ImageView.new(icon))
      item.setAccelerator(KeyCodeCombination.new(KeyCode.valueOf("DIGIT${(i + 1) % 10}"), KeyCombination.SHORTCUT_DOWN, KeyCombination.SHIFT_DOWN))
      $registerCurrentDictionaryMenu.getItems().add(item)
    }
  }

  private void setupDebugMenu() {
    Boolean isDebugging = Setting.getInstance().isDebugging()
    if (isDebugging) {
      MenuItem item = MenuItem.new()
      Image icon = Image.new(getClass().getClassLoader().getResourceAsStream("resource/icon/script_search.png"))
      item.setText("スクリプト検索")
      item.setGraphic(ImageView.new(icon))
      item.setAccelerator(KeyCodeCombination.new(KeyCode.U, KeyCombination.SHORTCUT_DOWN, KeyCombination.SHIFT_DOWN))
      item.setOnAction() {
        searchScript()
      }
      $searchMenu.getItems().add(item)
    }
  }

  private void setupWordListShortcuts() {
    $wordList.addEventHandler(KeyEvent.KEY_PRESSED) { KeyEvent event ->
      if (event.getCode() == KeyCode.ENTER) {
        modifyWord()
      }
    }
  }

  private void setupDragAndDrop() {
    $scene.addEventHandler(DragEvent.DRAG_OVER) { DragEvent event ->
      Dragboard dragboard = event.getDragboard()
      if (dragboard.hasFiles()) {
        event.acceptTransferModes(TransferMode.COPY_OR_MOVE)
      }
      event.consume()
    }
    $scene.addEventHandler(DragEvent.DRAG_DROPPED) { DragEvent event ->
      Boolean isCompleted = false
      Dragboard dragboard = event.getDragboard()
      if (dragboard.hasFiles()) {
        File file = dragboard.getFiles()[0]
        Platform.runLater() {
          openRegisteredDictionary(file)
        }
        isCompleted = true
      }
      event.setDropCompleted(isCompleted)
      event.consume()
    }
  }

  private void setupShortcuts() {
    $scene.addEventHandler(KeyEvent.KEY_PRESSED) { KeyEvent event ->
      if (KeyCodeCombination.new(KeyCode.L, KeyCombination.SHORTCUT_DOWN).match(event)) {
        focusWordList()
      }
    }
  }

  private void setupCloseConfirmation() {
    $stage.addEventHandler(WindowEvent.WINDOW_CLOSE_REQUEST) { WindowEvent event ->
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