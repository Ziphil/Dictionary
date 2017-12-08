package ziphil.controller

import groovy.transform.CompileStatic
import java.awt.Toolkit
import java.awt.datatransfer.Clipboard
import java.awt.datatransfer.UnsupportedFlavorException
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import java.security.AccessControlException
import java.security.PrivilegedActionException
import java.text.MessageFormat
import java.util.concurrent.Callable
import javafx.application.Platform
import javafx.beans.binding.Bindings
import javafx.beans.binding.BooleanBinding
import javafx.beans.binding.StringBinding
import javafx.concurrent.Task
import javafx.concurrent.Worker
import javafx.concurrent.WorkerStateEvent
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.Node
import javafx.scene.control.ComboBox
import javafx.scene.control.ContextMenu
import javafx.scene.control.Label
import javafx.scene.control.ListView
import javafx.scene.control.ProgressIndicator
import javafx.scene.control.TextField
import javafx.scene.control.ToggleButton
import javafx.scene.control.SelectionMode
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyCodeCombination
import javafx.scene.input.KeyCombination
import javafx.scene.input.KeyEvent
import javafx.scene.input.MouseButton
import javafx.scene.input.MouseEvent
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import javafx.stage.Stage
import javafx.stage.StageStyle
import javafx.stage.Modality
import javax.script.ScriptException
import ziphil.Launcher
import ziphil.custom.ClosableTab
import ziphil.custom.CustomBuilderFactory
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
import ziphil.dictionary.WordSelection
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
import ziphilib.transform.VoidClosure
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class MainWordListController extends PrimitiveController<Stage> {

  private static final String RESOURCE_PATH = "resource/fxml/controller/main_word_list.fxml"
  private static final String EXCEPTION_OUTPUT_PATH = "data/log/exception.txt"
  private static final String SCRIPT_EXCEPTION_OUTPUT_PATH = "data/log/script_exception.txt"

  @FXML private ContextMenu $editMenu
  @FXML private RefreshableListView<Element> $wordView
  @FXML private TextField $searchControl
  @FXML private ComboBox<SearchMode> $searchModeControl
  @FXML private ToggleButton $searchTypeControl
  @FXML private HBox $footerBox
  @FXML private Label $dictionaryNameLabel
  @FXML private Label $hitWordSizeLabel
  @FXML private Label $totalWordSizeLabel
  @FXML private Label $elapsedTimeLabel
  @FXML private VBox $loadingBox
  @FXML private ProgressIndicator $progressIndicator
  private ClosableTab $tab
  private Dictionary $dictionary
  private IndividualSetting $individualSetting = null
  private TemporarySetting $temporarySetting = null
  private SearchHistory $searchHistory = SearchHistory.new()
  private String $previousSearch = ""
  private List<Stage> $openStages = Collections.synchronizedList(ArrayList.new())

  public MainWordListController(Stage stage, ClosableTab tab) {
    super(stage)
    $tab = tab
    loadOriginalResource()
    setupSearchHistory()
  }

  @FXML
  public void initialize() {
    setupWordView()
    setupWordViewShortcut()
    setupSearchControl()
    setupSearchTypeControl()
  }

  public void update(Dictionary dictionary) {
    $dictionary = dictionary
    $individualSetting = dictionary.createIndividualSetting()
    $temporarySetting = TemporarySetting.new()
    updateLoader()
    updateOnLinkClicked()
  }

  public void searchNormal(Boolean forcesSearch) {
    String search = $searchControl.getText()
    SearchMode searchMode = $searchModeControl.getValue()
    Boolean strict = $searchTypeControl.isSelected()
    NormalSearchParameter parameter = NormalSearchParameter.new(search, searchMode, strict, false)
    if (forcesSearch || search != $previousSearch) {
      measureAndSearch(parameter)
      $previousSearch = search
      if (forcesSearch) {
        $searchHistory.add(parameter, false)
      } else {
        $searchHistory.add(parameter, true)
      }
    }
  }

  public void searchNormal() {
    searchNormal(false)
  }

  public void searchDetail() {
    UtilityStage<SearchParameter> nextStage = UtilityStage.new(StageStyle.UTILITY)
    Controller controller = $dictionary.getControllerSupplier().getSearcherController(nextStage)
    nextStage.initOwner($stage)
    nextStage.showAndWait()
    if (nextStage.isCommitted()) {
      SearchParameter parameter = nextStage.getResult()
      measureAndSearch(parameter)
      $searchHistory.add(parameter)
    }
  }

  public void searchScript() {
    UtilityStage<ScriptSearchParameter> nextStage = UtilityStage.new(StageStyle.UTILITY)
    ScriptController controller = ScriptController.new(nextStage)
    nextStage.initOwner($stage)
    nextStage.showAndWait()
    if (nextStage.isCommitted()) {
      try {
        ScriptSearchParameter parameter = nextStage.getResult()
        measureAndSearch(parameter)
      } catch (ScriptException | AccessControlException | PrivilegedActionException exception) {
        outputStackTrace(exception, Launcher.BASE_PATH + SCRIPT_EXCEPTION_OUTPUT_PATH)
        showErrorDialog("failSearchScript")
      } catch (NoSuchScriptEngineException exception) {
        showErrorDialog("missingScriptEngine")
      }
    }
  }

  public void searchPrevious() {
    SearchParameter parameter = $searchHistory.previous()
    if (parameter != null) {
      if (parameter instanceof NormalSearchParameter) {
        String search = parameter.getSearch()
        SearchMode searchMode = parameter.getSearchMode()
        Boolean strict = parameter.isStrict()
        $searchControl.setText(search)
        $searchModeControl.setValue(searchMode)
        $searchTypeControl.setSelected(strict)
        $previousSearch = search
        measureAndSearch(parameter)
      } else if (parameter instanceof DetailedSearchParameter) {
        measureAndSearch(parameter)
      }
    }
  }

  public void searchNext() {
    SearchParameter parameter = $searchHistory.next()
    if (parameter != null) {
      if (parameter instanceof NormalSearchParameter) {
        String search = parameter.getSearch()
        SearchMode searchMode = parameter.getSearchMode()
        Boolean strict = parameter.isStrict()
        $searchControl.setText(search)
        $searchModeControl.setValue(searchMode)
        $searchTypeControl.setSelected(strict)
        $previousSearch = search
        measureAndSearch(parameter)
      } else if (parameter instanceof DetailedSearchParameter) {
        measureAndSearch(parameter)
      }
    }
  }

  public void searchSentence() {
    UtilityStage<Void> nextStage = UtilityStage.new(StageStyle.UTILITY)
    SentenceSearcherController controller = SentenceSearcherController.new(nextStage)
    Boolean keepsEditorOnTop = Setting.getInstance().getKeepsEditorOnTop()
    if (keepsEditorOnTop) {
      nextStage.initOwner($stage)
    }
    controller.prepare($dictionary.copy())
    $openStages.add(nextStage)
    nextStage.showAndWait()
    $openStages.remove(nextStage)
  }

  public void shuffleWords() {
    $dictionary.shuffleWords()
  }

  public void measureAndSearch(SearchParameter parameter) {
    Long beforeTime = System.nanoTime()
    $dictionary.search(parameter)
    Long afterTime = System.nanoTime()
    Long elapsedTime = (Long)(afterTime - beforeTime).intdiv(1000000)
    $elapsedTimeLabel.setText(elapsedTime.toString())
    $hitWordSizeLabel.setText($dictionary.hitWordSize().toString())
    $totalWordSizeLabel.setText($dictionary.totalWordSize().toString())
    $wordView.scrollTo(0)
  }

  public void changeSearchModeToWord() {
    $searchModeControl.setValue(SearchMode.NAME)
    focusSearchControl()
  }

  public void changeSearchModeToEquivalent() {
    $searchModeControl.setValue(SearchMode.EQUIVALENT)
    focusSearchControl()
  }

  public void changeSearchModeToContent() {
    $searchModeControl.setValue(SearchMode.CONTENT)
    focusSearchControl()
  }

  public void toggleSearchType() {
    if (!$searchTypeControl.isDisable()) {
      $searchTypeControl.setSelected(!$searchTypeControl.isSelected())
      focusSearchControl()
    }
  }

  @FXML
  public void focusSearchControl() {
    $searchControl.requestFocus()
    searchNormal(true)
  }

  private void modifyWord(Element word) {
    if ($dictionary instanceof EditableDictionary) {
      if (word != null && word instanceof Word) {
        Boolean keepsEditorOnTop = Setting.getInstance().getKeepsEditorOnTop()
        Word oldWord = $dictionary.copyWord(word)
        UtilityStage<WordEditResult> nextStage = UtilityStage.new(StageStyle.UTILITY)
        Controller controller = $dictionary.getEditorControllerSupplier().getEditorController(nextStage, word, $temporarySetting)
        if (keepsEditorOnTop) {
          nextStage.initOwner($stage)
        }
        $openStages.add(nextStage)
        nextStage.showAndWait()
        $openStages.remove(nextStage)
        if (nextStage.isCommitted()) {
          WordEditResult result = nextStage.getResult()
          $dictionary.modifyWord(oldWord, word)
          if (result.getRemovedWord() != null) {
            $dictionary.mergeWord((Word)word, result.getRemovedWord())
          }
          $wordView.refresh()
        }
      }
    }
  }

  @FXML
  public void modifyWords() {
    if ($dictionary instanceof EditableDictionary) {
      List<Element> words = $wordView.getSelectionModel().getSelectedItems()
      for (Element word : words) {
        Element cachedWord = word
        Platform.runLater() {
          modifyWord(cachedWord)
        }
      }
    }
  }

  private void removeWord(Element word) {
    if ($dictionary instanceof EditableDictionary) {
      if (word != null && word instanceof Word) {
        $dictionary.removeWord(word)
      }
    }
  }

  @FXML
  public void removeWords() {
    if ($dictionary instanceof EditableDictionary) {
      List<Element> candidates = $wordView.getSelectionModel().getSelectedItems()
      List<Word> words = ArrayList.new()
      for (Element candidate : candidates) {
        if (candidate != null && candidate instanceof Word) {
          words.add((Word)candidate)
        }
      }
      $dictionary.removeWords(words)
    }
  }

  public void addWord(String defaultName) {
    if ($dictionary instanceof EditableDictionary) {
      Boolean keepsEditorOnTop = Setting.getInstance().getKeepsEditorOnTop()
      Word newWord = $dictionary.createWord(defaultName)
      UtilityStage<WordEditResult> nextStage = UtilityStage.new(StageStyle.UTILITY)
      Controller controller = $dictionary.getEditorControllerSupplier().getCreatorController(nextStage, newWord, $temporarySetting)      
      if (keepsEditorOnTop) {
        nextStage.initOwner($stage)
      }
      $openStages.add(nextStage)
      nextStage.showAndWait()
      $openStages.remove(nextStage)
      if (nextStage.isCommitted()) {
        WordEditResult result = nextStage.getResult()
        $dictionary.addWord(newWord)
        if (result.getRemovedWord() != null) {
          $dictionary.mergeWord(newWord, result.getRemovedWord())
        }
      }
    }
  }

  @FXML
  public void addWord() {
    String defaultName = $searchControl.getText()
    addWord(defaultName)
  }

  private void addInheritedWord(Element word) {
    if ($dictionary instanceof EditableDictionary) {
      if (word != null && word instanceof Word) {
        Boolean keepsEditorOnTop = Setting.getInstance().getKeepsEditorOnTop()
        Word newWord = $dictionary.inheritWord(word)
        UtilityStage<WordEditResult> nextStage = UtilityStage.new(StageStyle.UTILITY)
        Controller controller = $dictionary.getEditorControllerSupplier().getEditorController(nextStage, newWord, $temporarySetting)
        if (keepsEditorOnTop) {
          nextStage.initOwner($stage)
        }
        $openStages.add(nextStage)
        nextStage.showAndWait()
        $openStages.remove(nextStage)
        if (nextStage.isCommitted()) {
          WordEditResult result = nextStage.getResult()
          $dictionary.addWord(newWord)
          if (result.getRemovedWord() != null) {
            $dictionary.mergeWord(newWord, result.getRemovedWord())
          }
        }
      }
    }
  }

  @FXML
  public void addInheritedWords() {
    if ($dictionary instanceof EditableDictionary) {
      List<Element> words = $wordView.getSelectionModel().getSelectedItems()
      for (Element word : words) {
        Element cachedWord = word
        Platform.runLater() {
          addInheritedWord(cachedWord)
        }
      }
    }
  }

  public void addGeneratedWords() {
    if ($dictionary instanceof EditableDictionary) {
      UtilityStage<NameGeneratorController.Result> nextStage = UtilityStage.new(StageStyle.UTILITY)
      NameGeneratorController controller = NameGeneratorController.new(nextStage)
      nextStage.initModality(Modality.APPLICATION_MODAL)
      nextStage.initOwner($stage)
      controller.prepare(true, $temporarySetting.getGeneratorConfig())
      nextStage.showAndWait()
      if (nextStage.isCommitted()) {
        NameGeneratorController.Result result = nextStage.getResult()
        List<Word> newWords = ArrayList.new()
        for (Int i = 0 ; i < result.getPseudoWords().size() ; i ++) {
          PseudoWord pseudoWord = result.getPseudoWords()[i]
          String name = result.getNames()[i]
          Word newWord = $dictionary.determineWord(name, pseudoWord)
          newWords.add(newWord)
        }
        SearchParameter parameter = SelectionSearchParameter.new(newWords)
        $dictionary.addWords(newWords)
        measureAndSearch(parameter)
        $searchHistory.add(parameter)
        $temporarySetting.setGeneratorConfig(controller.createConfig())
      }
    }
  }

  private void cutOrCopyWords(Boolean copy) {
    if ($dictionary instanceof EditableDictionary) {
      List<Element> candidates = $wordView.getSelectionModel().getSelectedItems()
      List<Word> words = ArrayList.new()
      for (Element candidate : candidates) {
        if (candidate instanceof Word) {
          words.add((Word)candidate)
        }
      }
      Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard()
      WordSelection contents = WordSelection.new(words)
      clipboard.setContents(contents, contents)
      if (!copy) {
        $dictionary.removeWords(words)
      }
    }
  }

  @FXML
  public void cutWords() {
    cutOrCopyWords(false)
  }

  @FXML
  public void copyWords() {
    cutOrCopyWords(true)
  }

  @FXML
  public void pasteWords() {
    if ($dictionary instanceof EditableDictionary) {
      Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard()
      Class<?> wordClass = calculateWordClass()
      try {
        List<Word> candidates = (List<Word>)clipboard.getData(WordSelection.WORD_FLAVOR)
        List<Word> words = ArrayList.new()
        for (Word candidate : candidates) {
          if (wordClass != null && wordClass.isInstance(candidate)) {
            Word word = $dictionary.copyWord(candidate)
            words.add(word)
          }
        }
        $dictionary.addWords(words)
      } catch (UnsupportedFlavorException | IOException exception) {
      }
    }
  }

  public Class<?> calculateWordClass() {
    Class<?> wordClass = null
    for (Type type : $dictionary.getClass().getGenericInterfaces()) {
      if (type instanceof ParameterizedType) {
        Type rawType = ((ParameterizedType)type).getRawType()
        Type typeArgument = ((ParameterizedType)type).getActualTypeArguments()[0]
        if (rawType == EditableDictionary) {
          wordClass = (Class)typeArgument
        }
      }
    }
    return wordClass
  }

  private void cancelLoadDictionary() {
    Task<?> loader = $dictionary.getLoader()
    if (loader.isRunning()) {
      loader.cancel()
    }
  }

  private void updateLoader() {
    Task<?> loader = $dictionary.getLoader()
    $loadingBox.visibleProperty().bind(Bindings.notEqual(Worker.State.SUCCEEDED, loader.stateProperty()))
    $progressIndicator.progressProperty().bind(loader.progressProperty())
    loader.addEventHandler(WorkerStateEvent.WORKER_STATE_SUCCEEDED) { WorkerStateEvent event ->
      $wordView.setItems($dictionary.getWholeWords())
      $searchControl.requestFocus()
      searchNormal(true)
    }
    loader.addEventHandler(WorkerStateEvent.WORKER_STATE_FAILED) { WorkerStateEvent event ->
      $tab.requestClose()
      failUpdateDictionary(event.getSource().getException())
    }
  }

  private void updateOnLinkClicked() {
    $dictionary.setOnLinkClicked() { SearchParameter parameter ->
      measureAndSearch(parameter)
      $searchHistory.add(parameter)
    }
  }

  private void failUpdateDictionary(Throwable throwable) {
    outputStackTrace(throwable, Launcher.BASE_PATH + EXCEPTION_OUTPUT_PATH)
    showErrorDialog("failUpdateDictionary")
  }

  public void focusWordList() {
    $wordView.requestFocus()
    if ($wordView.getSelectionModel().getSelectedItems().isEmpty()) {
      $wordView.getSelectionModel().selectFirst()
      $wordView.scrollTo(0)
    }
  }

  private void closeOpenStages() {
    for (Stage stage : $openStages) {
      stage.close()
    }
    $openStages.clear()
  }

  public Boolean requestClose() {
    Boolean allowsClose = checkDictionaryChange()
    if (allowsClose) {
      cancelLoadDictionary()
      closeOpenStages()
      return true
    } else {
      return false
    }
  }

  private Boolean checkDictionaryChange() {
    Boolean savesAutomatically = Setting.getInstance().getSavesAutomatically()
    if ($individualSetting != null) {
      $individualSetting.save()
    }
    if ($dictionary.isChanged()) {
      if (!savesAutomatically) {
        Dialog dialog = Dialog.new(StageStyle.UTILITY)
        dialog.initOwner($stage)
        dialog.setTitle(DIALOG_RESOURCES.getString("title.checkDictionaryChange"))
        dialog.setContentText(MessageFormat.format(DIALOG_RESOURCES.getString("contentText.checkDictionaryChange"), $dictionary.getName()))
        dialog.setCommitText(DIALOG_RESOURCES.getString("commitText.checkDictionaryChange"))
        dialog.setNegateText(DIALOG_RESOURCES.getString("cancelText.checkDictionaryChange"))
        dialog.setAllowsNegate(true)
        dialog.showAndWait()
        if (dialog.isCommitted()) {
          $dictionary.save()
          if ($dictionary.getSaver() != null) {
            return true
          } else {
            showErrorDialog("saveUnsupported")
            return false
          }
        } else if (dialog.isNegated()) {
          return true
        } else {
          return false
        }
      } else {
        $dictionary.save()
        return true
      }
    } else {
      return true
    }
  }

  @VoidClosure
  private void setupWordView() {
    $wordView.setCellFactory() { ListView<Element> view ->
      WordCell cell = WordCell.new()
      cell.addEventHandler(MouseEvent.MOUSE_CLICKED) { MouseEvent event ->
        if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
          modifyWord(cell.getItem())
        }
      }
      return cell
    }
    $wordView.addEventHandler(MouseEvent.MOUSE_CLICKED) { MouseEvent event ->
      if (event.getButton() == MouseButton.SECONDARY) {
        $wordView.setContextMenu($editMenu)
      }
    }
    $wordView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE)
  }

  private void setupWordViewShortcut() {
    $wordView.addEventHandler(KeyEvent.KEY_PRESSED) { KeyEvent event ->
      if (event.getCode() == KeyCode.ENTER) {
        modifyWords()
      }
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

  private void setupSearchTypeControl() {
    Callable<String> textFunction = (Callable){
      return ($searchTypeControl.isSelected()) ? "完全一致" : "部分一致"
    }
    Callable<BooleanClass> disableFunction = (Callable){
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

  private void setupSearchHistory() {
    Int separativeInterval = Setting.getInstance().getSeparativeInterval()
    $searchHistory.setSeparativeInterval(separativeInterval)
  }

  private void loadOriginalResource() {
    FXMLLoader loader = FXMLLoader.new(getClass().getClassLoader().getResource(RESOURCE_PATH), null, CustomBuilderFactory.new())
    loader.setController(this)
    Node root = (Node)loader.load()
    $tab.setContent(root)
  }

  public Dictionary getDictionary() {
    return $dictionary
  }

  public IndividualSetting getIndividualSetting() {
    return $individualSetting
  }

}