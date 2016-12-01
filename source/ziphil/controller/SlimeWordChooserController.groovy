package ziphil.controller

import groovy.transform.CompileStatic
import java.util.concurrent.Callable
import javafx.beans.binding.Bindings
import javafx.beans.binding.BooleanBinding
import javafx.beans.binding.StringBinding
import javafx.fxml.FXML
import javafx.scene.control.ComboBox
import javafx.scene.control.ListView
import javafx.scene.control.TextField
import javafx.scene.control.ToggleButton
import javafx.scene.input.MouseButton
import javafx.scene.input.MouseEvent
import ziphil.custom.Measurement
import ziphil.custom.SimpleWordCell
import ziphil.custom.UtilityStage
import ziphil.dictionary.slime.SlimeDictionary
import ziphil.dictionary.slime.SlimeWord
import ziphil.module.Setting
import ziphilib.transform.VoidClosure
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class SlimeWordChooserController extends Controller<SlimeWord> {

  private static final String RESOURCE_PATH = "resource/fxml/slime_word_chooser.fxml"
  private static final String TITLE = "単語選択"
  private static final Double DEFAULT_WIDTH = Measurement.rpx(480)
  private static final Double DEFAULT_HEIGHT = Measurement.rpx(320)

  @FXML private ListView<SlimeWord> $wordListView
  @FXML private TextField $searchControl
  @FXML private ComboBox<String> $searchModeControl
  @FXML private ToggleButton $searchTypeControl
  private SlimeDictionary $dictionary

  public SlimeWordChooserController(UtilityStage<SlimeWord> stage) {
    super(stage)
    loadResource(RESOURCE_PATH, TITLE, DEFAULT_WIDTH, DEFAULT_HEIGHT)
  }

  public void prepare(SlimeDictionary dictionary) {
    $dictionary = dictionary
    setupWordListView()
    bindSearchTypeControlProperty()
  }

  @FXML
  private void search() {
    if ($dictionary != null) {
      String search = $searchControl.getText()
      String searchMode = $searchModeControl.getValue()
      Boolean isStrict = $searchTypeControl.getText() == "完全一致"
      if (searchMode == "単語") {
        $dictionary.searchByName(search, isStrict)
      } else if (searchMode == "訳語") {
        $dictionary.searchByEquivalent(search, isStrict)
      } else if (searchMode == "全文") {
        $dictionary.searchByContent(search)
      }
      $wordListView.scrollTo(0)
    }
  }

  @FXML
  private void changeSearchMode() {
    $searchControl.setText("")
    $searchControl.requestFocus()
    search()
  }

  @FXML
  private void toggleSearchType() {
    $searchControl.requestFocus()
    search()
  }

  @FXML
  protected void commit() {
    SlimeWord word = $wordListView.getSelectionModel().getSelectedItem()
    $stage.close(word)
  }

  @VoidClosure
  private void setupWordListView() {
    $wordListView.setItems($dictionary.getWords())
    $wordListView.setCellFactory() { ListView<SlimeWord> list ->
      SimpleWordCell cell = SimpleWordCell.new()
      cell.addEventHandler(MouseEvent.MOUSE_CLICKED) { MouseEvent event ->
        if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
          commit()
        }
      }
      return cell
    }
  }

  private void bindSearchTypeControlProperty() {
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

}