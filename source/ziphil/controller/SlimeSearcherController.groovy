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
  @FXML private ComboBox<SearchType> $nameSearchTypeControl
  @FXML private TextField $equivalentNameControl
  @FXML private ComboBox<String> $equivalentTitleControl
  @FXML private ComboBox<SearchType> $equivalentSearchTypeControl
  @FXML private TextField $informationTextControl
  @FXML private ComboBox<String> $informationTitleControl
  @FXML private ComboBox<SearchType> $informationSearchTypeControl
  @FXML private ComboBox<String> $tagControl
  private SlimeDictionary $dictionary
  private SlimeSearchParameter $searchParameter

  public SlimeSearcherController(UtilityStage<SlimeSearchParameter> stage) {
    super(stage)
    loadResource(RESOURCE_PATH, TITLE, DEFAULT_WIDTH, DEFAULT_HEIGHT, false)
  }

  @FXML
  private void initialize() {
    setupIdControl()
  }

  public void prepare(SlimeDictionary dictionary, SlimeSearchParameter searchParameter) {
    $dictionary = dictionary
    $searchParameter = searchParameter
    applyDictionary()
    applySearchParameter()
  }

  public void prepare(SlimeDictionary dictionary) {
    prepare(dictionary, null)
  }

  private void applyDictionary() {
    $equivalentTitleControl.getItems().addAll($dictionary.getRegisteredEquivalentTitles())
    $informationTitleControl.getItems().addAll($dictionary.getRegisteredInformationTitles())
    $tagControl.getItems().addAll($dictionary.getRegisteredTags())
  }

  private void applySearchParameter() {
    if ($searchParameter != null) {
      if ($searchParameter.getId() != null) {
        $idControl.setText($searchParameter.getId().toString())
      }
      if ($searchParameter.getName() != null) {
        $nameControl.setText($searchParameter.getName())
      }
      if ($searchParameter.getNameSearchType() != null) {
        $nameSearchTypeControl.setValue($searchParameter.getNameSearchType())
      }
      if ($searchParameter.getEquivalentName() != null) {
        $equivalentNameControl.setText($searchParameter.getEquivalentName())
      }
      if ($searchParameter.getEquivalentTitle() != null) {
        $equivalentTitleControl.setValue($searchParameter.getEquivalentTitle())
      }
      if ($searchParameter.getEquivalentSearchType() != null) {
        $equivalentSearchTypeControl.setValue($searchParameter.getEquivalentSearchType())
      }
      if ($searchParameter.getInformationText() != null) {
        $informationTextControl.setText($searchParameter.getInformationText())
      }
      if ($searchParameter.getInformationTitle() != null) {
        $informationTitleControl.setValue($searchParameter.getInformationTitle())
      }
      if ($searchParameter.getInformationSearchType() != null) {
        $informationSearchTypeControl.setValue($searchParameter.getInformationSearchType())
      }
      if ($searchParameter.getTag() != null) {
        $tagControl.setValue($searchParameter.getTag())
      }
    }
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
    parameter.setNameSearchType($nameSearchTypeControl.getValue())
    if ($equivalentNameControl.getText() != "") {
      parameter.setEquivalentName($equivalentNameControl.getText())
    }
    if ($equivalentTitleControl.getValue() != "") {
      parameter.setEquivalentTitle($equivalentTitleControl.getValue())
    } 
    parameter.setEquivalentSearchType($equivalentSearchTypeControl.getValue())
    if ($informationTextControl.getText() != "") {
      parameter.setInformationText($informationTextControl.getText())
    }
    if ($informationTitleControl.getValue() != "") {
      parameter.setInformationTitle($informationTitleControl.getValue())
    }
    parameter.setInformationSearchType($informationSearchTypeControl.getValue())
    if ($tagControl.getValue() != "") {
      parameter.setTag($tagControl.getValue())
    }
    $stage.commit(parameter)
  }

  private void setupIdControl() {
    $idControl.setTextFormatter(TextFormatter.new(IntegerUnaryOperator.new()))
  }

}