package ziphil.controller

import groovy.transform.CompileStatic
import javafx.fxml.FXML
import javafx.scene.control.ListView
import javafx.scene.control.TextField
import javafx.stage.StageStyle
import javafx.stage.Modality
import ziphil.custom.Measurement
import ziphil.custom.SentenceSearchResultCell
import ziphil.custom.UtilityStage
import ziphil.dictionary.Dictionary
import ziphil.dictionary.SentenceSearcher
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class SentenceSearcherController extends Controller<Void> {

  private static final String RESOURCE_PATH = "resource/fxml/controller/sentence_searcher.fxml"
  private static final String TITLE = "文一括検索"
  private static final Double DEFAULT_WIDTH = Measurement.rpx(540)
  private static final Double DEFAULT_HEIGHT = Measurement.rpx(240)

  @FXML private TextField $sentenceControl
  @FXML private TextField $punctuationsControl
  @FXML private ListView<SentenceSearcher.Result> $resultView
  private Dictionary $dictionary

  public SentenceSearcherController(UtilityStage<? super Void> stage) {
    super(stage)
    loadResource(RESOURCE_PATH, TITLE, DEFAULT_WIDTH, DEFAULT_HEIGHT, true)
  }

  public void prepare(Dictionary dictionary) {
    $dictionary = dictionary
  }

  @FXML
  public void initialize() {
    setupResultView()
  }

  @FXML
  private void search() {
    String sentence = $sentenceControl.getText()
    String punctuations = $punctuationsControl.getText()
    SentenceSearcher searcher = SentenceSearcher.new($dictionary, sentence, punctuations)
    $resultView.setItems(searcher.search())
  }

  private void setupResultView() {
    $resultView.setCellFactory() { ListView<SentenceSearcher.Result> view ->
      SentenceSearchResultCell cell = SentenceSearchResultCell.new()
      return cell
    }
  }

}