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
  @FXML private TextField $equivalent
  @FXML private ComboBox<String> $equivalentTitle
  @FXML private ComboBox<String> $equivalentSearchType
  @FXML private TextField $information
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
    Integer id = ($id.getText() != "") ? $id.getText().toInteger() : null
    String name = ($name.getText() != "") ? $name.getText() : null
    SearchType nameSearchType = SearchType.valueOfExplanation($nameSearchType.getValue())
    String equivalent = ($equivalent.getText() != "") ? $equivalent.getText() : null
    String equivalentTitle = ($equivalentTitle.getValue() != "") ? $equivalentTitle.getValue() : null
    SearchType equivalentSearchType = SearchType.valueOfExplanation($equivalentSearchType.getValue())
    String information = ($information.getText() != "") ? $information.getText() : null
    String informationTitle = ($informationTitle.getValue() != "") ? $informationTitle.getValue() : null
    SearchType informationSearchType = SearchType.valueOfExplanation($informationSearchType.getValue())
    String tag = ($tag.getValue() != "") ? $tag.getValue() : null
    SlimeSearchParameter parameter = SlimeSearchParameter.new(id, name, nameSearchType, equivalent, equivalentTitle, equivalentSearchType, information, informationTitle, informationSearchType, tag)
    $stage.close(parameter)
  }

  private void setupTitles() {
    $equivalentTitle.getItems().addAll($dictionary.registeredEquivalentTitles())
    $informationTitle.getItems().addAll($dictionary.registeredInformationTitles())
    $tag.getItems().addAll($dictionary.registeredTags())
  }

}