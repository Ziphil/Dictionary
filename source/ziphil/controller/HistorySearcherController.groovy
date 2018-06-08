package ziphil.controller

import groovy.transform.CompileStatic
import javafx.fxml.FXML
import javafx.scene.control.ListView
import ziphil.custom.Measurement
import ziphil.custom.UtilityStage
import ziphil.dictionary.SearchHistory
import ziphil.dictionary.SearchParameter
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class HistorySearcherController extends Controller<SearchParameter> {

  private static final String RESOURCE_PATH = "resource/fxml/controller/history_searcher.fxml"
  private static final String TITLE = "検索履歴から検索"
  private static final Double DEFAULT_WIDTH = Measurement.rpx(240)
  private static final Double DEFAULT_HEIGHT = Measurement.rpx(360)

  @FXML private ListView<SearchParameter> $parameterView

  public HistorySearcherController(UtilityStage<? super SearchParameter> stage) {
    super(stage)
    loadResource(RESOURCE_PATH, TITLE, DEFAULT_WIDTH, DEFAULT_HEIGHT, false)
  }

  public void prepare(SearchHistory history) {
    $parameterView.getItems().addAll(history.getParameters())
    $parameterView.getSelectionModel().select(history.getPointer())
  }

  @FXML
  protected void commit() {
    SearchParameter parameter = $parameterView.getSelectionModel().getSelectedItem()
    $stage.commit(parameter)
  }

}