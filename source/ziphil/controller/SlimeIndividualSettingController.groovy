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
import javafx.scene.layout.Priority
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
  private static final Double DEFAULT_HEIGHT = -1

  @FXML private TextField $alphabetOrderControl
  @FXML private ListSelectionView<String> $plainInformationTitleView
  @FXML private PermutableListView<String> $informationTitleOrderView
  @FXML private CheckBox $usesIndividualOrderControl
  @FXML private GridPane $registeredParameterPane
  @FXML private List<TextField> $registeredParameterStringControls = ArrayList.new(10)
  @FXML private List<TextField> $registeredParameterNameControls = ArrayList.new(10)
  private List<SlimeSearchParameter> $registeredParameters
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
    List<SlimeSearchParameter> registeredParameters = ArrayList.new(individualSetting.getRegisteredParameters())
    List<String> registeredParameterStrings = registeredParameters.collect{parameter -> (parameter != null) ? parameter.toString() : ""}
    List<String> registeredParameterNames = ArrayList.new(individualSetting.getRegisteredParameterNames())
    $alphabetOrderControl.setText(dictionary.getAlphabetOrder())
    $plainInformationTitleView.setSources(normalInformationTitles)
    $plainInformationTitleView.setTargets(plainInformationTitles)
    $informationTitleOrderView.setItems(informationTitleOrder)
    if (dictionary.getInformationTitleOrder() == null) {
      $usesIndividualOrderControl.setSelected(true)
    }
    $registeredParameters = registeredParameters
    for (Integer i : 0 ..< 10) {
      $registeredParameterStringControls[i].setText(registeredParameterStrings[i])
      $registeredParameterNameControls[i].setText(registeredParameterNames[i])
    }
  }

  @FXML
  protected void commit() {
    String alphabetOrder = ($alphabetOrderControl.getText() == "") ? null : $alphabetOrderControl.getText()
    List<String> plainInformationTitles = ArrayList.new($plainInformationTitleView.getTargets())
    Boolean usesIndividualOrder = $usesIndividualOrderControl.isSelected()
    List<String> informationTitleOrder = (usesIndividualOrder) ? null : ArrayList.new($informationTitleOrderView.getItems())
    List<SlimeSearchParameter> registeredParameters = $registeredParameters
    List<String> registeredParameterNames = $registeredParameterNameControls.collect{control -> control.getText()}
    $dictionary.setAlphabetOrder(alphabetOrder)
    $dictionary.setPlainInformationTitles(plainInformationTitles)
    $dictionary.setInformationTitleOrder(informationTitleOrder)
    $individualSetting.setRegisteredParameters(registeredParameters)
    $individualSetting.setRegisteredParameterNames(registeredParameterNames)
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
    controller.prepare($dictionary, $registeredParameters[i])
    nextStage.showAndWait()
    if (nextStage.isCommitted()) {
      SlimeSearchParameter parameter = nextStage.getResult()
      $registeredParameters[i] = parameter
      $registeredParameterStringControls[i].setText(parameter.toString())
    }
  }

  private void deregisterSearchParameter(Integer i) {
    $registeredParameters[i] = (SlimeSearchParameter)null
    $registeredParameterStringControls[i].setText("")
    $registeredParameterNameControls[i].setText("")
  }

  private void setupSearchParameterPane() {
    for (Integer i : 0 ..< 10) {
      Integer j = i
      Label numberLabel = Label.new("検索条件${(i + 1) % 10}:")
      HBox box = HBox.new(Measurement.rpx(5))
      HBox innerBox = HBox.new()
      TextField registeredParameterStringControl = TextField.new()
      TextField registeredParameterNameControl = TextField.new()
      Button editButton = Button.new("…")
      Button deregisterButton = Button.new("解除")
      registeredParameterStringControl.setEditable(false)
      registeredParameterStringControl.getStyleClass().add("left-pill")
      registeredParameterNameControl.setPrefWidth(Measurement.rpx(120))
      registeredParameterNameControl.setMinWidth(Measurement.rpx(120))
      editButton.getStyleClass().add("right-pill")
      deregisterButton.setPrefWidth(Measurement.rpx(70))
      deregisterButton.setMinWidth(Measurement.rpx(70))
      editButton.setOnAction() {
        editSearchParameter(j)
      }
      deregisterButton.setOnAction() {
        deregisterSearchParameter(j)
      }
      innerBox.getChildren().addAll(registeredParameterStringControl, editButton)
      innerBox.setHgrow(registeredParameterStringControl, Priority.ALWAYS)
      box.getChildren().addAll(registeredParameterNameControl, innerBox, deregisterButton)
      box.setHgrow(innerBox, Priority.ALWAYS)
      $registeredParameterStringControls[i] = registeredParameterStringControl
      $registeredParameterNameControls[i] = registeredParameterNameControl
      $registeredParameterPane.add(numberLabel, 0, i)
      $registeredParameterPane.add(box, 1, i)
    }
  }

  private void bindInformationTitleOrderViewProperty() {
    $informationTitleOrderView.disableProperty().bind($usesIndividualOrderControl.selectedProperty())
  }

}