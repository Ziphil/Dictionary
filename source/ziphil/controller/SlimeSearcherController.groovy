package ziphil.controller

import groovy.transform.CompileStatic
import javafx.fxml.FXML
import javafx.scene.control.ComboBox
import javafx.scene.control.TextField
import javafx.scene.control.TextFormatter
import ziphil.custom.IntegerUnaryOperator
import ziphil.custom.Measurement
import ziphil.custom.UtilityStage
import ziphil.dictionary.SearchType
import ziphil.dictionary.slime.SlimeDictionary
import ziphil.dictionary.slime.SlimeSearchParameter
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class SlimeSearcherController extends Controller<SlimeSearchParameter> {

  private static final String RESOURCE_PATH = "resource/fxml/controller/slime_searcher.fxml"
  private static final String TITLE = "高度な検索"
  private static final Double DEFAULT_WIDTH = Measurement.rpx(640)
  private static final Double DEFAULT_HEIGHT = -1

  @FXML private TextField $idControl
  @FXML private TextField $nameControl
  @FXML private ComboBox<String> $nameSearchTypeControl
  @FXML private TextField $equivalentNameControl
  @FXML private ComboBox<String> $equivalentTitleControl
  @FXML private ComboBox<String> $equivalentSearchTypeControl
  @FXML private TextField $informationTextControl
  @FXML private ComboBox<String> $informationTitleControl
  @FXML private ComboBox<String> $informationSearchTypeControl
  @FXML private ComboBox<String> $tagControl
  private SlimeDictionary $dictionary

  public SlimeSearcherController(UtilityStage<SlimeSearchParameter> stage) {
    super(stage)
    loadResource(RESOURCE_PATH, TITLE, DEFAULT_WIDTH, DEFAULT_HEIGHT, false)
  }

  @FXML
  private void initialize() {
    setupIdControl()
  }

  public void prepare(SlimeDictionary dictionary) {
    $dictionary = dictionary
    setupTitleControls()
  }

  @FXML
  protected void commit() {
    SlimeSearchParameter parameter = SlimeSearchParameter.new()
    if ($idControl.getText() != "") {
      parameter.setId($idControl.getText().toInteger())
    }
    if ($nameControl.getText() != "") {
      parameter.setName($nameControl.getText())
    }
    parameter.setNameSearchType(SearchType.valueOfExplanation($nameSearchTypeControl.getValue()))
    if ($equivalentNameControl.getText() != "") {
      parameter.setEquivalentName($equivalentNameControl.getText())
    }
    if ($equivalentTitleControl.getValue() != "") {
      parameter.setEquivalentTitle($equivalentTitleControl.getValue())
    } 
    parameter.setEquivalentSearchType(SearchType.valueOfExplanation($equivalentSearchTypeControl.getValue()))
    if ($informationTextControl.getText() != "") {
      parameter.setInformationText($informationTextControl.getText())
    }
    if ($informationTitleControl.getValue() != "") {
      parameter.setInformationTitle($informationTitleControl.getValue())
    }
    parameter.setInformationSearchType(SearchType.valueOfExplanation($informationSearchTypeControl.getValue()))
    if ($tagControl.getValue() != "") {
      parameter.setTag($tagControl.getValue())
    }
    $stage.commit(parameter)
  }

  private void setupTitleControls() {
    $equivalentTitleControl.getItems().addAll($dictionary.getRegisteredEquivalentTitles())
    $informationTitleControl.getItems().addAll($dictionary.getRegisteredInformationTitles())
    $tagControl.getItems().addAll($dictionary.getRegisteredTags())
  }

  private void setupIdControl() {
    $idControl.setTextFormatter(TextFormatter.new(IntegerUnaryOperator.new()))
  }

}