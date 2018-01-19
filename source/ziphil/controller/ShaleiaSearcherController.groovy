package ziphil.controller

import groovy.transform.CompileStatic
import javafx.fxml.FXML
import javafx.scene.control.ComboBox
import javafx.scene.control.TextField
import ziphil.custom.Measurement
import ziphil.custom.UtilityStage
import ziphil.dictionary.SearchType
import ziphil.dictionary.shaleia.ShaleiaSearchParameter
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class ShaleiaSearcherController extends Controller<ShaleiaSearchParameter> {

  private static final String RESOURCE_PATH = "resource/fxml/controller/shaleia_searcher.fxml"
  private static final String TITLE = "高度な検索"
  private static final Double DEFAULT_WIDTH = Measurement.rpx(480)
  private static final Double DEFAULT_HEIGHT = -1

  @FXML private TextField $nameControl
  @FXML private ComboBox<SearchType> $nameSearchTypeControl
  @FXML private TextField $equivalentControl
  @FXML private ComboBox<SearchType> $equivalentSearchTypeControl
  @FXML private TextField $descriptionControl
  @FXML private ComboBox<SearchType> $descriptionSearchTypeControl

  public ShaleiaSearcherController(UtilityStage<? super ShaleiaSearchParameter> stage) {
    super(stage)
    loadResource(RESOURCE_PATH, TITLE, DEFAULT_WIDTH, DEFAULT_HEIGHT, false)
  }

  @FXML
  protected void commit() {
    ShaleiaSearchParameter parameter = ShaleiaSearchParameter.new()
    if (!$nameControl.getText().isEmpty()) {
      parameter.setHasName(true)
      parameter.setName($nameControl.getText())
      parameter.setNameSearchType($nameSearchTypeControl.getValue())
    }
    if (!$equivalentControl.getText().isEmpty()) {
      parameter.setHasEquivalent(true)
      parameter.setEquivalent($equivalentControl.getText())
      parameter.setEquivalentSearchType($equivalentSearchTypeControl.getValue())
    }
    if (!$descriptionControl.getText().isEmpty()) {
      parameter.setHasDescription(true)
      parameter.setDescription($descriptionControl.getText())
      parameter.setDescriptionSearchType($descriptionSearchTypeControl.getValue())
    }
    $stage.commit(parameter)
  }

}