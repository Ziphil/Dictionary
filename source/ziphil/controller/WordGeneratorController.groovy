package ziphil.controller

import groovy.transform.CompileStatic
import javafx.beans.value.ObservableValue
import javafx.fxml.FXML
import javafx.scene.control.ComboBox
import javafx.scene.control.Spinner
import javafx.scene.control.TextFormatter
import ziphil.custom.IntegerUnaryOperator
import ziphil.custom.Measurement
import ziphil.custom.StringListEditor
import ziphil.custom.UtilityStage
import ziphil.dictionary.EditableDictionary
import ziphil.dictionary.EquivalentCollectionType
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
  @FXML private Spinner<IntegerClass> $minSyllableSizeControl
  @FXML private Spinner<IntegerClass> $maxSyllableSizeControl
  @FXML private ComboBox $collectionTypeControl

  public WordGeneratorController(UtilityStage<Void> stage) {
    super(stage)
    loadResource(RESOURCE_PATH, TITLE, DEFAULT_WIDTH, DEFAULT_HEIGHT, false)
  }

  @FXML
  private void initialize() {
    setupSyllableSizeControls()
    setupCollectionTypeControl()
    setupIntegerControls()
  }

  public void prepare(EditableDictionary dictionary) {
  }

  private void setupSyllableSizeControls() {
    $minSyllableSizeControl.valueProperty().addListener() { ObservableValue<? extends IntegerClass> observableValue, IntegerClass oldValue, IntegerClass newValue ->
      if (newValue > $maxSyllableSizeControl.getValue()) {
        $maxSyllableSizeControl.getValueFactory().setValue(newValue)
      }
    }
    $maxSyllableSizeControl.valueProperty().addListener() { ObservableValue<? extends IntegerClass> observableValue, IntegerClass oldValue, IntegerClass newValue ->
      if (newValue < $minSyllableSizeControl.getValue()) {
        $minSyllableSizeControl.getValueFactory().setValue(newValue)
      }
    }
  }

  private void setupCollectionTypeControl() {
    List<EquivalentCollectionType> collectionTypes = EquivalentCollectionType.getCollectionTypes()
    $collectionTypeControl.getItems().addAll(collectionTypes)
  }

  private void setupIntegerControls() {
    $minSyllableSizeControl.getEditor().setTextFormatter(TextFormatter.new(IntegerUnaryOperator.new()))
    $maxSyllableSizeControl.getEditor().setTextFormatter(TextFormatter.new(IntegerUnaryOperator.new()))
  }

}