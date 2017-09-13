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
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.concurrent.Task
import javafx.concurrent.Worker
import javafx.concurrent.WorkerStateEvent
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
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
public class MainWordListController {

  private static final String RESOURCE_PATH = "resource/fxml/controller/main_word_list.fxml"
  private static final String EXCEPTION_OUTPUT_PATH = "data/log/exception.txt"
  private static final String SCRIPT_EXCEPTION_OUTPUT_PATH = "data/log/script_exception.txt"

  @FXML private ContextMenu $editMenu
  @FXML private MenuItem $addWordContextItem
  @FXML private MenuItem $addInheritedWordContextItem
  @FXML private MenuItem $modifyWordContextItem
  @FXML private MenuItem $removeWordContextItem
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
  private Stage $stage
  private Tab $tab
  private Dictionary $dictionary
  private IndividualSetting $individualSetting = null
  private TemporarySetting $temporarySetting = null
  private SearchHistory $searchHistory = SearchHistory.new()
  private String $previousSearch = ""
  private List<Stage> $openStages = Collections.synchronizedList(ArrayList.new())

  public MainWordListController(Stage stage, Tab tab) {
    $stage = stage
    $tab = tab
    loadOriginalResource()
    setupSearchHistory()
  }

  @FXML
  public void initialize() {
    setupWordView()
    setupSearchControl()
    setupSearchTypeControl()
  }

  public void update(Dictionary dictionary) {
    $dictionary = dictionary
    $individualSetting = Dictionaries.createIndividualSetting(dictionary)
    $temporarySetting = TemporarySetting.new()
    updateSearchStatuses()
    updateLoader()
    updateOnLinkClicked()
  }

  public Boolean close() {
    Boolean allowsClose = checkDictionaryChange()
    if (allowsClose) {
      cancelLoadDictionary()
      closeOpenStages()
      return true
    } else {
      return false
    }
  }

