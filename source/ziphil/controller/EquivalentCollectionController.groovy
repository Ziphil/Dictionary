package ziphil.controller

import groovy.transform.CompileStatic
import javafx.fxml.FXML
import javafx.scene.control.ListView
import ziphil.custom.Measurement
import ziphil.custom.UtilityStage
import ziphil.dictionary.EquivalentCollection
import ziphil.dictionary.PseudoWord
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class EquivalentCollectionController extends Controller<Void> {

  private static final String RESOURCE_PATH = "resource/fxml/controller/equivalent_collection.fxml"
  private static final String TITLE = "訳語リスト"
  private static final Double DEFAULT_WIDTH = Measurement.rpx(360)
  private static final Double DEFAULT_HEIGHT = Measurement.rpx(480)

  @FXML private ListView<String> $equivalentView

  public EquivalentCollectionController(UtilityStage<Void> stage) {
    super(stage)
    loadResource(RESOURCE_PATH, TITLE, DEFAULT_WIDTH, DEFAULT_HEIGHT, false)
  }

  public void prepare(EquivalentCollection collection) {
    for (PseudoWord pseudoWord : collection.getPseudoWords()) {
      $equivalentView.getItems().add(pseudoWord.getEquivalents().join(", "))
    }
  }

}