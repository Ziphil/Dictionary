package ziphil.controller

import groovy.transform.CompileStatic
import javafx.fxml.FXML
import javafx.scene.control.TextField
import ziphil.custom.Measurement
import ziphil.custom.UtilityStage
import ziphil.dictionary.slime.SlimeDictionary


@CompileStatic @Newify
public class SlimeIndividualSettingController extends Controller<Boolean> {

  private static final String RESOURCE_PATH = "resource/fxml/slime_individual_setting.fxml"
  private static final String TITLE = "個別設定"
  private static final Double DEFAULT_WIDTH = Measurement.rpx(480)
  private static final Double DEFAULT_HEIGHT = -1

  @FXML private TextField $alphabetOrderControl
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
    $alphabetOrderControl.setText(alphabetOrder)
  }

  private void saveSettings() {
    String alphabetOrder = $alphabetOrderControl.getText()
    $dictionary.setAlphabetOrder(alphabetOrder)
  }

  @FXML
  protected void commit() {
    saveSettings()
    $stage.close(true)
  }

}