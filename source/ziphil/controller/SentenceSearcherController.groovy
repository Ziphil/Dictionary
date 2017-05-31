package ziphil.controller

import groovy.transform.CompileStatic
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.fxml.FXML
import javafx.scene.control.TextField
import javafx.stage.StageStyle
import javafx.stage.Modality
import ziphil.custom.Measurement
import ziphil.custom.UtilityStage
import ziphil.dictionary.Dictionary
import ziphil.dictionary.Element
import ziphil.dictionary.Word
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
    ObservableList<List<Word>> result = FXCollections.observableArrayList()
    for (String wordName : sentence.split(/\s+/)) {
      StringBuilder nextWordName = StringBuilder.new()
      for (String character : wordName) {
        if (punctuations.indexOf(character) < 0) {
          nextWordName.append(character)
        }
      }
      if (nextWordName.length() > 0) {
        $dictionary.searchByName(nextWordName.toString(), true, true)
        List<Element> hitWords = $dictionary.getWholeWords()
        List<Word> finalHitwords = ArrayList.new()
        for (Element word : hitWords) {
          if (word instanceof Word) {
            finalHitwords.add((Word)word)
          }
        }
        result.add(finalHitwords)
      }
    }
    UtilityStage<Void> nextStage = UtilityStage.new(StageStyle.UTILITY)
    SentenceSearchResultController controller = SentenceSearchResultController.new(nextStage)
    nextStage.initModality(Modality.APPLICATION_MODAL)
    nextStage.initOwner($stage)
    controller.prepare(result)
    nextStage.showAndWait()
  }

}