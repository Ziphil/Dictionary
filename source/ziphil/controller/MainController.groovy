package ziphil.controller

import groovy.transform.CompileStatic
import java.awt.Desktop
import java.awt.GraphicsEnvironment
import java.lang.Thread.UncaughtExceptionHandler
import java.security.AccessControlException
import java.security.PrivilegedActionException
import java.util.concurrent.Callable
import javafx.application.Platform
import javafx.beans.binding.Bindings
import javafx.beans.binding.BooleanBinding
import javafx.beans.binding.StringBinding
import javafx.beans.value.ObservableValue
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.concurrent.Task
import javafx.concurrent.Worker
import javafx.concurrent.WorkerStateEvent
import javafx.event.Event
import javafx.fxml.FXML
import javafx.scene.Node
import javafx.scene.Scene
import javafx.scene.control.ComboBox
import javafx.scene.control.ContextMenu
import javafx.scene.control.Label
import javafx.scene.control.ListView
import javafx.scene.control.Menu
import javafx.scene.control.MenuBar
import javafx.scene.control.MenuItem
import javafx.scene.control.ProgressIndicator
import javafx.scene.control.Tab
import javafx.scene.control.TabPane
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
import javax.script.ScriptException
import ziphil.Launcher
import ziphil.custom.Dialog
import ziphil.custom.Measurement
import ziphil.custom.RefreshableListView
import ziphil.custom.UtilityStage
import ziphil.custom.WordCell
import ziphil.dictionary.DetailedSearchParameter
import ziphil.dictionary.Dictionaries
import ziphil.dictionary.Dictionary
import ziphil.dictionary.DictionaryType
import ziphil.dictionary.EditableDictionary
import ziphil.dictionary.Element
import ziphil.dictionary.IndividualSetting
import ziphil.dictionary.NormalSearchParameter
import ziphil.dictionary.PseudoWord
import ziphil.dictionary.ScriptSearchParameter
import ziphil.dictionary.SearchHistory
import ziphil.dictionary.SearchMode
import ziphil.dictionary.SearchParameter
import ziphil.dictionary.SearchType
import ziphil.dictionary.SelectionSearchParameter
import ziphil.dictionary.Suggestion
import ziphil.dictionary.Word
import ziphil.dictionary.WordEditResult
import ziphil.dictionary.personal.PersonalDictionary
import ziphil.dictionary.personal.PersonalWord
import ziphil.dictionary.shaleia.ShaleiaDictionary
import ziphil.dictionary.shaleia.ShaleiaSearchParameter
import ziphil.dictionary.shaleia.ShaleiaWord
import ziphil.dictionary.slime.SlimeDictionary
import ziphil.dictionary.slime.SlimeIndividualSetting
import ziphil.dictionary.slime.SlimeSearchParameter
import ziphil.dictionary.slime.SlimeWord
import ziphil.module.NoSuchScriptEngineException
import ziphil.module.Setting
import ziphil.module.TemporarySetting
import ziphil.module.Version
import ziphilib.transform.VoidClosure
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class MainController extends PrimitiveController<Stage> {

  private static final String RESOURCE_PATH = "resource/fxml/controller/main.fxml"
  private static final String EXCEPTION_OUTPUT_PATH = "data/log/exception.txt"
  private static final String OFFICIAL_SITE_URI = "http://ziphil.web.fc2.com/application/download/2.html"
  private static final String TITLE = "ZpDIC fetith"
  private static final Double MIN_WIDTH = Measurement.rpx(360)
  private static final Double MIN_HEIGHT = Measurement.rpx(240)

  @FXML private MenuBar $menuBar
  @FXML private Menu $createDictionaryMenu
  @FXML private Menu $openRegisteredDictionaryMenu
  @FXML private Menu $registerCurrentDictionaryMenu
  @FXML private Menu $convertDictionaryMenu
  @FXML private Menu $searchRegisteredParameterMenu
  @FXML private TabPane $tabPane
  private List<MainWordListController> $wordListControllers = ArrayList.new()
  private List<Stage> $openStages = Collections.synchronizedList(ArrayList.new())

  public MainController(Stage stage) {
    super(stage)
    loadOriginalResource()
    setupDragAndDrop()
    setupShortcuts()
    setupCloseConfirmation()
    setupExceptionHandler()
  }

  @FXML
  public void initialize() {
    setupTabPane()
    setupCreateDictionaryMenu()
    setupOpenRegisteredDictionaryMenu()
    setupRegisterCurrentDictionaryMenu()
    setupConvertDictionaryMenu()
    setupDebug()
  }

  public void prepare() {
    checkVersion()
    openDefaultDictionary()
  }

  private void addDictionaryTab(Dictionary dictionary) {
    Tab tab = Tab.new()
    MainWordListController controller = MainWordListController.new($stage, tab)
    tab.setText(dictionary.getName())
    controller.update(dictionary)
    $wordListControllers.add(controller)
    $tabPane.getTabs().add(tab)
    $tabPane.getSelectionModel().select(tab)
  }

  private MainWordListController currentWordListController() {
    Int index = $tabPane.getSelectionModel().getSelectedIndex()
    if (index >= 0) {
      return $wordListControllers[index]
    } else {
      return null
    }
  }

  private Dictionary currentDictionary() {
    MainWordListController controller = currentWordListController()
    if (controller != null) {
      return controller.getDictionary()
    } else {
      return null
    }
  }

  private IndividualSetting currentIndividualSetting() {
    MainWordListController controller = currentWordListController()
    if (controller != null) {
      return controller.getIndividualSetting()
    } else {
      return null
    }
  }

  @FXML
  private void openDictionary() {
    UtilityStage<File> nextStage = UtilityStage.new(StageStyle.UTILITY)
    DictionaryChooserController controller = DictionaryChooserController.new(nextStage)
    nextStage.initModality(Modality.APPLICATION_MODAL)
    nextStage.initOwner($stage)
    nextStage.showAndWait()
    if (nextStage.isCommitted()) {
      File file = nextStage.getResult()
      Dictionary dictionary = Dictionaries.loadDictionary(file)
      if (dictionary != null) {
        Setting.getInstance().setDefaultDictionaryPath(file.getAbsolutePath())
        addDictionaryTab(dictionary)
      } else {
        Dialog dialog = Dialog.new(StageStyle.UTILITY)
        dialog.initOwner($stage)
        dialog.setTitle("読み込みエラー")
        dialog.setContentText("辞書データが読み込めませんでした。正しいファイルかどうか確認してください。")
        dialog.setAllowsCancel(false)
        dialog.showAndWait()
        Setting.getInstance().setDefaultDictionaryPath(null)
      }
    }
  }

  private void openDefaultDictionary() {
    String filePath = Setting.getInstance().getDefaultDictionaryPath()
    if (filePath != null) {
      File file = File.new(filePath)
      Dictionary dictionary = Dictionaries.loadDictionary(file)
      if (dictionary != null) {
        addDictionaryTab(dictionary)
      } else {
        Dialog dialog = Dialog.new(StageStyle.UTILITY)
        dialog.initOwner($stage)
        dialog.setTitle("読み込みエラー")
        dialog.setContentText("辞書データが読み込めませんでした。正しいファイルかどうか確認してください。")
        dialog.setAllowsCancel(false)
        dialog.showAndWait()
        Setting.getInstance().setDefaultDictionaryPath(null)
      }
    }
  }

  private void openRegisteredDictionary(File file) {
    Dictionary dictionary = Dictionaries.loadDictionary(file)
    if (dictionary != null) {
      Setting.getInstance().setDefaultDictionaryPath(file.getAbsolutePath())
      addDictionaryTab(dictionary)
    } else {
      Dialog dialog = Dialog.new(StageStyle.UTILITY)
      dialog.initOwner($stage)
      dialog.setTitle("読み込みエラー")
      dialog.setContentText("辞書データが読み込めませんでした。正しいファイルかどうか確認してください。")
      dialog.setAllowsCancel(false)
      dialog.showAndWait()
      Setting.getInstance().setDefaultDictionaryPath(null)
    }
  }

  private void createDictionary(DictionaryType type) {
    UtilityStage<File> nextStage = UtilityStage.new(StageStyle.UTILITY)
    DictionaryChooserController controller = DictionaryChooserController.new(nextStage)
    nextStage.initModality(Modality.APPLICATION_MODAL)
    nextStage.initOwner($stage)
    controller.prepare(type, null, true)
    nextStage.showAndWait()
    if (nextStage.isCommitted()) {
      File file = nextStage.getResult()
      Dictionary dictionary = Dictionaries.loadEmptyDictionary(type, file)
      if (dictionary != null) {
        Setting.getInstance().setDefaultDictionaryPath(file.getAbsolutePath())
        addDictionaryTab(dictionary)
      } else {
        Dialog dialog = Dialog.new(StageStyle.UTILITY)
        dialog.initOwner($stage)
        dialog.setTitle("新規作成エラー")
        dialog.setContentText("辞書の新規作成ができませんでした。辞書形式を正しく選択したか確認してください。")
        dialog.setAllowsCancel(false)
        dialog.showAndWait()
        Setting.getInstance().setDefaultDictionaryPath(null)
      }
    }
  }

  @FXML
  private void saveDictionary() {
    Dictionary dictionary = currentDictionary()
    if (dictionary != null) {
      dictionary.save()
    }
  }

  @FXML
  private void saveAndRenameDictionary() {
    Dictionary dictionary = currentDictionary()
    if (dictionary != null) {
      UtilityStage<File> nextStage = UtilityStage.new(StageStyle.UTILITY)
      DictionaryChooserController controller = DictionaryChooserController.new(nextStage)
      nextStage.initModality(Modality.APPLICATION_MODAL)
      nextStage.initOwner($stage)
      controller.prepare(DictionaryType.valueOfDictionary(dictionary), File.new(dictionary.getPath()).getParentFile(), true)
      nextStage.showAndWait()
      if (nextStage.isCommitted()) {
        File file = nextStage.getResult()
        if (file != null) {
          dictionary.setName(file.getName())
          dictionary.setPath(file.getAbsolutePath())
          dictionary.save()
          Setting.getInstance().setDefaultDictionaryPath(file.getAbsolutePath())
        } else {
          Dialog dialog = Dialog.new(StageStyle.UTILITY)
          dialog.initOwner($stage)
          dialog.setTitle("保存エラー")
          dialog.setContentText("辞書の保存ができませんでした。正しいファイルかどうか確認してください。")
          dialog.setAllowsCancel(false)
          dialog.showAndWait()
        }
      }
    }
  }

  private void convertDictionary(DictionaryType type) {
    Dictionary dictionary = currentDictionary()
    if (dictionary != null) {
      UtilityStage<File> nextStage = UtilityStage.new(StageStyle.UTILITY)
      DictionaryChooserController controller = DictionaryChooserController.new(nextStage)
      nextStage.initModality(Modality.APPLICATION_MODAL)
      nextStage.initOwner($stage)
      controller.prepare(type, File.new(dictionary.getPath()).getParentFile(), true)
      nextStage.showAndWait()
      if (nextStage.isCommitted()) {
        File file = nextStage.getResult()
        Dictionary newDictionary = Dictionaries.convertDictionary(type, dictionary, file)
        if (newDictionary != null) {
          Setting.getInstance().setDefaultDictionaryPath(file.getAbsolutePath())
          addDictionaryTab(newDictionary)
        } else {
          Dialog dialog = Dialog.new(StageStyle.UTILITY)
          dialog.initOwner($stage)
          dialog.setTitle("変換エラー")
          dialog.setContentText("辞書の変換ができませんでした。正しいファイルかどうか確認してください。")
          dialog.setAllowsCancel(false)
          dialog.showAndWait()
          Setting.getInstance().setDefaultDictionaryPath(null)
        }
      }
    }
  }

  private void updateMenuItems() {
    Dictionary dictionary = currentDictionary()
    String plainName = Dictionaries.plainNameOf(dictionary) ?: "missing"
    for (Menu menu : $menuBar.getMenus()) {
      for (MenuItem item : menu.getItems()) {
        List<String> styleClass = item.getStyleClass()
        if (styleClass.contains("option")) {
          if (styleClass.contains(plainName) || (dictionary != null && styleClass.contains("all"))) {
            item.setDisable(false)
            if (styleClass.contains("dammy")) {
              if (!styleClass.contains("menu")) {
                item.setDisable(true)
              }
              item.setVisible(true)
            }
          } else {
            item.setDisable(true)
            if (styleClass.contains("dammy")) {
              item.setVisible(false)
            }
          }
        }
      }
    }
    $menuBar.layout()
  }

  private void updateSearchRegisteredParameterMenu() {
    $searchRegisteredParameterMenu.getItems().clear()
    IndividualSetting individualSetting = currentIndividualSetting()
    if (individualSetting != null) {
      if (individualSetting instanceof SlimeIndividualSetting) {
        List<SlimeSearchParameter> parameters = individualSetting.getRegisteredParameters()
        List<String> parameterNames = individualSetting.getRegisteredParameterNames()
        for (Int i = 0 ; i < 10 ; i ++) {
          SlimeSearchParameter parameter = parameters[i]
          String parameterName = parameterNames[i]
          MenuItem item = MenuItem.new()
          if (parameter != null) {
            item.setText(parameterNames[i] ?: "")
            item.setOnAction() {
              currentWordListController().measureAndSearch(parameter)
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

  private void registerCurrentDictionary(Int index) {
    Setting.getInstance().getRegisteredDictionaryPaths()[index] = currentDictionary().getPath()
    setupOpenRegisteredDictionaryMenu()
    setupRegisterCurrentDictionaryMenu()
  }

  @FXML
  private void editIndividualSetting() {
    Dictionary dictionary = currentDictionary()
    IndividualSetting individualSetting = currentIndividualSetting()
    if (dictionary != null) {
      UtilityStage<BooleanClass> nextStage = UtilityStage.new(StageStyle.UTILITY)
      nextStage.initModality(Modality.APPLICATION_MODAL)
      nextStage.initOwner($stage)
      if (dictionary instanceof ShaleiaDictionary) {
        ShaleiaIndividualSettingController controller = ShaleiaIndividualSettingController.new(nextStage)
        controller.prepare(dictionary)
      } else if (dictionary instanceof SlimeDictionary && individualSetting instanceof SlimeIndividualSetting) {
        SlimeIndividualSettingController controller = SlimeIndividualSettingController.new(nextStage)
        controller.prepare(dictionary, individualSetting)
      }
      nextStage.showAndWait()
      if (nextStage.isCommitted()) {
        updateSearchRegisteredParameterMenu()
      }
    }
  }

  @FXML
  private void editSetting() {
    UtilityStage<BooleanClass> nextStage = UtilityStage.new(StageStyle.UTILITY)
    SettingController controller = SettingController.new(nextStage)
    nextStage.initModality(Modality.APPLICATION_MODAL)
    nextStage.initOwner($stage)
    nextStage.showAndWait()
    if (nextStage.isCommitted()) {
      setupOpenRegisteredDictionaryMenu()
      setupRegisterCurrentDictionaryMenu()
    }
  }

  @FXML
  private void executeHahCompression() {
    Boolean keepsEditorOnTop = Setting.getInstance().getKeepsEditorOnTop()
    Dictionary dictionary = currentDictionary()
    UtilityStage<Void> nextStage = UtilityStage.new(StageStyle.UTILITY)
    HahCompressionExecutorController controller = HahCompressionExecutorController.new(nextStage)
    if (keepsEditorOnTop) {
      nextStage.initOwner($stage)
    }
    if (dictionary instanceof ShaleiaDictionary) {
      controller.prepare(dictionary.getAlphabetOrder())
    } else if (dictionary instanceof SlimeDictionary) {
      controller.prepare(dictionary.getAlphabetOrder())
    } else {
      controller.prepare(null)
    }
    $openStages.add(nextStage)
    nextStage.showAndWait()
    $openStages.remove(nextStage)
  }

  @FXML
  private void executeAkrantiain() {
    Boolean keepsEditorOnTop = Setting.getInstance().getKeepsEditorOnTop()
    UtilityStage<Void> nextStage = UtilityStage.new(StageStyle.UTILITY)
    AkrantiainExecutorController controller = AkrantiainExecutorController.new(nextStage)
    if (keepsEditorOnTop) {
      nextStage.initOwner($stage)
    }
    $openStages.add(nextStage)
    nextStage.showAndWait()
    $openStages.remove(nextStage)
  }

  @FXML
  private void executeZatlin() {
    Boolean keepsEditorOnTop = Setting.getInstance().getKeepsEditorOnTop()
    UtilityStage<Void> nextStage = UtilityStage.new(StageStyle.UTILITY)
    ZatlinExecutorController controller = ZatlinExecutorController.new(nextStage)
    if (keepsEditorOnTop) {
      nextStage.initOwner($stage)
    }
    $openStages.add(nextStage)
    nextStage.showAndWait()
    $openStages.remove(nextStage)
  }

  @FXML
  private void executeCharacterAnalysis() {
    Boolean keepsEditorOnTop = Setting.getInstance().getKeepsEditorOnTop()
    UtilityStage<Void> nextStage = UtilityStage.new(StageStyle.UTILITY)
    CharacterFrequencyAnalyzerController controller = CharacterFrequencyAnalyzerController.new(nextStage)
    if (keepsEditorOnTop) {
      nextStage.initOwner($stage)
    }
    $openStages.add(nextStage)
    nextStage.showAndWait()
    $openStages.remove(nextStage)
  }

  @FXML
  private void printDictionary() {
    Dictionary dictionary = currentDictionary()
    if (!dictionary.getWholeWords().isEmpty()) {
      UtilityStage<Void> nextStage = UtilityStage.new(StageStyle.UTILITY)
      PrintController controller = PrintController.new(nextStage)
      nextStage.initModality(Modality.APPLICATION_MODAL)
      nextStage.initOwner($stage)
      controller.prepare(dictionary)
      nextStage.showAndWait()
    } else {
      Dialog dialog = Dialog.new(StageStyle.UTILITY)
      dialog.initOwner($stage)
      dialog.setTitle("印刷内容エラー")
      dialog.setContentText("印刷する単語データがありません。")
      dialog.setAllowsCancel(false)
      dialog.showAndWait()
    }
  }

  @FXML
  private void showStatistics() {
    Dictionary dictionary = currentDictionary()
    UtilityStage<Void> nextStage = UtilityStage.new(StageStyle.UTILITY)
    StatisticsController controller = StatisticsController.new(nextStage)
    nextStage.initModality(Modality.APPLICATION_MODAL)
    nextStage.initOwner($stage)
    controller.prepare(dictionary)
    nextStage.showAndWait()
  }

  @FXML
  private void showHelp() {
    UtilityStage<Void> nextStage = UtilityStage.new(StageStyle.UTILITY)
    HelpController controller = HelpController.new(nextStage)
    nextStage.initModality(Modality.APPLICATION_MODAL)
    nextStage.initOwner($stage)
    nextStage.showAndWait()
  }

  @FXML
  private void showOfficialSite() {
    if (Desktop.isDesktopSupported() && !GraphicsEnvironment.isHeadless()) {
      Desktop desktop = Desktop.getDesktop()
      if (desktop.isSupported(Desktop.Action.BROWSE)) {
        URI uri = URI.new(OFFICIAL_SITE_URI)
        desktop.browse(uri)
      } else {
        Dialog dialog = Dialog.new(StageStyle.UTILITY)
        dialog.initOwner($stage)
        dialog.setTitle("デスクトップエラー")
        dialog.setContentText("この環境はブラウザの起動がサポートされていません。")
        dialog.setAllowsCancel(false)
        dialog.showAndWait()
      }
    } else {
      Dialog dialog = Dialog.new(StageStyle.UTILITY)
      dialog.initOwner($stage)
      dialog.setTitle("デスクトップエラー")
      dialog.setContentText("この環境はデスクトップの操作がサポートされていません。")
      dialog.setAllowsCancel(false)
      dialog.showAndWait()
    }
  }

  @FXML
  private void showApplicationInformation() {
    UtilityStage<Void> nextStage = UtilityStage.new(StageStyle.UTILITY)
    ApplicationInformationController controller = ApplicationInformationController.new(nextStage)
    nextStage.initModality(Modality.APPLICATION_MODAL)
    nextStage.initOwner($stage)
    nextStage.showAndWait()
  }

  @FXML
  public void searchNormal(KeyEvent event) {
    currentWordListController().searchNormal()
  }

  @FXML
  public void searchDetail() {
    currentWordListController().searchDetail()
  }

  @FXML
  private void searchScript() {
    currentWordListController().searchScript()
  }

  @FXML
  private void searchPrevious() {
    currentWordListController().searchPrevious()
  }

  @FXML
  private void searchNext() {
    currentWordListController().searchPrevious()
  }

  @FXML
  private void searchSentence() {
    currentWordListController().searchSentence()
  }

  @FXML
  private void shuffleWords() {
    currentWordListController().shuffleWords()
  }

  @FXML
  private void changeSearchModeToWord() {
    currentWordListController().changeSearchModeToWord()
  }

  @FXML
  private void changeSearchModeToEquivalent() {
    currentWordListController().changeSearchModeToEquivalent()
  }

  @FXML
  private void changeSearchModeToContent() {
    currentWordListController().changeSearchModeToContent()
  }

  @FXML
  private void changeSearchType() {
    currentWordListController().changeSearchType()
  }

  @FXML
  private void modifyWord() {
    currentWordListController().modifyWord()
  }

  @FXML
  private void removeWord() {
    currentWordListController().removeWord()
  }

  @FXML
  private void addWord() {
    currentWordListController().addWord()
  }

  @FXML
  private void addInheritedWord() {
    currentWordListController().addInheritedWord()
  }

  @FXML
  private void addGeneratedWords() {
    currentWordListController().addGeneratedWords()
  }

  private void checkVersion() {
    Version previousVersion = Setting.getInstance().getVersion()
    if (false && previousVersion < Launcher.VERSION) {
      UtilityStage<Void> nextStage = UtilityStage.new(StageStyle.UTILITY)
      UpdateInformationController controller = UpdateInformationController.new(nextStage)
      nextStage.initModality(Modality.APPLICATION_MODAL)
      nextStage.initOwner($stage)
      nextStage.showAndWait()
    }
  }

  private void saveMainWindowSize() {
    Setting setting = Setting.getInstance()
    if (setting.getPreservesMainWindowSize()) {
      setting.setMainWindowWidth((Int)$scene.getWidth())
      setting.setMainWindowHeight((Int)$scene.getHeight())
    }
  }

  private void closeOpenStages() {
    for (Stage stage : $openStages) {
      stage.close()
    }
    $openStages.clear()
  }

  private void handleException(Throwable throwable) {
    Dictionary dictionary = currentDictionary()
    IndividualSetting individualSetting = currentIndividualSetting()
    PrintWriter writer = PrintWriter.new(Launcher.BASE_PATH + EXCEPTION_OUTPUT_PATH)
    String name = throwable.getClass().getSimpleName()
    Dialog dialog = Dialog.new(StageStyle.UTILITY)
    if (dictionary != null) {
      dictionary.saveBackup()
    }
    if (individualSetting != null) {
      individualSetting.save()
    }
    Setting.getInstance().save()
    dialog.initOwner($stage)
    dialog.setTitle("エラー")
    dialog.setContentText("エラーが発生しました(${name})。詳細はエラーログを確認してください。")
    dialog.setAllowsCancel(false)
    throwable.printStackTrace()
    throwable.printStackTrace(writer)
    writer.flush()
    writer.close()
    dialog.showAndWait()
    Platform.exit()
  }

  @FXML
  private void exit() {
    closeOpenStages()
    saveMainWindowSize()
    Platform.exit()
  }

  private void setupTabPane() {
    $tabPane.addEventHandler(Tab.TAB_CLOSE_REQUEST_EVENT) { Event event ->
      Int index = $tabPane.getSelectionModel().getSelectedIndex()
      if (index > 0) {
        Boolean allowsClose = $wordListControllers[index].close()
        if (!allowsClose) {
          event.consume()
        }
      }
    }
    $tabPane.getSelectionModel().selectedItemProperty().addListener() { ObservableValue<? extends Tab> observableValue, Tab oldValue, Tab newValue ->
      updateMenuItems()
      updateSearchRegisteredParameterMenu()
    }
  }

  private void setupCreateDictionaryMenu() {
    $createDictionaryMenu.getItems().clear()
    for (DictionaryType type : DictionaryType.values()) {
      DictionaryType cachedType = type
      MenuItem item = MenuItem.new(type.getName())
      item.setGraphic(ImageView.new(type.createIcon()))
      item.setOnAction() {
        createDictionary(cachedType)
      }
      $createDictionaryMenu.getItems().add(item)
    }
  }

  private void setupOpenRegisteredDictionaryMenu() {
    $openRegisteredDictionaryMenu.getItems().clear()
    Setting setting = Setting.getInstance()
    List<String> dictionaryPaths = setting.getRegisteredDictionaryPaths()
    List<String> dictionaryNames = setting.getRegisteredDictionaryNames()
    for (Int i = 0 ; i < 10 ; i ++) {
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
    $registerCurrentDictionaryMenu.getItems().clear()
    List<String> dictionaryPaths = Setting.getInstance().getRegisteredDictionaryPaths()
    for (Int i = 0 ; i < 10 ; i ++) {
      Int j = i
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

  private void setupConvertDictionaryMenu() {
    $convertDictionaryMenu.getItems().clear()
    for (DictionaryType type : DictionaryType.values()) {
      DictionaryType cachedType = type
      MenuItem item = MenuItem.new(type.getName())
      item.setGraphic(ImageView.new(type.createIcon()))
      item.setOnAction() {
        convertDictionary(cachedType)
      }
      if (type == DictionaryType.SHALEIA) {
        item.setDisable(true)
      }
      $convertDictionaryMenu.getItems().add(item)
    }
  }

  private void setupExceptionHandler() {
    Thread.setDefaultUncaughtExceptionHandler() { Thread thread, Throwable throwable ->
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
      Boolean completed = false
      Dragboard dragboard = event.getDragboard()
      if (dragboard.hasFiles()) {
        File file = dragboard.getFiles()[0]
        Platform.runLater() {
          openRegisteredDictionary(file)
        }
        completed = true
      }
      event.setDropCompleted(completed)
      event.consume()
    }
  }

  private void setupShortcuts() {
    $scene.addEventHandler(KeyEvent.KEY_PRESSED) { KeyEvent event ->
      if (KeyCodeCombination.new(KeyCode.L, KeyCombination.SHORTCUT_DOWN).match(event)) {
        MainWordListController controller = currentWordListController()
        if (controller != null) {
          controller.focusWordList()
        }
      }
    }
  }

  private void setupCloseConfirmation() {
    $stage.addEventHandler(WindowEvent.WINDOW_CLOSE_REQUEST) { WindowEvent event ->
      Boolean allowsClose = true
      if (allowsClose) {
        closeOpenStages()
        saveMainWindowSize()
        Setting.getInstance().save()
      } else {
        event.consume()
      }
    }
  }

  private void setupDebug() {
    Boolean debugging = Setting.getInstance().isDebugging()
  }

  private void loadOriginalResource() {
    Setting setting = Setting.getInstance()
    Double defaultWidth = setting.getMainWindowWidth()
    Double defaultHeight = setting.getMainWindowHeight()
    loadResource(RESOURCE_PATH, TITLE, defaultWidth, defaultHeight, MIN_WIDTH, MIN_HEIGHT)
  }

}