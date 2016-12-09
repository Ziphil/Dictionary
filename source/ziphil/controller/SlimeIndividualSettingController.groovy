package ziphil.controller

import groovy.transform.CompileStatic
import javafx.fxml.FXML
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.scene.control.CheckBox
import javafx.scene.control.TextField
import javafx.stage.StageStyle
import javafx.stage.Modality
import ziphil.custom.ListSelectionView
import ziphil.custom.PermutableListView
import ziphil.custom.Measurement
import ziphil.custom.UtilityStage
import ziphil.dictionary.slime.SlimeDictionary
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
  private SlimeDictionary $dictionary

  public SlimeIndividualSettingController(UtilityStage<Boolean> stage) {
    super(stage)
    loadResource(RESOURCE_PATH, TITLE, DEFAULT_WIDTH, DEFAULT_HEIGHT, false)
  }

  @FXML
  private void initialize() {
    bindInformationTitleOrderViewProperty()
  }

  public void prepare(SlimeDictionary dictionary) {
    $dictionary = dictionary
    List<String> plainInformationTitles = FXCollections.observableArrayList(dictionary.getPlainInformationTitles())
    List<String> normalInformationTitles = FXCollections.observableArrayList(dictionary.getRegisteredInformationTitles() - dictionary.getPlainInformationTitles())
    List<String> rawInformationTitleOrder = FXCollections.observableArrayList(dictionary.getInformationTitleOrder())
    List<String> informationTitleOrder = FXCollections.observableArrayList(dictionary.getInformationTitleOrder() ?: dictionary.getRegisteredInformationTitles())
    $alphabetOrderControl.setText(dictionary.getAlphabetOrder())
    $plainInformationTitlesView.setSources(normalInformationTitles)
    $plainInformationTitlesView.setTargets(plainInformationTitles)
    $informationTitleOrderView.setItems(informationTitleOrder)
    if (dictionary.getInformationTitleOrder() == null) {
      $usesIndividualOrderControl.setSelected(true)
    }
  }

  @FXML
  protected void commit() {
    List<String> plainInformationTitles = ArrayList.new($plainInformationTitlesView.getTargets())
    List<String> informationTitleOrder = ArrayList.new($informationTitleOrderView.getItems())
    String alphabetOrder = $alphabetOrderControl.getText()
    $dictionary.update(alphabetOrder, plainInformationTitles, informationTitleOrder)
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

  private void bindInformationTitleOrderViewProperty() {
    $informationTitleOrderView.disableProperty().bind($usesIndividualOrderControl.selectedProperty())
  }

}