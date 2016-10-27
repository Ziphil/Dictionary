package ziphil.controller

import groovy.transform.CompileStatic
import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.scene.control.ComboBox
import javafx.scene.control.TextField
import ziphil.custom.Measurement
import ziphil.custom.UtilityStage
import ziphil.dictionary.SearchType
import ziphil.dictionary.ShaleiaSearchParameter


@CompileStatic @Newify
public class ShaleiaSearcherController extends Controller<ShaleiaSearchParameter> {

  private static final String RESOURCE_PATH = "resource/fxml/shaleia_searcher.fxml"
  private static final String TITLE = "高度な検索"
  private static final Double DEFAULT_WIDTH = Measurement.rpx(480)
  private static final Double DEFAULT_HEIGHT = -1

  @FXML private TextField $nameControl
  @FXML private ComboBox<String> $nameSearchTypeControl

  public ShaleiaSearcherController(UtilityStage<ShaleiaSearchParameter> stage) {
    super(stage)
    loadResource(RESOURCE_PATH, TITLE, DEFAULT_WIDTH, DEFAULT_HEIGHT, false)
  }

  @FXML
  protected void commit() {
    ShaleiaSearchParameter parameter = ShaleiaSearchParameter.new()
    parameter.setName($nameControl.getText())
    parameter.setNameSearchType(SearchType.valueOfExplanation($nameSearchTypeControl.getValue()))
    $stage.close(parameter)
  }

}