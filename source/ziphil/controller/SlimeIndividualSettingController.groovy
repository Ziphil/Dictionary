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

  @FXML private ListSelectionView<String> $plainInformationTitlesView
  @FXML private PermutableListView<String> $informationTitleOrderView
  @FXML private CheckBox $usesIndividualOrderControl
  @FXML private TextField $alphabetOrderControl
  private SlimeWord $defaultWord
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
    applySettings()
  }

  private void applySettings() {
    ObservableList<String> plainInformationTitles = calculatePlainInformationTitles()
    ObservableList<String> normalInformationTitles = calculateNormalInformationTitles()
    ObservableList<String> informationTitleOrder = calculateInformationTitleOrder()
    List<String> rawInformationTitleOrder = $dictionary.getInformationTitleOrder()
    String alphabetOrder = $dictionary.getAlphabetOrder()
    SlimeWord defaultWord = $dictionary.getDefaultWord()
    $plainInformationTitlesView.setSources(normalInformationTitles)
    $plainInformationTitlesView.setTargets(plainInformationTitles)
    $informationTitleOrderView.setItems(informationTitleOrder)
    if (rawInformationTitleOrder == null) {
      $usesIndividualOrderControl.setSelected(true)
    }
    $alphabetOrderControl.setText(alphabetOrder)
    $defaultWord = defaultWord
  }

  private ObservableList<String> calculatePlainInformationTitles() {
    List<String> registeredInformationTitles = $dictionary.getRegisteredInformationTitles()
    return FXCollections.observableArrayList(registeredInformationTitles.intersect($dictionary.getPlainInformationTitles()))
  }

  private ObservableList<String> calculateNormalInformationTitles() {
    List<String> registeredInformationTitles = $dictionary.getRegisteredInformationTitles()
    return FXCollections.observableArrayList(registeredInformationTitles.minus($dictionary.getPlainInformationTitles()))
  }

  private ObservableList<String> calculateInformationTitleOrder() {
    List<String> registeredInformationTitles = $dictionary.getRegisteredInformationTitles()
    List<String> rawInformationTitleOrder = $dictionary.getInformationTitleOrder()
    ObservableList<String> informationTitleOrder
    if (rawInformationTitleOrder == null) {
      informationTitleOrder = FXCollections.observableArrayList(registeredInformationTitles)
    } else {
      informationTitleOrder = FXCollections.observableArrayList()
      for (String title : rawInformationTitleOrder) {
        if (registeredInformationTitles.contains(title)) {
          informationTitleOrder.add(title)
        }
      }
      for (String title : registeredInformationTitles) {
        if (!informationTitleOrder.contains(title)) {
          informationTitleOrder.add(title)
        }
      }
    }
    return informationTitleOrder
  }

  private void saveSettings() {
    List<String> plainInformationTitles = ArrayList.new($plainInformationTitlesView.getTargets())
    List<String> informationTitleOrder = ArrayList.new($informationTitleOrderView.getItems())
    String alphabetOrder = $alphabetOrderControl.getText()
    SlimeWord defaultWord = $defaultWord
    $dictionary.setPlainInformationTitles(plainInformationTitles)
    $dictionary.setInformationTitleOrder(informationTitleOrder)
    $dictionary.setAlphabetOrder(alphabetOrder)
    $dictionary.setDefaultWord(defaultWord)
  }

  @FXML
  private void editDefaultWord() {
    UtilityStage<Boolean> nextStage = UtilityStage.new(StageStyle.UTILITY)
    SlimeEditorController controller = SlimeEditorController.new(nextStage)
    nextStage.initModality(Modality.WINDOW_MODAL)
    nextStage.initOwner($stage)
    controller.prepare($defaultWord, $dictionary, false, false)
    nextStage.showAndWait()
  }

  @FXML
  protected void commit() {
    saveSettings()
    $stage.commit(true)
  }

  private void bindInformationTitleOrderViewProperty() {
    $informationTitleOrderView.disableProperty().bind($usesIndividualOrderControl.selectedProperty())
  }

}