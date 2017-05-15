package ziphil.controller

import groovy.transform.CompileStatic
import javafx.fxml.FXML
import javafx.scene.control.Label
import javafx.stage.StageStyle
import javafx.stage.Modality
import ziphil.dictionary.Dictionary
import ziphil.dictionary.DictionaryStatisticsCalculator
import ziphil.dictionary.Word
import ziphil.custom.UtilityStage
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class StatisticsController extends Controller<Void> {

  private static final String RESOURCE_PATH = "resource/fxml/controller/statistics.fxml"
  private static final String TITLE = "統計"
  private static final Double DEFAULT_WIDTH = -1
  private static final Double DEFAULT_HEIGHT = -1

  @FXML private Label $wordSizeText
  @FXML private Label $tokiponaText
  @FXML private Label $logTokiponaText
  @FXML private Label $averageWordNameLengthText
  @FXML private Label $contentLengthText
  @FXML private Label $richnessText
  private DictionaryStatisticsCalculator $calculator

  public StatisticsController(UtilityStage<Void> stage) {
    super(stage)
    loadResource(RESOURCE_PATH, TITLE, DEFAULT_WIDTH, DEFAULT_HEIGHT, true)
  }

  public void prepare(Dictionary dictionary) {
    DictionaryStatisticsCalculator calculator = DictionaryStatisticsCalculator.new(dictionary)
    $calculator = calculator
    $wordSizeText.setText(String.format("%d", calculator.wordSize()))
    $tokiponaText.setText(String.format("%.2f", calculator.tokipona()))
    $logTokiponaText.setText(String.format("%.2f", calculator.logTokipona()))
    $averageWordNameLengthText.setText(String.format("%.2f", calculator.averageWordNameLength()))
    $contentLengthText.setText(String.format("%d", calculator.contentLength()))
    $richnessText.setText(String.format("%.2f", calculator.richness()))
  }

  @FXML
  private void showCharacterFrequency() {
    UtilityStage<Void> nextStage = UtilityStage.new(StageStyle.UTILITY)
    CharacterFrequencyController controller = CharacterFrequencyController.new(nextStage)
    nextStage.initModality(Modality.APPLICATION_MODAL)
    nextStage.initOwner($stage)
    controller.prepare($calculator)
    nextStage.showAndWait()
  }

}