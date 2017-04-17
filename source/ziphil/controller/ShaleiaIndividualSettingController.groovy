package ziphil.controller

import groovy.transform.CompileStatic
import javafx.fxml.FXML
import javafx.scene.control.TextArea
import javafx.scene.control.TextField
import javafx.stage.StageStyle
import javafx.stage.Modality
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
  @FXML private TextField $akrantiainSourceControl
  @FXML private TextArea $changeDescriptionControl
  private String $akrantiainSource
  private ShaleiaDictionary $dictionary

  public ShaleiaIndividualSettingController(UtilityStage<Boolean> stage) {
    super(stage)
    loadResource(RESOURCE_PATH, TITLE, DEFAULT_WIDTH, DEFAULT_HEIGHT, false)
  }

  public void prepare(ShaleiaDictionary dictionary) {
    $dictionary = dictionary
    $alphabetOrderControl.setText(dictionary.getAlphabetOrder())
    $versionControl.setText(dictionary.getVersion())
    $akrantiainSourceControl.setText(dictionary.getAkrantiainSource())
    $changeDescriptionControl.setText(dictionary.getChangeDescription())
    $akrantiainSource = dictionary.getAkrantiainSource()
  }

  @FXML
  protected void commit() {
    $dictionary.setAlphabetOrder($alphabetOrderControl.getText())
    $dictionary.setVersion($versionControl.getText())
    $dictionary.setAkrantiainSource($akrantiainSource)
    $dictionary.setChangeDescription($changeDescriptionControl.getText())
    $dictionary.updateMinimum()
    $stage.commit(true)
  }

  @FXML
  private void editSnoj() {
    UtilityStage<SnojChooserController.Result> nextStage = UtilityStage.new(StageStyle.UTILITY)
    SnojChooserController controller = SnojChooserController.new(nextStage)
    SnojChooserController.Result previousResult = SnojChooserController.Result.new(null, $akrantiainSource, false)
    nextStage.initModality(Modality.APPLICATION_MODAL)
    nextStage.initOwner($stage)     
    controller.prepare(previousResult)
    nextStage.showAndWait()
    if (nextStage.isCommitted()) {
      SnojChooserController.Result result = nextStage.getResult()
      if (result.isFileSelected()) {
        File file = result.getFile()
        if (file != null) {
          String source = file.getText()
          $akrantiainSource = source
          $akrantiainSourceControl.setText(source)
        }
      } else {
        String source = result.getSource()
        $akrantiainSource = source
        $akrantiainSourceControl.setText(source)
      }
    }
  }

  @FXML
  private void removeSnoj() {
    $akrantiainSource = null
    $akrantiainSourceControl.setText("")
  }

}