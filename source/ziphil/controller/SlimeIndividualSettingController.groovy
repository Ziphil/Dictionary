package ziphil.controller

import groovy.transform.CompileStatic
import javafx.fxml.FXML
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.scene.control.Button
import javafx.scene.control.CheckBox
import javafx.scene.control.Label
import javafx.scene.control.TextField
import javafx.scene.layout.GridPane
import javafx.scene.layout.HBox
import javafx.stage.StageStyle
import javafx.stage.Modality
import ziphil.custom.ListSelectionView
import ziphil.custom.PermutableListView
import ziphil.custom.Measurement
import ziphil.custom.UtilityStage
import ziphil.dictionary.slime.SlimeDictionary
import ziphil.dictionary.slime.SlimeIndividualSetting
import ziphil.dictionary.slime.SlimeSearchParameter
import ziphil.dictionary.slime.SlimeWord
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class SlimeIndividualSettingController extends Controller<Boolean> {

  private static final String RESOURCE_PATH = "resource/fxml/controller/slime_individual_setting.fxml"
  private static final String TITLE = "個別設定"
  private static final Double DEFAULT_WIDTH = Measurement.rpx(520)
  private static final Double DEFAULT_HEIGHT = Measurement.rpx(400)

  @FXML private TextField $alphabetOrderControl
  @FXML private ListSelectionView<String> $plainInformationTitlesView
  @FXML private PermutableListView<String> $informationTitleOrderView
  @FXML private CheckBox $usesIndividualOrderControl
  @FXML private GridPane $searchParameterPane
  private List<SlimeSearchParameter> $searchParameters
  private SlimeDictionary $dictionary
  private SlimeIndividualSetting $individualSetting

  public SlimeIndividualSettingController(UtilityStage<Boolean> stage) {
    super(stage)
    loadResource(RESOURCE_PATH, TITLE, DEFAULT_WIDTH, DEFAULT_HEIGHT, false)
  }

  @FXML
  private void initialize() {
    setupSearchParameterPane()
    bindInformationTitleOrderViewProperty()
  }

  public void prepare(SlimeDictionary dictionary, SlimeIndividualSetting individualSetting) {
    $dictionary = dictionary
    $individualSetting = individualSetting
    List<String> plainInformationTitles = FXCollections.observableArrayList(dictionary.getPlainInformationTitles())
    List<String> normalInformationTitles = FXCollections.observableArrayList(dictionary.getRegisteredInformationTitles() - dictionary.getPlainInformationTitles())
    List<String> rawInformationTitleOrder = dictionary.getInformationTitleOrder()
    List<String> informationTitleOrder = FXCollections.observableArrayList(dictionary.getInformationTitleOrder() ?: dictionary.getRegisteredInformationTitles())
    List<SlimeSearchParameter> searchParameters = ArrayList.new(individualSetting.getSearchParameters())
    $alphabetOrderControl.setText(dictionary.getAlphabetOrder())
    $plainInformationTitlesView.setSources(normalInformationTitles)
    $plainInformationTitlesView.setTargets(plainInformationTitles)
    $informationTitleOrderView.setItems(informationTitleOrder)
    if (dictionary.getInformationTitleOrder() == null) {
      $usesIndividualOrderControl.setSelected(true)
    }
    $searchParameters = searchParameters
  }

  @FXML
  protected void commit() {
    String alphabetOrder = ($alphabetOrderControl.getText() == "") ? null : $alphabetOrderControl.getText()
    List<String> plainInformationTitles = ArrayList.new($plainInformationTitlesView.getTargets())
    Boolean usesIndividualOrder = $usesIndividualOrderControl.isSelected()
    List<String> informationTitleOrder = (usesIndividualOrder) ? null : ArrayList.new($informationTitleOrderView.getItems())
    List<SlimeSearchParameter> searchParameters = $searchParameters
    $dictionary.setAlphabetOrder(alphabetOrder)
    $dictionary.setPlainInformationTitles(plainInformationTitles)
    $dictionary.setInformationTitleOrder(informationTitleOrder)
    $individualSetting.setSearchParameters(searchParameters)
    $dictionary.updateMinimum()
    $stage.commit(true)
  }

  @FXML
  private void editDefaultWord() {
    UtilityStage<Boolean> nextStage = UtilityStage.new(StageStyle.UTILITY)
    SlimeEditorController controller = SlimeEditorController.new(nextStage)
    nextStage.initModality(Modality.WINDOW_MODAL)
    nextStage.initOwner($stage)
    controller.prepare($dictionary.getDefaultWord(), $dictionary, false, false)
    nextStage.showAndWait()
  }

  private void editSearchParameter(Integer i) {
    UtilityStage<SlimeSearchParameter> nextStage = UtilityStage.new(StageStyle.UTILITY)
    SlimeSearcherController controller = SlimeSearcherController.new(nextStage)
    nextStage.initOwner($stage)
    controller.prepare($dictionary, $searchParameters[i])
    nextStage.showAndWait()
    if (nextStage.isCommitted()) {
      SlimeSearchParameter parameter = nextStage.getResult()
      $searchParameters[i] = parameter
    }
  }

  private void deregisterSearchParameter(Integer i) {
    $searchParameters[i] = (SlimeSearchParameter)null
  }

  private void setupSearchParameterPane() {
    for (Integer i : 0 ..< 10) {
      Integer j = i
      Label numberLabel = Label.new("検索条件${(i + 1) % 10}:")
      HBox box = HBox.new(Measurement.rpx(5))
      Button editButton = Button.new("編集")
      Button deregisterButton = Button.new("解除")
      editButton.setPrefWidth(Measurement.rpx(70))
      editButton.setMinWidth(Measurement.rpx(70))
      deregisterButton.setPrefWidth(Measurement.rpx(70))
      deregisterButton.setMinWidth(Measurement.rpx(70))
      editButton.setOnAction() {
        editSearchParameter(j)
      }
      deregisterButton.setOnAction() {
        deregisterSearchParameter(j)
      }
      box.getChildren().addAll(editButton, deregisterButton)
      $searchParameterPane.add(numberLabel, 0, i)
      $searchParameterPane.add(box, 1, i)
    }
  }

  private void bindInformationTitleOrderViewProperty() {
    $informationTitleOrderView.disableProperty().bind($usesIndividualOrderControl.selectedProperty())
  }

}