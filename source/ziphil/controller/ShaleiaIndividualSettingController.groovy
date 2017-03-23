package ziphil.controller

import groovy.transform.CompileStatic
import javafx.fxml.FXML
import javafx.scene.control.TextArea
import javafx.scene.control.TextField
import ziphil.custom.Measurement
import ziphil.custom.UtilityStage
import ziphil.dictionary.shaleia.ShaleiaDictionary
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class ShaleiaIndividualSettingController extends Controller<Boolean> {

  private static final String RESOURCE_PATH = "resource/fxml/controller/shaleia_individual_setting.fxml"
  private static final String TITLE = "個別設定"
  private static final Double DEFAULT_WIDTH = Measurement.rpx(480)
  private static final Double DEFAULT_HEIGHT = Measurement.rpx(320)

  @FXML private TextField $alphabetOrderControl
  @FXML private TextField $versionControl
  @FXML private TextArea $changeDescriptionControl
  private ShaleiaDictionary $dictionary

  public ShaleiaIndividualSettingController(UtilityStage<Boolean> stage) {
    super(stage)
    loadResource(RESOURCE_PATH, TITLE, DEFAULT_WIDTH, DEFAULT_HEIGHT, false)
  }

  public void prepare(ShaleiaDictionary dictionary) {
    $dictionary = dictionary
    $alphabetOrderControl.setText(dictionary.getAlphabetOrder())
    $versionControl.setText(dictionary.getVersion())
    $changeDescriptionControl.setText(dictionary.getChangeDescription())
  }

  @FXML
  protected void commit() {
    $dictionary.setAlphabetOrder($alphabetOrderControl.getText())
    $dictionary.setVersion($versionControl.getText())
    $dictionary.setChangeDescription($changeDescriptionControl.getText())
    $dictionary.updateMinimum()
    $stage.commit(true)
  }

}