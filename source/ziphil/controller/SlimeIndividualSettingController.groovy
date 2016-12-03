package ziphil.controller

import groovy.transform.CompileStatic
import javafx.fxml.FXML
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.scene.control.TextField
import javafx.stage.StageStyle
import javafx.stage.Modality
import ziphil.custom.ListSelectionView
import ziphil.custom.Measurement
import ziphil.custom.UtilityStage
import ziphil.dictionary.slime.SlimeDictionary
import ziphil.dictionary.slime.SlimeWord
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class SlimeIndividualSettingController extends Controller<Boolean> {

  private static final String RESOURCE_PATH = "resource/fxml/slime_individual_setting.fxml"
  private static final String TITLE = "個別設定"
  private static final Double DEFAULT_WIDTH = Measurement.rpx(520)
  private static final Double DEFAULT_HEIGHT = Measurement.rpx(400)

  @FXML private TextField $alphabetOrderControl
  @FXML private ListSelectionView<String> $plainInformationTitlesView
  private SlimeWord $defaultWord
  private SlimeDictionary $dictionary

  public SlimeIndividualSettingController(UtilityStage<Boolean> stage) {
    super(stage)
    loadResource(RESOURCE_PATH, TITLE, DEFAULT_WIDTH, DEFAULT_HEIGHT, false)
  }

  public void prepare(SlimeDictionary dictionary) {
    $dictionary = dictionary
    applySettings()
  }

  private void applySettings() {
    String alphabetOrder = $dictionary.getAlphabetOrder()
    List<String> registeredInformationTitles = $dictionary.getRegisteredInformationTitles()
    SlimeWord defaultWord = $dictionary.getDefaultWord()
    ObservableList<String> plainInformationTitles = FXCollections.observableArrayList(registeredInformationTitles.intersect($dictionary.getPlainInformationTitles()))
    ObservableList<String> normalInformationTitles = FXCollections.observableArrayList(registeredInformationTitles.minus($dictionary.getPlainInformationTitles()))
    $alphabetOrderControl.setText(alphabetOrder)
    $plainInformationTitlesView.setSources(normalInformationTitles)
    $plainInformationTitlesView.setTargets(plainInformationTitles)
    $defaultWord = defaultWord
  }

  private void saveSettings() {
    String alphabetOrder = $alphabetOrderControl.getText()
    List<String> plainInformationTitles = ArrayList.new($plainInformationTitlesView.getTargets())
    SlimeWord defaultWord = $defaultWord
    $dictionary.setAlphabetOrder(alphabetOrder)
    $dictionary.setPlainInformationTitles(plainInformationTitles)
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
    $stage.close(true)
  }

}