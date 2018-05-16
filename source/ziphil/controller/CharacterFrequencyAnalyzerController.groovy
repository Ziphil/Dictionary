package ziphil.controller

import groovy.transform.CompileStatic
import javafx.beans.value.ObservableValue
import javafx.fxml.FXML
import javafx.scene.control.TextArea
import javafx.scene.control.TextField
import ziphil.custom.Measurement
import ziphil.custom.StringListEditor
import ziphil.custom.UtilityStage
import ziphil.module.CharacterFrequencyAnalyzer
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class CharacterFrequencyAnalyzerController extends Controller<Void> {

  private static final String RESOURCE_PATH = "resource/fxml/controller/character_frequency_analyzer.fxml"
  private static final String TITLE = "文字頻度解析"
  private static final Double DEFAULT_WIDTH = Measurement.rpx(480)
  private static final Double DEFAULT_HEIGHT = Measurement.rpx(280)

  @FXML private TextArea $inputControl
  @FXML private TextField $excludedCharactersControl
  @FXML private StringListEditor $multigraphsControl

  public CharacterFrequencyAnalyzerController(UtilityStage<? super Void> stage) {
    super(stage)
    loadResource(RESOURCE_PATH, TITLE, DEFAULT_WIDTH, DEFAULT_HEIGHT, true)
  }

  @FXML
  private void execute() {
    CharacterFrequencyAnalyzer analyzer = CharacterFrequencyAnalyzer.new()
    analyzer.setExcludedCharacters($excludedCharactersControl.getText())
    analyzer.setMultigraphs($multigraphsControl.getStrings())
    analyzer.addInput($inputControl.getText())
    UtilityStage<Void> nextStage = createStage()
    CharacterFrequencyController controller = CharacterFrequencyController.new(nextStage)
    controller.prepare(analyzer)
    nextStage.showAndWait()
  }

}