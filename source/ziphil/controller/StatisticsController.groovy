package ziphil.controller

import groovy.transform.CompileStatic
import javafx.fxml.FXML
import javafx.scene.control.Label
import javafx.stage.StageStyle
import javafx.stage.Modality
import ziphil.dictionary.Dictionary
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
  private Dictionary $dictionary

  public StatisticsController(UtilityStage<Void> stage) {
    super(stage)
    loadResource(RESOURCE_PATH, TITLE, DEFAULT_WIDTH, DEFAULT_HEIGHT, true)
  }

  public void prepare(Dictionary dictionary) {
    $dictionary = dictionary
    Integer wordSize = dictionary.totalWordSize()
    Double tokipona = (Double)(wordSize / 120)
    Double logTokipona = Math.log10(tokipona)
    Integer wordNameLength = 0
    Integer contentLength = 0
    for (Word word : dictionary.getRawWords()) {
      wordNameLength += word.getName().length()
      contentLength += word.getContent().length()
    }
    Double averageWordNameLength = (wordSize > 0) ? (Double)(wordNameLength / wordSize) : 0
    Double richness = (wordSize > 0) ? (Double)(contentLength / wordSize) : 0
    $wordSizeText.setText(wordSize.toString())
    $tokiponaText.setText(String.format("%.2f", tokipona))
    $logTokiponaText.setText(String.format("%.2f", logTokipona))
    $averageWordNameLengthText.setText(String.format("%.2f", averageWordNameLength))
    $contentLengthText.setText(contentLength.toString())
    $richnessText.setText(String.format("%.2f", richness))
  }

  @FXML
  private void showCharacterFrequency() {
    UtilityStage<Void> nextStage = UtilityStage.new(StageStyle.UTILITY)
    CharacterFrequencyController controller = CharacterFrequencyController.new(nextStage)
    nextStage.initModality(Modality.APPLICATION_MODAL)
    nextStage.initOwner($stage)
    controller.prepare($dictionary)
    nextStage.showAndWait()
  }

}