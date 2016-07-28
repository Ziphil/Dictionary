package ziphil.controller

import groovy.transform.CompileStatic
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.control.ComboBox
import javafx.scene.control.TextField
import ziphil.custom.CustomBuilderFactory
import ziphil.custom.Measurement
import ziphil.custom.UtilityStage
import ziphil.dictionary.SearchType
import ziphil.dictionary.SlimeDictionary
import ziphil.dictionary.SlimeSearchParameter


@CompileStatic @Newify
public class SlimeSearcherController {

  private static final String RESOURCE_PATH = "resource/fxml/slime_searcher.fxml"
  private static final String TITLE = "高度な検索"
  private static final Double DEFAULT_WIDTH = Measurement.rpx(640)
  private static final Double DEFAULT_HEIGHT = -1

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
  private UtilityStage<SlimeSearchParameter> $stage
  private Scene $scene

  public SlimeSearcherController(UtilityStage<SlimeSearchParameter> stage) {
    $stage = stage
    loadResource()
  }

  public void prepare(SlimeDictionary dictionary) {
    $dictionary = dictionary
    setupTitles()
  }

  @FXML
  private void commitSearch() {
    String name = ($name.getText() != "") ? $name.getText() : null
    SearchType nameSearchType = SearchType.valueOfExplanation($nameSearchType.getValue())
    String equivalent = ($equivalent.getText() != "") ? $equivalent.getText() : null
    String equivalentTitle = ($equivalentTitle.getValue() != "") ? $equivalentTitle.getValue() : null
    SearchType equivalentSearchType = SearchType.valueOfExplanation($equivalentSearchType.getValue())
    String information = ($information.getText() != "") ? $information.getText() : null
    String informationTitle = ($informationTitle.getValue() != "") ? $informationTitle.getValue() : null
    SearchType informationSearchType = SearchType.valueOfExplanation($informationSearchType.getValue())
    String tag = ($tag.getValue() != "") ? $tag.getValue() : null
    SlimeSearchParameter parameter = SlimeSearchParameter.new(name, nameSearchType, equivalent, equivalentTitle, equivalentSearchType, information, informationTitle, informationSearchType, tag)
    $stage.close(parameter)
  }

  @FXML
  private void cancelSearch() {
    $stage.close(null)
  }

  private void setupTitles() {
    $equivalentTitle.getItems().addAll($dictionary.registeredEquivalentTitles())
    $informationTitle.getItems().addAll($dictionary.registeredInformationTitles())
    $tag.getItems().addAll($dictionary.registeredTags())
  }

  private void loadResource() {
    FXMLLoader loader = FXMLLoader.new(getClass().getClassLoader().getResource(RESOURCE_PATH), null, CustomBuilderFactory.new())
    loader.setController(this)
    Parent root = (Parent)loader.load()
    $scene = Scene.new(root, DEFAULT_WIDTH, DEFAULT_HEIGHT)
    $stage.setScene($scene)
    $stage.setTitle(TITLE)
    $stage.sizeToScene()
  }

}