package ziphil.controller

import groovy.transform.CompileStatic
import javafx.fxml.FXML
import javafx.scene.control.ComboBox
import javafx.scene.control.Spinner
import ziphil.custom.Measurement
import ziphil.custom.StringListEditor
import ziphil.custom.UtilityStage
import ziphil.dictionary.EditableDictionary
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class WordGeneratorController extends Controller<Void> {

  private static final String RESOURCE_PATH = "resource/fxml/controller/word_generator.fxml"
  private static final String TITLE = "単語自動生成"
  private static final Double DEFAULT_WIDTH = Measurement.rpx(480)
  private static final Double DEFAULT_HEIGHT = -1

  @FXML private StringListEditor $vowelsControl
  @FXML private StringListEditor $consonantsControl
  @FXML private StringListEditor $syllablePatternsControl
  @FXML private Spinner $minSyllableSizeControl
  @FXML private Spinner $maxSyllableSizeControl
  @FXML private ComboBox $collectionTypeControl

  public WordGeneratorController(UtilityStage<Void> stage) {
    super(stage)
    loadResource(RESOURCE_PATH, TITLE, DEFAULT_WIDTH, DEFAULT_HEIGHT, false)
  }

  public void prepare(EditableDictionary dictionary) {
  }

}