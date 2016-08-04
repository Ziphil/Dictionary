package ziphil.controller

import groovy.transform.CompileStatic
import javafx.fxml.FXML
import javafx.scene.control.ComboBox
import javafx.scene.control.TextField
import ziphil.custom.Measurement
import ziphil.custom.UtilityStage
import ziphil.dictionary.SearchType
import ziphil.dictionary.SlimeDictionary
import ziphil.dictionary.SlimeSearchParameter


@CompileStatic @Newify
public class SlimeSearcherController extends Controller<SlimeSearchParameter> {

  private static final String RESOURCE_PATH = "resource/fxml/slime_searcher.fxml"
  private static final String TITLE = "高度な検索"
  private static final Double DEFAULT_WIDTH = Measurement.rpx(640)
  private static final Double DEFAULT_HEIGHT = -1

  @FXML private TextField $id
  @FXML private TextField $name
  @FXML private ComboBox<String> $nameSearchType
  @FXML private TextField $equivalentName
  @FXML private ComboBox<String> $equivalentTitle
  @FXML private ComboBox<String> $equivalentSearchType
  @FXML private TextField $informationText
  @FXML private ComboBox<String> $informationTitle
  @FXML private ComboBox<String> $informationSearchType
  @FXML private ComboBox<String> $tag
  private SlimeDictionary $dictionary

  public SlimeSearcherController(UtilityStage<SlimeSearchParameter> stage) {
    super(stage)
    loadResource(RESOURCE_PATH, TITLE, DEFAULT_WIDTH, DEFAULT_HEIGHT, false)
  }

  public void prepare(SlimeDictionary dictionary) {
    $dictionary = dictionary
    setupTitles()
  }

  @FXML
  protected void commit() {
    SlimeSearchParameter parameter = SlimeSearchParameter.new()
    if ($id.getText() != "") {
      parameter.setId($id.getText().toInteger())
    }
    if ($name.getText() != "") {
      parameter.setName($name.getText())
    }
    parameter.setNameSearchType(SearchType.valueOfExplanation($nameSearchType.getValue()))
    if ($equivalentName.getText() != "") {
      parameter.setEquivalentName($equivalentName.getText())
    }
    if ($equivalentTitle.getValue() != "") {
      parameter.setEquivalentTitle($equivalentTitle.getValue())
    } 
    parameter.setEquivalentSearchType(SearchType.valueOfExplanation($equivalentSearchType.getValue()))
    if ($informationText.getText() != "") {
      parameter.setInformationText($informationText.getText())
    }
    if ($informationTitle.getValue() != "") {
      parameter.setInformationTitle($informationTitle.getValue())
    }
    parameter.setInformationSearchType(SearchType.valueOfExplanation($informationSearchType.getValue()))
    if ($tag.getValue() != "") {
      parameter.setTag($tag.getValue())
    }
    $stage.close(parameter)
  }

  private void setupTitles() {
    $equivalentTitle.getItems().addAll($dictionary.registeredEquivalentTitles())
    $informationTitle.getItems().addAll($dictionary.registeredInformationTitles())
    $tag.getItems().addAll($dictionary.registeredTags())
  }

}