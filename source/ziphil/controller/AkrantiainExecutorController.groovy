package ziphil.controller

import groovy.transform.CompileStatic
import javafx.beans.value.ObservableValue
import javafx.fxml.FXML
import javafx.scene.Scene
import javafx.scene.control.TextField
import javafx.stage.StageStyle
import javafx.stage.Modality
import ziphil.custom.Measurement
import ziphil.custom.UtilityStage
import ziphil.module.akrantiain.Akrantiain
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class AkrantiainExecutorController extends Controller<Void> {

  private static final String RESOURCE_PATH = "resource/fxml/controller/akrantiain_executor.fxml"
  private static final String TITLE = "akrantiain"
  private static final Double DEFAULT_WIDTH = Measurement.rpx(480)
  private static final Double DEFAULT_HEIGHT = -1

  @FXML private TextField $snojPathControl
  @FXML private TextField $inputControl
  @FXML private TextField $outputControl

  private Akrantiain $akrantiain = null

  public AkrantiainExecutorController(UtilityStage<Void> stage) {
    super(stage)
    loadResource(RESOURCE_PATH, TITLE, DEFAULT_WIDTH, DEFAULT_HEIGHT, false)
  }

  @FXML
  private void initialize() {
    setupInputControl()
  }

  @FXML
  private void execute() {
    String output = $akrantiain.convert($inputControl.getText())
    $outputControl.setText(output)
  }

  @FXML
  private void openSnoj() {
    UtilityStage<Object> nextStage = UtilityStage.new(StageStyle.UTILITY)
    SnojChooserController controller = SnojChooserController.new(nextStage)
    nextStage.initModality(Modality.APPLICATION_MODAL)
    nextStage.initOwner($stage)
    nextStage.showAndWait()
    if (nextStage.isCommitted()) {
      Object result = nextStage.getResult()
      if (result instanceof File) {
        $snojPathControl.setText(result.getAbsolutePath())
        $akrantiain = Akrantiain.new()
        $akrantiain.load(result)
      } else if (result instanceof String) {
        $snojPathControl.setText("[テキスト]")
        $akrantiain = Akrantiain.new()
        $akrantiain.load(result)
      } else {
        $snojPathControl.setText("")
        $akrantiain = null
      }
    }
  }

  private void setupInputControl() {
    $inputControl.sceneProperty().addListener() { ObservableValue<? extends Scene> observableValue, Scene oldValue, Scene newValue ->
      if (oldValue == null && newValue != null) {
        $inputControl.requestFocus()
      }
    }
  }

}