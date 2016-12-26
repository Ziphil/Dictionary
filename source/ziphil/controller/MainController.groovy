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
import ziphil.dictionary.DetailSearchParameter
import ziphil.dictionary.Dictionary
import ziphil.dictionary.Dictionaries
import ziphil.dictionary.Element
import ziphil.dictionary.IndividualSetting
import ziphil.dictionary.NormalSearchParameter
import ziphil.dictionary.SearchHistory
import ziphil.dictionary.SearchMode
import ziphil.dictionary.SearchParameter
import ziphil.dictionary.SearchType
import ziphil.dictionary.Suggestion
import ziphil.dictionary.Word
import ziphil.dictionary.personal.PersonalDictionary
import ziphil.dictionary.personal.PersonalWord
import ziphil.dictionary.shaleia.ShaleiaDictionary
import ziphil.dictionary.shaleia.ShaleiaSearchParameter
import ziphil.dictionary.shaleia.ShaleiaWord
import ziphil.dictionary.slime.SlimeDictionary
import ziphil.dictionary.slime.SlimeIndividualSetting
import ziphil.dictionary.slime.SlimeSearchParameter
import ziphil.dictionary.slime.SlimeWord
import ziphil.module.Setting
import ziphil.module.Version
import ziphilib.transform.VoidClosure
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class MainController extends PrimitiveController<Stage> {

  private static final String RESOURCE_PATH = "resource/fxml/controller/main.fxml"
  private static final String EXCEPTION_OUTPUT_PATH = "data/log/exception.txt"
  private static final String TITLE = "ZpDIC shalnif"
  private static final Double DEFAULT_WIDTH = Measurement.rpx(720)
  private static final Double DEFAULT_HEIGHT = Measurement.rpx(720)
  private static final Double MIN_WIDTH = Measurement.rpx(360)
  private static final Double MIN_HEIGHT = Measurement.rpx(240)

  @FXML private ListView<Element> $wordsView
  @FXML private TextField $searchControl
  @FXML private ComboBox<SearchMode> $searchModeControl
  @FXML private ToggleButton $searchTypeControl
  @FXML private Menu $openRegisteredDictionaryMenu
  @FXML private Menu $registerCurrentDictionaryMenu
  @FXML private Menu $searchRegisteredParameterMenu
  @FXML private MenuItem $searchRegisteredParameterItem
  @FXML private ContextMenu $editMenu
  @FXML private MenuItem $saveDictionaryItem
  @FXML private MenuItem $saveAndRenameDictionaryItem
  @FXML private MenuItem $searchDetailItem
  @FXML private MenuItem $searchScriptItem
  @FXML private MenuItem $addWordItem
  @FXML private MenuItem $addInheritedWordItem
  @FXML private MenuItem $modifyWordItem
  @FXML private MenuItem $removeWordItem
  @FXML private MenuItem $editIndividualSettingItem
  @FXML private MenuItem $addWordContextItem
  @FXML private MenuItem $addInheritedWordContextItem
  @FXML private MenuItem $modifyWordContextItem
  @FXML private MenuItem $removeWordContextItem
  @FXML private HBox $footerBox
  @FXML private Label $dictionaryNameLabel
  @FXML private Label $hitWordSizeLabel
  @FXML private Label $totalWordSizeLabel
  @FXML private Label $elapsedTimeLabel
  @FXML private VBox $loadingBox
  @FXML private ProgressIndicator $progressIndicator
  private Dictionary $dictionary = null
  private IndividualSetting $individualSetting = null
  private SearchHistory $searchHistory = SearchHistory.new()
  private String $previousSearch = ""

  public MainController(Stage stage) {
    super(stage)
    loadResource(RESOURCE_PATH, TITLE, DEFAULT_WIDTH, DEFAULT_HEIGHT, MIN_WIDTH, MIN_HEIGHT)
    setupSearchHistory()
    setupDragAndDrop()
    setupShortcuts()
    setupCloseConfirmation()
    setupExceptionHandler()
  }

  @FXML
  public void initialize() {
    setupWordsView()
    setupSearchControl()
    setupOpenRegisteredDictionaryMenu()
    setupRegisterCurrentDictionaryMenu()
    setupWordsViewShortcuts()
    setupDebug()
    bindSearchTypeControlProperty()
    checkVersion()
    updateDictionaryToDefault()
  }

  private void search(Boolean forcesSearch) {
    if ($dictionary != null) {
      String search = $searchControl.getText()
      SearchMode searchMode = $searchModeControl.getValue()
      Boolean isStrict = $searchTypeControl.isSelected()
      if (forcesSearch || search != $previousSearch) {
        searchBy(search, searchMode, isStrict)
        $previousSearch = search
        if (forcesSearch) {
          $searchHistory.add(NormalSearchParameter.new(search, searchMode, isStrict), false)
        } else {
          $searchHistory.add(NormalSearchParameter.new(search, searchMode, isStrict), true)
        }
      }
    }
  }

  @FXML
  private void search(KeyEvent event) {
    search(false)
  }

  private void search() {
    search(false)
  }

  private void searchBy(String search, SearchMode searchMode, Boolean isStrict) {
    measureDictionaryStatus() {
      if (searchMode == SearchMode.NAME) {
        $dictionary.searchByName(search, isStrict)
      } else if (searchMode == SearchMode.EQUIVALENT) {
        $dictionary.searchByEquivalent(search, isStrict)
      } else if (searchMode == SearchMode.CONTENT) {
        $dictionary.searchByContent(search)
      }
    }
  }

  @FXML
  private void searchDetail() {
    if ($dictionary != null) {
      if ($dictionary instanceof ShaleiaDictionary) {
        UtilityStage<ShaleiaSearchParameter> nextStage = UtilityStage.new(StageStyle.UTILITY)
        ShaleiaSearcherController controller = ShaleiaSearcherController.new(nextStage)
        nextStage.initOwner($stage)
        nextStage.showAndWait()
        if (nextStage.isCommitted()) {
          ShaleiaSearchParameter parameter = nextStage.getResult()
          searchDetailBy(parameter)
          $searchHistory.add(parameter)
        }
      } else if ($dictionary instanceof SlimeDictionary) {
        UtilityStage<SlimeSearchParameter> nextStage = UtilityStage.new(StageStyle.UTILITY)
        SlimeSearcherController controller = SlimeSearcherController.new(nextStage)
        nextStage.initOwner($stage)
        controller.prepare($dictionary)
        nextStage.showAndWait()
        if (nextStage.isCommitted()) {
          SlimeSearchParameter parameter = nextStage.getResult()
          searchDetailBy(parameter)
          $searchHistory.add(parameter)
        }
      }
    }
  }

  private void searchDetailBy(DetailSearchParameter parameter) {
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
      nextStage.showAndWait()
      if (nextStage.isCommitted()) {
        String script = nextStage.getResult()
        searchScriptBy(script)
      }
    }
  }

  private void searchScriptBy(String script) {
    measureDictionaryStatus() {
      $dictionary.searchScript(script)
    }
  }

  @FXML
  private void searchPrevious() {
    if ($dictionary != null) {
      SearchParameter parameter = $searchHistory.previous()
      if (parameter != null) {
        if (parameter instanceof NormalSearchParameter) {
          String search = parameter.getSearch()
          SearchMode searchMode = parameter.getSearchMode()
          Boolean isStrict = parameter.isStrict()
          $searchControl.setText(search)
          $searchModeControl.setValue(searchMode)
          $searchTypeControl.setSelected(isStrict)
          $previousSearch = search
          searchBy(search, searchMode, isStrict)
        } else if (parameter instanceof DetailSearchParameter) {
          searchDetailBy(parameter)
        }
      }
    }
  }

  @FXML
  private void searchNext() {
    if ($dictionary != null) {
      SearchParameter parameter = $searchHistory.next()
      if (parameter != null) {
        if (parameter instanceof NormalSearchParameter) {
          String search = parameter.getSearch()
          SearchMode searchMode = parameter.getSearchMode()
          Boolean isStrict = parameter.isStrict()
          $searchControl.setText(search)
          $searchModeControl.setValue(searchMode)
          $searchTypeControl.setSelected(isStrict)
          $previousSearch = search
          searchBy(search, searchMode, isStrict)
        } else if (parameter instanceof DetailSearchParameter) {
          searchDetailBy(parameter)
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
    $wordsView.scrollTo(0)
  }

  @FXML
  private void changeSearchMode() {
    $searchControl.requestFocus()
    search(true)
  }

  @FXML
  private void changeSearchModeToWord() {
    $searchModeControl.setValue(SearchMode.NAME)
    changeSearchMode()
  }

  @FXML
  private void changeSearchModeToEquivalent() {
    $searchModeControl.setValue(SearchMode.EQUIVALENT)
    changeSearchMode()
  }

  @FXML
  private void changeSearchModeToContent() {
    $searchModeControl.setValue(SearchMode.CONTENT)
    changeSearchMode()
  }

  @FXML
  private void changeSearchType() {
    if (!$searchTypeControl.isDisable()) {
      $searchTypeControl.setSelected(!$searchTypeControl.isSelected())
      search(true)
    }
  }

  @FXML
  private void toggleSearchType() {
    $searchControl.requestFocus()
    search(true)
  }

  private void modifyWord(Element word) {
    if ($dictionary != null) {
      if (word != null && word instanceof Word) {
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
        nextStage.showAndWait()
        if (nextStage.isCommitted() && nextStage.getResult()) {
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
    Element word = $wordsView.getSelectionModel().getSelectedItems()[0]
    modifyWord(word)
  }

  private void removeWord(Element word) {
    if ($dictionary != null) {
      if (word != null && word instanceof Word) {
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
    Element word = $wordsView.getSelectionModel().getSelectedItems()[0]
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
        newWord = $dictionary.emptyWord(defaultName)
        controller.prepare((ShaleiaWord)newWord, true)
      } else if ($dictionary instanceof PersonalDictionary) {
        PersonalEditorController controller = PersonalEditorController.new(nextStage)
        newWord = $dictionary.emptyWord(defaultName)
        controller.prepare((PersonalWord)newWord, true)
      } else if ($dictionary instanceof SlimeDictionary) {
        SlimeEditorController controller = SlimeEditorController.new(nextStage)
        newWord = $dictionary.emptyWord(defaultName)
        controller.prepare((SlimeWord)newWord, $dictionary, true)
      }
      nextStage.showAndWait()
      if (nextStage.isCommitted() && nextStage.getResult()) {
        $dictionary.addWord(newWord)
        if (savesAutomatically) {
          $dictionary.save()
        }
      }
    }
  }

  private void addInheritedWord(Element word) {
    if ($dictionary != null) {
      if (word != null && word instanceof Word) {
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
        nextStage.showAndWait()
        if (nextStage.isCommitted() && nextStage.getResult()) {
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
    Element word = $wordsView.getSelectionModel().getSelectedItems()[0]
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
      nextStage.showAndWait()
      if (nextStage.isCommitted()) {
        File file = nextStage.getResult()
        Dictionary dictionary = Dictionaries.loadDictionary(file)
        updateDictionary(dictionary)
        if (dictionary != null) {
          Setting.getInstance().setDefaultDictionaryPath(file.getAbsolutePath())
        } else {
          Setting.getInstance().setDefaultDictionaryPath(null)
          Dialog dialog = Dialog.new(StageStyle.UTILITY)
          dialog.initOwner($stage)
          dialog.setTitle("読み込みエラー")
          dialog.setContentText("辞書データが読み込めませんでした。正しいファイルかどうか確認してください。")
          dialog.setAllowsCancel(false)
          dialog.showAndWait()
        }
      }
    }
  }

  private void openRegisteredDictionary(File file) {
    Boolean allowsOpen = checkDictionaryChange()
    if (allowsOpen) {
      Dictionary dictionary = Dictionaries.loadDictionary(file)
      updateDictionary(dictionary)
      if (dictionary != null) {
        Setting.getInstance().setDefaultDictionaryPath(file.getAbsolutePath())
      } else {
        Setting.getInstance().setDefaultDictionaryPath(null)
        Dialog dialog = Dialog.new(StageStyle.UTILITY)
        dialog.initOwner($stage)
        dialog.setTitle("読み込みエラー")
        dialog.setContentText("辞書データが読み込めませんでした。正しいファイルかどうか確認してください。")
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
      nextStage.showAndWait()
      if (nextStage.isCommitted()) {
        File file = nextStage.getResult()
        Dictionary dictionary = Dictionaries.loadEmptyDictionary(file)
        updateDictionary(dictionary)
        if (dictionary != null) {
          Setting.getInstance().setDefaultDictionaryPath(file.getAbsolutePath())
        } else {
          Setting.getInstance().setDefaultDictionaryPath(null)
          Dialog dialog = Dialog.new(StageStyle.UTILITY)
          dialog.initOwner($stage)
          dialog.setTitle("新規作成エラー")
          dialog.setTitle("辞書の新規作成ができませんでした。辞書形式を正しく選択したか確認してください。")
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
    }
  }

  @FXML
  private void saveAndRenameDictionary() {
    if ($dictionary != null) {
      UtilityStage<File> nextStage = UtilityStage.new(StageStyle.UTILITY)
      DictionaryChooserController controller = DictionaryChooserController.new(nextStage)
      nextStage.initModality(Modality.WINDOW_MODAL)
      nextStage.initOwner($stage)
      controller.prepare(true, File.new($dictionary.getPath()).getParentFile(), $dictionary.getExtension())
      nextStage.showAndWait()
      if (nextStage.isCommitted()) {
        File file = nextStage.getResult()
        $dictionary.setName(file.getName())
        $dictionary.setPath(file.getAbsolutePath())
        $dictionary.save()
        $dictionaryNameLabel.setText($dictionary.getName())
        Setting.getInstance().setDefaultDictionaryPath(file.getAbsolutePath())
      }
    }
  }

  private void updateDictionary(Dictionary dictionary) {
    cancelLoadDictionary()
    $dictionary = dictionary
    $previousSearch = ""
    $searchHistory.clear()
    updateIndividualSetting()
    updateSearchStatuses()
    updateLoader()
    updateOnLinkClicked()
    updateMenuItems()
    setupSearchRegisteredParameterMenu()
  }

  private void cancelLoadDictionary() {
    if ($dictionary != null) {
      Task<?> oldLoader = $dictionary.getLoader()
      if (oldLoader.isRunning()) {
        oldLoader.cancel()
      }
    }
  }

  private void updateIndividualSetting() {
    if ($dictionary != null) {
      if ($dictionary instanceof SlimeDictionary) {
        $individualSetting = SlimeIndividualSetting.create($dictionary)
      } else {
        $individualSetting = null
      }
    } else {
      $individualSetting = null
    }
  }

  private void updateSearchStatuses() {
    if ($dictionary != null) {
      $dictionaryNameLabel.setText($dictionary.getName())
      $wordsView.setItems($dictionary.getWholeWords())
    } else {
      $dictionaryNameLabel.setText("")
      $wordsView.setItems((ObservableList<Element>)FXCollections.observableArrayList())
    }
    $searchControl.setText("")
    $searchControl.requestFocus()
  }

  private void updateLoader() {
    if ($dictionary != null) {
      Task<?> loader = $dictionary.getLoader()
      $loadingBox.visibleProperty().unbind()
      $progressIndicator.progressProperty().unbind()
      $loadingBox.visibleProperty().bind(Bindings.notEqual(Worker.State.SUCCEEDED, loader.stateProperty()))
      $progressIndicator.progressProperty().bind(loader.progressProperty())
      loader.addEventHandler(WorkerStateEvent.WORKER_STATE_SUCCEEDED) { WorkerStateEvent event ->
        search(true)
      }
      loader.addEventHandler(WorkerStateEvent.WORKER_STATE_FAILED) { WorkerStateEvent event ->
        failUpdateDictionary()
      }
    } else {
      $loadingBox.visibleProperty().unbind()
      $loadingBox.setVisible(false)
      $progressIndicator.progressProperty().unbind()
      $progressIndicator.setProgress(0)
    }
  }

  private void updateOnLinkClicked() {
    if ($dictionary != null) {
      if ($dictionary instanceof ShaleiaDictionary) {
        $dictionary.setOnLinkClicked() { String name ->
          ShaleiaSearchParameter parameter = ShaleiaSearchParameter.new()
          parameter.setName(name)
          parameter.setNameSearchType(SearchType.EXACT)
          searchDetailBy(parameter)
          $searchHistory.add(parameter)
        }
      } else if ($dictionary instanceof SlimeDictionary) {
        $dictionary.setOnLinkClicked() { Integer id ->
          SlimeSearchParameter parameter = SlimeSearchParameter.new()
          parameter.setId(id)
          searchDetailBy(parameter)
          $searchHistory.add(parameter)
        }
      }
    }
  }

  private void updateMenuItems() {
    if ($dictionary != null) {
      $saveDictionaryItem.setDisable(false)
      $saveAndRenameDictionaryItem.setDisable(false)
      $addWordItem.setDisable(false)
      $addInheritedWordItem.setDisable(false)
      $modifyWordItem.setDisable(false)
      $removeWordItem.setDisable(false)
      $searchScriptItem.setDisable(false)
      if ($dictionary instanceof ShaleiaDictionary) {
        $searchDetailItem.setDisable(false)
        $editIndividualSettingItem.setDisable(false)
        $searchRegisteredParameterMenu.setVisible(false)
        $searchRegisteredParameterMenu.setDisable(true)
        $searchRegisteredParameterItem.setVisible(true)
      } else if ($dictionary instanceof PersonalDictionary) {
        $searchDetailItem.setDisable(true)
        $editIndividualSettingItem.setDisable(true)
        $searchRegisteredParameterMenu.setVisible(false)
        $searchRegisteredParameterMenu.setDisable(true)
        $searchRegisteredParameterItem.setVisible(true)
      } else if ($dictionary instanceof SlimeDictionary) {
        $searchDetailItem.setDisable(false)
        $editIndividualSettingItem.setDisable(false)
        $searchRegisteredParameterMenu.setVisible(true)
        $searchRegisteredParameterMenu.setDisable(false)
        $searchRegisteredParameterItem.setVisible(false)
      }
    } else {
      $saveDictionaryItem.setDisable(true)
      $saveAndRenameDictionaryItem.setDisable(true)
      $addWordItem.setDisable(true)
      $addInheritedWordItem.setDisable(true)
      $modifyWordItem.setDisable(true)
      $removeWordItem.setDisable(true)
      $searchDetailItem.setDisable(true)
      $searchScriptItem.setDisable(true)
      $editIndividualSettingItem.setDisable(true)
    }
  }

  private void updateDictionaryToDefault() {
    String filePath = Setting.getInstance().getDefaultDictionaryPath()
    if (filePath != null) {
      File file = File.new(filePath)
      Dictionary dictionary = Dictionaries.loadDictionary(file)
      updateDictionary(dictionary)
      if (dictionary == null) {
        Setting.getInstance().setDefaultDictionaryPath(null)
        Dialog dialog = Dialog.new(StageStyle.UTILITY)
        dialog.initOwner($stage)
        dialog.setTitle("読み込みエラー")
        dialog.setContentText("辞書データが読み込めませんでした。正しいファイルかどうか確認してください。")
        dialog.setAllowsCancel(false)
        dialog.showAndWait()
      }
    } else {
      updateDictionary(null)
    }
  }

  private void failUpdateDictionary() {
    updateDictionary(null)
    Dialog dialog = Dialog.new(StageStyle.UTILITY)
    dialog.initOwner($stage)
    dialog.setTitle("読み込みエラー")
    dialog.setContentText("辞書データの読み込み中にエラーが発生しました。データが壊れている可能性があります。")
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
    $wordsView.requestFocus()
    if ($wordsView.getSelectionModel().getSelectedItems().isEmpty()) {
      $wordsView.getSelectionModel().selectFirst()
      $wordsView.scrollTo(0)
    }
  }

  private Boolean checkDictionaryChange() {
    if ($dictionary != null) {
      if ($dictionary.isChanged()) {
        Dialog dialog = Dialog.new(StageStyle.UTILITY)
        dialog.initOwner($stage)
        dialog.setTitle("確認")
        dialog.setContentText("辞書データは変更されています。保存しますか?")
        dialog.setCommitText("保存する")
        dialog.setNegateText("保存しない")
        dialog.setAllowsNegate(true)
        dialog.showAndWait()
        if (dialog.isCommitted()) {
          $dictionary.save()
          return true
        } else if (dialog.isNegated()) {
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

  @FXML
  private void editIndividualSetting() {
    if ($dictionary != null) {
      UtilityStage<Boolean> nextStage = UtilityStage.new(StageStyle.UTILITY)
      Boolean savesAutomatically = Setting.getInstance().getSavesAutomatically()
      nextStage.initModality(Modality.WINDOW_MODAL)
      nextStage.initOwner($stage)
      if ($dictionary instanceof SlimeDictionary && $individualSetting instanceof SlimeIndividualSetting) {
        SlimeIndividualSettingController controller = SlimeIndividualSettingController.new(nextStage)
        controller.prepare($dictionary, $individualSetting)
      } else if ($dictionary instanceof ShaleiaDictionary) {
        ShaleiaIndividualSettingController controller = ShaleiaIndividualSettingController.new(nextStage)
        controller.prepare($dictionary)
      }
      nextStage.showAndWait()
      if (nextStage.isCommitted() && nextStage.getResult()) {
        if (savesAutomatically) {
          $dictionary.save()
        }
        if ($individualSetting != null) {
          $individualSetting.save()
        }
        setupSearchRegisteredParameterMenu()
      }
    }
  }

  @FXML
  private void editSetting() {
    UtilityStage<Boolean> nextStage = UtilityStage.new(StageStyle.UTILITY)
    SettingController controller = SettingController.new(nextStage)
    nextStage.initModality(Modality.WINDOW_MODAL)
    nextStage.initOwner($stage)
    nextStage.showAndWait()
    if (nextStage.isCommitted()) {
      Setting.getInstance().save()
      setupOpenRegisteredDictionaryMenu()
      setupRegisterCurrentDictionaryMenu()
    }
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
    URI uri = URI.new("http://ziphil.web.fc2.com/application/download/2.html")
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

  private void checkVersion() {
    Version previousVersion = Setting.getInstance().getVersion()
    if (previousVersion < Launcher.VERSION) {
      UtilityStage<Void> nextStage = UtilityStage.new(StageStyle.UTILITY)
      UpdateInformationController controller = UpdateInformationController.new(nextStage)
      nextStage.initModality(Modality.WINDOW_MODAL)
      nextStage.initOwner($stage)
      nextStage.showAndWait()
    }
  }

  private void handleException(Throwable throwable) {
    PrintStream stream = PrintStream.new(Launcher.BASE_PATH + EXCEPTION_OUTPUT_PATH)
    String name = throwable.getClass().getSimpleName()
    Dialog dialog = Dialog.new(StageStyle.UTILITY)
    dialog.initOwner($stage)
    dialog.setTitle("エラー")
    dialog.setContentText("エラーが発生しました(${name})。詳細はエラーログを確認してください。")
    dialog.setAllowsCancel(false)
    throwable.printStackTrace()
    throwable.printStackTrace(stream)
    stream.close()
    dialog.showAndWait()
    Platform.exit()
  }

  @FXML
  private void exit() {
    Platform.exit()
  }

  private void bindSearchTypeControlProperty() {
    Callable<String> textFunction = (Callable){
      return ($searchTypeControl.isSelected()) ? "完全一致" : "部分一致"
    }
    Callable<Boolean> disableFunction = (Callable){
      SearchMode searchMode = $searchModeControl.getValue()
      if (searchMode == SearchMode.NAME) {
        return false
      } else if (searchMode == SearchMode.EQUIVALENT) {
        return false
      } else if (searchMode == SearchMode.CONTENT) {
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

  @VoidClosure
  private void setupWordsView() {
    $wordsView.setCellFactory() { ListView<Element> view ->
      WordCell cell = WordCell.new()
      cell.addEventHandler(MouseEvent.MOUSE_CLICKED) { MouseEvent event ->
        if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
          modifyWord(cell.getItem())
        }
        if (event.getButton() == MouseButton.SECONDARY) {
          cell.setContextMenu($editMenu)
          $modifyWordContextItem.setOnAction() {
            modifyWord(cell.getItem())
          }
          $removeWordContextItem.setOnAction() {
            removeWord(cell.getItem())
          }
          $addWordContextItem.setOnAction() {
            addWord()
          }
          $addInheritedWordContextItem.setOnAction() {
            addInheritedWord(cell.getItem())
          }
        }
      }
      return cell
    }
  }

  private void setupSearchControl() {
    $searchControl.addEventHandler(KeyEvent.KEY_PRESSED) { KeyEvent event ->
      if (KeyCodeCombination.new(KeyCode.Z, KeyCombination.SHORTCUT_DOWN).match(event)) {
        searchPrevious()
        event.consume()
      } else if (KeyCodeCombination.new(KeyCode.Y, KeyCombination.SHORTCUT_DOWN).match(event)) {
        searchNext()
        event.consume()
      }
    }
  }

  private void setupOpenRegisteredDictionaryMenu() {
    Setting setting = Setting.getInstance()
    List<String> dictionaryPaths = setting.getRegisteredDictionaryPaths()
    List<String> dictionaryNames = setting.getRegisteredDictionaryNames()
    $openRegisteredDictionaryMenu.getItems().clear()
    for (Integer i : 0 ..< 10) {
      String dictionaryPath = dictionaryPaths[i]
      String dictionaryName = dictionaryNames[i]
      MenuItem item = MenuItem.new()
      if (dictionaryPath != null) {
        File file = File.new(dictionaryPath)
        item.setText(dictionaryName ?: file.getName())
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
      $registerCurrentDictionaryMenu.getItems().add(item)
    }
  }

  private void setupSearchRegisteredParameterMenu() {
    $searchRegisteredParameterMenu.getItems().clear()
    if ($individualSetting != null) {
      if ($individualSetting instanceof SlimeIndividualSetting) {
        List<SlimeSearchParameter> parameters = $individualSetting.getSearchParameters()
        List<String> parameterNames = $individualSetting.getSearchParameterNames()
        for (Integer i : 0 ..< 10) {
          SlimeSearchParameter parameter = parameters[i]
          String parameterName = parameterNames[i]
          MenuItem item = MenuItem.new()
          if (parameter != null) {
            item.setText(parameterNames[i] ?: "")
            item.setOnAction() {
              searchDetailBy(parameter)
              $searchHistory.add(parameter)
            }
          } else {
            item.setText("未登録")
            item.setDisable(true)
          }
          Image icon = Image.new(getClass().getClassLoader().getResourceAsStream("resource/icon/empty.png"))
          item.setGraphic(ImageView.new(icon))
          item.setAccelerator(KeyCodeCombination.new(KeyCode.valueOf("DIGIT${(i + 1) % 10}"), KeyCombination.SHORTCUT_DOWN, KeyCombination.SHIFT_DOWN))
          $searchRegisteredParameterMenu.getItems().add(item)
        }
      }
    }
  }

  private void setupSearchHistory() {
    Integer separativeInterval = Setting.getInstance().getSeparativeInterval()
    $searchHistory.setSeparativeInterval(separativeInterval)
  }

  private void setupExceptionHandler() {
    Thread.currentThread().setUncaughtExceptionHandler() { Thread thread, Throwable throwable ->
      handleException(throwable)
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

  private void setupWordsViewShortcuts() {
    $wordsView.addEventHandler(KeyEvent.KEY_PRESSED) { KeyEvent event ->
      if (event.getCode() == KeyCode.ENTER) {
        modifyWord()
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

  private void setupDebug() {
    Boolean isDebugging = Setting.getInstance().isDebugging()
    if (!isDebugging) {
      $searchScriptItem.setVisible(false)
    }
  }

}