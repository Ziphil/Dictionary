package ziphil.controller

import groovy.transform.CompileStatic
import javafx.fxml.FXML
import javafx.scene.control.TextArea
import javafx.scene.control.TextField
import ziphil.custom.Measurement
import ziphil.custom.UtilityStage
import ziphil.dictionary.shaleia.ShaleiaDictionary


@CompileStatic @Newify
public class ShaleiaIndividualSettingController extends Controller<Boolean> {

  private static final String RESOURCE_PATH = "resource/fxml/shaleia_individual_setting.fxml"
  private static final String TITLE = "個別設定"
  private static final Double DEFAULT_WIDTH = Measurement.rpx(480)
  private static final Double DEFAULT_HEIGHT = Measurement.rpx(320)

  @FXML private TextField $alphabetOrderControl
  @FXML private TextArea $changeDataControl
  private ShaleiaDictionary $dictionary

  public ShaleiaIndividualSettingController(UtilityStage<Boolean> stage) {
    super(stage)
    loadResource(RESOURCE_PATH, TITLE, DEFAULT_WIDTH, DEFAULT_HEIGHT, false)
  }

  public void prepare(ShaleiaDictionary dictionary) {
    $dictionary = dictionary
    applySettings()
  }

  private void applySettings() {
    String alphabetOrder = $dictionary.getAlphabetOrder()
    String changeData = $dictionary.getChangeData()
    $alphabetOrderControl.setText(alphabetOrder)
    $changeDataControl.setText(changeData)
  }

  private void saveSettings() {
    String alphabetOrder = $alphabetOrderControl.getText()
    String changeData = $changeDataControl.getText()
    $dictionary.setAlphabetOrder(alphabetOrder)
    $dictionary.setChangeData(changeData)
  }

  @FXML
  protected void commit() {
    saveSettings()
    $stage.close(true)
  }

}