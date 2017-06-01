package ziphil.controller

import groovy.transform.CompileStatic
import javafx.fxml.FXML
import javafx.scene.control.TextField
import javafx.stage.StageStyle
import javafx.stage.Modality
import ziphil.custom.Measurement
import ziphil.custom.UtilityStage
import ziphil.dictionary.Dictionary
import ziphil.dictionary.SentenceSearcher
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class SentenceSearcherController extends Controller<Void> {

  private static final String RESOURCE_PATH = "resource/fxml/controller/sentence_searcher.fxml"
  private static final String TITLE = "文一括検索"
  private static final Double DEFAULT_WIDTH = Measurement.rpx(480)
  private static final Double DEFAULT_HEIGHT = -1

  @FXML private TextField $sentenceControl
  @FXML private TextField $punctuationsControl
  private Dictionary $dictionary

  public SentenceSearcherController(UtilityStage<Void> stage) {
    super(stage)
    loadResource(RESOURCE_PATH, TITLE, DEFAULT_WIDTH, DEFAULT_HEIGHT, true)
  }

  public void prepare(Dictionary dictionary) {
    $dictionary = dictionary
  }

  @FXML
  private void search() {
    String sentence = $sentenceControl.getText()
    String punctuations = $punctuationsControl.getText()
    SentenceSearcher searcher = SentenceSearcher.new($dictionary, sentence, punctuations)
    UtilityStage<Void> nextStage = UtilityStage.new(StageStyle.UTILITY)
    SentenceSearchResultController controller = SentenceSearchResultController.new(nextStage)
    nextStage.initModality(Modality.APPLICATION_MODAL)
    nextStage.initOwner($stage)
    controller.prepare(searcher.search())
    nextStage.showAndWait()
  }

}