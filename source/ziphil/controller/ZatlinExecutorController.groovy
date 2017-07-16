package ziphil.controller

import groovy.transform.CompileStatic
import javafx.beans.value.ObservableValue
import javafx.fxml.FXML
import javafx.scene.Scene
import javafx.scene.control.TextArea
import javafx.scene.control.TextField
import javafx.stage.StageStyle
import javafx.stage.Modality
import ziphil.custom.ExtensionFilter
import ziphil.custom.Measurement
import ziphil.custom.UtilityStage
import ziphil.module.zatlin.Zatlin
import ziphil.module.zatlin.ZatlinException
import ziphil.module.zatlin.ZatlinParseException
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class ZatlinExecutorController extends Controller<Void> {

  private static final String RESOURCE_PATH = "resource/fxml/controller/zatlin_executor.fxml"
  private static final String TITLE = "Zatlin"
  private static final Double DEFAULT_WIDTH = Measurement.rpx(480)
  private static final Double DEFAULT_HEIGHT = -1

  @FXML private TextField $zatlinPathControl
  @FXML private TextField $outputControl
  @FXML private TextArea $logControl
  private Zatlin $zatlin = null
  private FileStringChooserController.Result $result = null

  public ZatlinExecutorController(UtilityStage<Void> stage) {
    super(stage)
    loadResource(RESOURCE_PATH, TITLE, DEFAULT_WIDTH, DEFAULT_HEIGHT, false)
  }

  @FXML
  private void execute() {
    if ($zatlin != null) {
      try {
        String output = $zatlin.generate()
        $outputControl.setText(output)
        $logControl.setText("")
      } catch (ZatlinException exception) {
        $outputControl.setText("")
        $logControl.setText(exception.getFullMessage())
      }
    } else {
      $outputControl.setText("")
    }
  }

  @FXML
  private void openZatlin() {
    UtilityStage<FileStringChooserController.Result> nextStage = UtilityStage.new(StageStyle.UTILITY)
    FileStringChooserController controller = FileStringChooserController.new(nextStage)
    ExtensionFilter filter = ExtensionFilter.new("生成規則ファイル", "ztl")
    nextStage.initModality(Modality.APPLICATION_MODAL)
    nextStage.initOwner($stage)
    controller.prepare(filter, $result)
    nextStage.showAndWait()
    if (nextStage.isCommitted()) {
      $result = nextStage.getResult()
      if ($result.isFileSelected()) {
        File file = $result.getFile()
        if (file != null) {
          $zatlinPathControl.setText(file.getAbsolutePath())
          $zatlin = Zatlin.new()
          try {
            $zatlin.load(file)
            $logControl.setText("")
          } catch (ZatlinParseException exception) {
            $logControl.setText(exception.getFullMessage())
            $zatlin = null
          }
        } else {
          $zatlinPathControl.setText("")
          $logControl.setText("")
          $zatlin = null
        }
      } else {
        String source = $result.getSource()
        $zatlinPathControl.setText("[テキスト]")
        $zatlin = Zatlin.new()
        try {
          $zatlin.load(source)
          $logControl.setText("")
        } catch (ZatlinParseException exception) {
          $logControl.setText(exception.getFullMessage())
          $zatlin = null
        }
      }
    }
  }

}