package ziphil.controller

import groovy.transform.CompileStatic
import javafx.fxml.FXML
import javafx.scene.control.CheckBox
import javafx.scene.control.ComboBox
import javafx.scene.control.ListView
import javafx.scene.control.TextField
import ziphil.custom.BadgeCell
import ziphil.custom.Measurement
import ziphil.custom.UtilityStage
import ziphil.dictionary.Badge
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
  @FXML private ComboBox<Badge> $badgeControl
  @FXML private CheckBox $hasNameControl
  @FXML private CheckBox $hasEquivalentControl
  @FXML private CheckBox $hasDescriptionControl
  @FXML private CheckBox $hasBadgeControl

  public ShaleiaSearcherController(UtilityStage<? super ShaleiaSearchParameter> stage) {
    super(stage)
    loadResource(RESOURCE_PATH, TITLE, DEFAULT_WIDTH, DEFAULT_HEIGHT, false)
  }

  @FXML
  private void initialize() {
    setupBadgeControl()
    setupHasFieldControls()
  }

  @FXML
  protected void commit() {
    ShaleiaSearchParameter parameter = ShaleiaSearchParameter.new()
    if ($hasNameControl.isSelected()) {
      parameter.setHasName(true)
      parameter.setName($nameControl.getText() ?: "")
      parameter.setNameSearchType($nameSearchTypeControl.getValue())
    }
    if ($hasEquivalentControl.isSelected()) {
      parameter.setHasEquivalent(true)
      parameter.setEquivalent($equivalentControl.getText() ?: "")
      parameter.setEquivalentSearchType($equivalentSearchTypeControl.getValue())
    }
    if ($hasDescriptionControl.isSelected()) {
      parameter.setHasDescription(true)
      parameter.setDescription($descriptionControl.getText() ?: "")
      parameter.setDescriptionSearchType($descriptionSearchTypeControl.getValue())
    }
    if ($hasBadgeControl.isSelected()) {
      parameter.setHasBadge(true)
      parameter.setBadge($badgeControl.getValue())
    }
    $stage.commit(parameter)
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
    $badgeControl.getSelectionModel().select(0)
  }

  private void setupHasFieldControls() {
    $nameControl.disableProperty().bind($hasNameControl.selectedProperty().not())
    $nameSearchTypeControl.disableProperty().bind($hasNameControl.selectedProperty().not())
    $equivalentControl.disableProperty().bind($hasEquivalentControl.selectedProperty().not())
    $equivalentSearchTypeControl.disableProperty().bind($hasEquivalentControl.selectedProperty().not())
    $descriptionControl.disableProperty().bind($hasDescriptionControl.selectedProperty().not())
    $descriptionSearchTypeControl.disableProperty().bind($hasDescriptionControl.selectedProperty().not())
    $badgeControl.disableProperty().bind($hasBadgeControl.selectedProperty().not())
  }

}