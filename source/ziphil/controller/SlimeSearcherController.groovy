package ziphil.controller

import groovy.transform.CompileStatic
import javafx.fxml.FXML
import javafx.scene.control.CheckBox
import javafx.scene.control.ComboBox
import javafx.scene.control.ListView
import javafx.scene.control.TextField
import javafx.scene.control.TextFormatter
import ziphil.custom.BadgeCell
import ziphil.custom.IntegerUnaryOperator
import ziphil.custom.Measurement
import ziphil.custom.UtilityStage
import ziphil.dictionary.Badge
import ziphil.dictionary.SearchType
import ziphil.dictionary.slime.SlimeDictionary
import ziphil.dictionary.slime.SlimeSearchParameter
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class SlimeSearcherController extends Controller<SlimeSearchParameter> {

  private static final String RESOURCE_PATH = "resource/fxml/controller/slime_searcher.fxml"
  private static final String TITLE = "高度な検索"
  private static final Double DEFAULT_WIDTH = Measurement.rpx(560)
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
  @FXML private ComboBox<Badge> $badgeControl
  @FXML private CheckBox $hasIdControl
  @FXML private CheckBox $hasNameControl
  @FXML private CheckBox $hasEquivalentControl
  @FXML private CheckBox $hasInformationControl
  @FXML private CheckBox $hasTagControl
  @FXML private CheckBox $hasBadgeControl
  private SlimeDictionary $dictionary
  private SlimeSearchParameter $searchParameter

  public SlimeSearcherController(UtilityStage<? super SlimeSearchParameter> stage) {
    super(stage)
    loadResource(RESOURCE_PATH, TITLE, DEFAULT_WIDTH, DEFAULT_HEIGHT, false)
  }

  @FXML
  private void initialize() {
    setupIdControl()
    setupBadgeControl()
    setupHasFieldControls()
  }

  public void prepare(SlimeDictionary dictionary, SlimeSearchParameter searchParameter) {
    $dictionary = dictionary
    $searchParameter = searchParameter
    $nameControl.requestFocus()
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
      if ($searchParameter.hasId()) {
        $idControl.setText($searchParameter.getId().toString())
      }
      if ($searchParameter.hasName()) {
        $nameControl.setText($searchParameter.getName())
        $nameSearchTypeControl.setValue($searchParameter.getNameSearchType())
      }
      if ($searchParameter.hasEquivalent()) {
        $equivalentNameControl.setText($searchParameter.getEquivalentName())
        $equivalentTitleControl.setValue($searchParameter.getEquivalentTitle())
        $equivalentSearchTypeControl.setValue($searchParameter.getEquivalentSearchType())
      }
      if ($searchParameter.hasInformation()) {
        $informationTextControl.setText($searchParameter.getInformationText())
        $informationTitleControl.setValue($searchParameter.getInformationTitle())
        $informationSearchTypeControl.setValue($searchParameter.getInformationSearchType())
      }
      if ($searchParameter.hasTag()) {
        $tagControl.setValue($searchParameter.getTag())
      }
      if ($searchParameter.hasBadge()) {
        $badgeControl.setValue($searchParameter.getBadge())
      }
    }
  }

  @FXML
  protected void commit() {
    SlimeSearchParameter parameter = SlimeSearchParameter.new()
    if ($hasIdControl.isSelected()) {
      parameter.setHasId(true)
      parameter.setId(IntegerClass.parseInt($idControl.getText()))
    }
    if ($hasNameControl.isSelected()) {
      parameter.setHasName(true)
      parameter.setName($nameControl.getText())
      parameter.setNameSearchType($nameSearchTypeControl.getValue())
    }
    if ($hasEquivalentControl.isSelected()) {
      parameter.setHasEquivalent(true)
      parameter.setEquivalentName($equivalentNameControl.getText() ?: "")
      parameter.setEquivalentTitle($equivalentTitleControl.getValue())
      parameter.setEquivalentSearchType($equivalentSearchTypeControl.getValue())
    } 
    if ($hasInformationControl.isSelected()) {
      parameter.setHasInformation(true)
      parameter.setInformationText($informationTextControl.getText() ?: "")
      parameter.setInformationTitle($informationTitleControl.getValue())
      parameter.setInformationSearchType($informationSearchTypeControl.getValue())
    }
    if ($hasTagControl.isSelected()) {
      parameter.setHasTag(true)
      parameter.setTag($tagControl.getValue())
    }
    if ($hasBadgeControl.isSelected()) {
      parameter.setHasBadge(true)
      parameter.setBadge($badgeControl.getValue())
    }
    $stage.commit(parameter)
  }

  private void setupIdControl() {
    $idControl.setTextFormatter(TextFormatter.new(IntegerUnaryOperator.new()))
  }

  private void setupBadgeControl() {
    $badgeControl.setButtonCell(BadgeCell.new())
    $badgeControl.setCellFactory() { ListView<Badge> view ->
      BadgeCell cell = BadgeCell.new()
      return cell
    }
    for (Badge badge : Badge.values()) {
      $badgeControl.getItems().add(badge)
    }
  }

  private void setupHasFieldControls() {
    $idControl.disableProperty().bind($hasIdControl.selectedProperty().not())
    $nameControl.disableProperty().bind($hasNameControl.selectedProperty().not())
    $nameSearchTypeControl.disableProperty().bind($hasNameControl.selectedProperty().not())
    $equivalentNameControl.disableProperty().bind($hasEquivalentControl.selectedProperty().not())
    $equivalentTitleControl.disableProperty().bind($hasEquivalentControl.selectedProperty().not())
    $equivalentSearchTypeControl.disableProperty().bind($hasEquivalentControl.selectedProperty().not())
    $informationTextControl.disableProperty().bind($hasInformationControl.selectedProperty().not())
    $informationTitleControl.disableProperty().bind($hasInformationControl.selectedProperty().not())
    $informationSearchTypeControl.disableProperty().bind($hasInformationControl.selectedProperty().not())
    $tagControl.disableProperty().bind($hasTagControl.selectedProperty().not())
    $badgeControl.disableProperty().bind($hasBadgeControl.selectedProperty().not())
  }

}