  public void searchNormal(Boolean forcesSearch) {
    if ($dictionary != null) {
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
  }

  public void searchNormal() {
    searchNormal(false)
  }

  public void searchDetail() {
    if ($dictionary != null) {
      if ($dictionary instanceof ShaleiaDictionary) {
        UtilityStage<ShaleiaSearchParameter> nextStage = UtilityStage.new(StageStyle.UTILITY)
        ShaleiaSearcherController controller = ShaleiaSearcherController.new(nextStage)
        nextStage.initOwner($stage)
        nextStage.showAndWait()
        if (nextStage.isCommitted()) {
          ShaleiaSearchParameter parameter = nextStage.getResult()
          measureAndSearch(parameter)
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
          measureAndSearch(parameter)
          $searchHistory.add(parameter)
        }
      }
    }
  }

  public void searchScript() {
    if ($dictionary != null) {
      UtilityStage<ScriptSearchParameter> nextStage = UtilityStage.new(StageStyle.UTILITY)
      ScriptController controller = ScriptController.new(nextStage)
      nextStage.initOwner($stage)
      nextStage.showAndWait()
      if (nextStage.isCommitted()) {
        try {
          ScriptSearchParameter parameter = nextStage.getResult()
          measureAndSearch(parameter)
        } catch (ScriptException | AccessControlException | PrivilegedActionException exception) {
          PrintWriter writer = PrintWriter.new(Launcher.BASE_PATH + SCRIPT_EXCEPTION_OUTPUT_PATH)
          Dialog dialog = Dialog.new(StageStyle.UTILITY)
          dialog.initOwner($stage)
          dialog.setTitle("実行エラー")
          dialog.setContentText("スクリプト実行中にエラーが発生しました。詳細はエラーログを確認してください。")
          dialog.setAllowsCancel(false)
          exception.printStackTrace(writer)
          writer.flush()
          writer.close()
          dialog.showAndWait()
        } catch (NoSuchScriptEngineException exception) {
          Dialog dialog = Dialog.new(StageStyle.UTILITY)
          dialog.initOwner($stage)
          dialog.setTitle("スクリプトエンジンエラー")
          dialog.setContentText("指定されたスクリプトエンジンが見つかりません。実行用のjarファイルがライブラリに追加されているか確認してください。")
          dialog.setAllowsCancel(false)
          dialog.showAndWait()
        }
      }
    }
  }

  public void searchPrevious() {
    if ($dictionary != null) {
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
  }

  public void searchNext() {
    if ($dictionary != null) {
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
  }

  public void searchSentence() {
    if ($dictionary != null) {
      UtilityStage<Void> nextStage = UtilityStage.new(StageStyle.UTILITY)
      SentenceSearcherController controller = SentenceSearcherController.new(nextStage)
      nextStage.initModality(Modality.APPLICATION_MODAL)
      nextStage.initOwner($stage)
      controller.prepare($dictionary.copy())
      nextStage.showAndWait()
    }
  }

  public void shuffleWords() {
    if ($dictionary != null) {
      $dictionary.shuffleWords()
    }
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

  @FXML
  private void changeSearchMode() {
    $searchControl.requestFocus()
    searchNormal(true)
  }

  public void changeSearchModeToWord() {
    $searchModeControl.setValue(SearchMode.NAME)
    changeSearchMode()
  }

  public void changeSearchModeToEquivalent() {
    $searchModeControl.setValue(SearchMode.EQUIVALENT)
    changeSearchMode()
  }

  public void changeSearchModeToContent() {
    $searchModeControl.setValue(SearchMode.CONTENT)
    changeSearchMode()
  }

  public void changeSearchType() {
    if (!$searchTypeControl.isDisable()) {
      $searchTypeControl.setSelected(!$searchTypeControl.isSelected())
      searchNormal(true)
    }
  }

  @FXML
  private void toggleSearchType() {
    $searchControl.requestFocus()
    searchNormal(true)
  }

  public void modifyWord(Element word) {
    if ($dictionary != null && $dictionary instanceof EditableDictionary) {
      if (word != null && word instanceof Word) {
        UtilityStage<WordEditResult> nextStage = UtilityStage.new(StageStyle.UTILITY)
        Boolean keepsEditorOnTop = Setting.getInstance().getKeepsEditorOnTop()
        if (keepsEditorOnTop) {
          nextStage.initOwner($stage)
        }
        Word oldWord = $dictionary.copyWord(word)
        if ($dictionary instanceof ShaleiaDictionary && word instanceof ShaleiaWord) {
          ShaleiaEditorController controller = ShaleiaEditorController.new(nextStage)
          controller.prepare(word)
        } else if ($dictionary instanceof PersonalDictionary && word instanceof PersonalWord) {
          PersonalEditorController controller = PersonalEditorController.new(nextStage)
          controller.prepare(word)
        } else if ($dictionary instanceof SlimeDictionary && word instanceof SlimeWord) {
          SlimeEditorController controller = SlimeEditorController.new(nextStage)
          controller.prepare(word, $dictionary, $temporarySetting)
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

  public void modifyWord() {
    Element word = $wordView.getSelectionModel().getSelectedItem()
    modifyWord(word)
  }

  public void removeWord(Element word) {
    if ($dictionary != null && $dictionary instanceof EditableDictionary) {
      if (word != null && word instanceof Word) {
        $dictionary.removeWord(word)
      }
    }
  }

  public void removeWord() {
    Element word = $wordView.getSelectionModel().getSelectedItem()
    removeWord(word)
  }

  public void addWord() {
    if ($dictionary != null && $dictionary instanceof EditableDictionary) {
      Word newWord
      UtilityStage<WordEditResult> nextStage = UtilityStage.new(StageStyle.UTILITY)
      Boolean keepsEditorOnTop = Setting.getInstance().getKeepsEditorOnTop()
      if (keepsEditorOnTop) {
        nextStage.initOwner($stage)
      }
      String defaultName = $searchControl.getText()
      if ($dictionary instanceof ShaleiaDictionary) {
        ShaleiaEditorController controller = ShaleiaEditorController.new(nextStage)
        ShaleiaWord localNewWord = $dictionary.createWord(defaultName)
        newWord = localNewWord
        controller.prepare(localNewWord, true)
      } else if ($dictionary instanceof PersonalDictionary) {
        PersonalEditorController controller = PersonalEditorController.new(nextStage)
        PersonalWord localNewWord = $dictionary.createWord(defaultName)
        newWord = localNewWord
        controller.prepare(localNewWord, true)
      } else if ($dictionary instanceof SlimeDictionary) {
        SlimeEditorController controller = SlimeEditorController.new(nextStage)
        SlimeWord localNewWord = $dictionary.createWord(defaultName)
        newWord = localNewWord
        controller.prepare(localNewWord, $dictionary, $temporarySetting, true)
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

  public void addInheritedWord(Element word) {
    if ($dictionary != null && $dictionary instanceof EditableDictionary) {
      if (word != null && word instanceof Word) {
        Word newWord
        UtilityStage<WordEditResult> nextStage = UtilityStage.new(StageStyle.UTILITY)
        Boolean keepsEditorOnTop = Setting.getInstance().getKeepsEditorOnTop()
        if (keepsEditorOnTop) {
          nextStage.initOwner($stage)
        }
        if ($dictionary instanceof ShaleiaDictionary && word instanceof ShaleiaWord) {
          ShaleiaEditorController controller = ShaleiaEditorController.new(nextStage)
          ShaleiaWord localNewWord = $dictionary.inheritWord(word)
          newWord = localNewWord
          controller.prepare(localNewWord)
        } else if ($dictionary instanceof PersonalDictionary && word instanceof PersonalWord) {
          PersonalEditorController controller = PersonalEditorController.new(nextStage)
          PersonalWord localNewWord = $dictionary.inheritWord(word)
          newWord = localNewWord
          controller.prepare(localNewWord)
        } else if ($dictionary instanceof SlimeDictionary && word instanceof SlimeWord) {
          SlimeEditorController controller = SlimeEditorController.new(nextStage)
          SlimeWord localNewWord = $dictionary.inheritWord(word)
          newWord = localNewWord
          controller.prepare(localNewWord, $dictionary, $temporarySetting)
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

  public void addInheritedWord() {
    Element word = $wordView.getSelectionModel().getSelectedItem()
    addInheritedWord(word)
  }

  public void addGeneratedWords() {
    if ($dictionary != null && $dictionary instanceof EditableDictionary) {
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

  private void cancelLoadDictionary() {
    if ($dictionary != null) {
      Task<?> loader = $dictionary.getLoader()
      if (loader.isRunning()) {
        loader.cancel()
      }
    }
  }

  private void updateSearchStatuses() {
    $dictionaryNameLabel.setText($dictionary.getName())
    $searchControl.requestFocus()
  }

  private void updateLoader() {
    Task<?> loader = $dictionary.getLoader()
    $loadingBox.visibleProperty().bind(Bindings.notEqual(Worker.State.SUCCEEDED, loader.stateProperty()))
    $progressIndicator.progressProperty().bind(loader.progressProperty())
    loader.addEventHandler(WorkerStateEvent.WORKER_STATE_SUCCEEDED) { WorkerStateEvent event ->
      $wordView.setItems($dictionary.getWholeWords())
      searchNormal(true)
    }
    loader.addEventHandler(WorkerStateEvent.WORKER_STATE_FAILED) { WorkerStateEvent event ->
      $wordView.setItems(null)
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
    PrintWriter writer = PrintWriter.new(Launcher.BASE_PATH + EXCEPTION_OUTPUT_PATH)
    String name = throwable.getClass().getSimpleName()
    Dialog dialog = Dialog.new(StageStyle.UTILITY)
    dialog.initOwner($stage)
    dialog.setTitle("読み込みエラー")
    dialog.setContentText("エラーが発生しました(${name})。データが壊れている可能性があります。詳細はエラーログを確認してください。")
    dialog.setAllowsCancel(false)
    throwable.printStackTrace()
    throwable.printStackTrace(writer)
    writer.flush()
    writer.close()
    dialog.showAndWait()
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

  private Boolean checkDictionaryChange() {
    Boolean savesAutomatically = Setting.getInstance().getSavesAutomatically()
    if ($individualSetting != null) {
      $individualSetting.save()
    }
    if ($dictionary != null) {
      if ($dictionary.isChanged()) {
        if (!savesAutomatically) {
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
          $dictionary.save()
          return true
        }
      } else {
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

  private void setupWordViewShortcuts() {
    $wordView.addEventHandler(KeyEvent.KEY_PRESSED) { KeyEvent event ->
      if (event.getCode() == KeyCode.ENTER) {
        modifyWord()
      }
    }
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