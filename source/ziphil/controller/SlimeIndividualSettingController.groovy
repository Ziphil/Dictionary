package ziphil.controller

import groovy.transform.CompileStatic
import javafx.fxml.FXML
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.scene.control.TextField
import ziphil.custom.ListSelectionView
import ziphil.custom.Measurement
import ziphil.custom.UtilityStage
import ziphil.dictionary.slime.SlimeDictionary
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class SlimeIndividualSettingController extends Controller<Boolean> {

  private static final String RESOURCE_PATH = "resource/fxml/slime_individual_setting.fxml"
  private static final String TITLE = "個別設定"
  private static final Double DEFAULT_WIDTH = Measurement.rpx(640)
  private static final Double DEFAULT_HEIGHT = Measurement.rpx(320)

  @FXML private TextField $alphabetOrderControl
  @FXML private ListSelectionView<String> $plainInformationTitlesView
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
    ObservableList<String> plainInformationTitles = FXCollections.observableArrayList(registeredInformationTitles.intersect($dictionary.getPlainInformationTitles()))
    ObservableList<String> normalInformationTitles = FXCollections.observableArrayList(registeredInformationTitles.minus($dictionary.getPlainInformationTitles()))
    $alphabetOrderControl.setText(alphabetOrder)
    $plainInformationTitlesView.setSources(normalInformationTitles)
    $plainInformationTitlesView.setTargets(plainInformationTitles)
  }

  private void saveSettings() {
    String alphabetOrder = $alphabetOrderControl.getText()
    List<String> plainInformationTitles = ArrayList.new($plainInformationTitlesView.getTargets())
    $dictionary.setAlphabetOrder(alphabetOrder)
    $dictionary.setPlainInformationTitles(plainInformationTitles)
  }

  @FXML
  protected void commit() {
    saveSettings()
    $stage.close(true)
  }

}