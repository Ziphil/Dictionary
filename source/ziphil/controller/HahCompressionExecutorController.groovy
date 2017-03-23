package ziphil.controller

import groovy.transform.CompileStatic
import java.util.concurrent.Callable
import javafx.beans.binding.Bindings
import javafx.beans.binding.BooleanBinding
import javafx.fxml.FXML
import javafx.scene.control.CheckBox
import javafx.scene.control.ComboBox
import javafx.scene.control.Spinner
import javafx.scene.control.TextField
import javafx.scene.control.TextFormatter
import ziphil.custom.IntegerUnaryOperator
import ziphil.custom.Measurement
import ziphil.custom.UtilityStage
import ziphil.module.HahCompression
import ziphil.module.HahCompressionType
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class HahCompressionExecutorController extends Controller<Void> {

  private static final String RESOURCE_PATH = "resource/fxml/controller/hah_compression_executor.fxml"
  private static final String TITLE = "hah圧縮"
  private static final Double DEFAULT_WIDTH = Measurement.rpx(480)
  private static final Double DEFAULT_HEIGHT = -1

  @FXML private TextField $inputControl
  @FXML private TextField $outputControl
  @FXML private ComboBox<HahCompressionType> $typeControl
  @FXML private Spinner<Integer> $intervalControl
  @FXML private TextField $alphabetOrderControl
  @FXML private CheckBox $usesDictionaryAlphabetOrderControl
  @FXML private CheckBox $usesUnicodeOrderControl
  private String $dictionaryAlphabetOrder = null

  public HahCompressionExecutorController(UtilityStage<Void> stage) {
    super(stage)
    loadResource(RESOURCE_PATH, TITLE, DEFAULT_WIDTH, DEFAULT_HEIGHT, false)
  }

  @FXML
  private void initialize() {
    setupIntervalControl()
    setupAlphabetOrderControl()
    setupUsesUnicodeOrderControl()
  }

  public void prepare(String dictionaryAlphabetOrder) {
    $dictionaryAlphabetOrder = dictionaryAlphabetOrder
    setupUsesDictionaryAlphabetOrderControl()
  }

  @FXML
  private void execute() {
    HahCompressionType type = $typeControl.getValue()
    Boolean usesDictionaryAlphabetOrder = $usesDictionaryAlphabetOrderControl.isSelected()
    Boolean usesUnicodeOrder = $usesUnicodeOrderControl.isSelected()
    HahCompression compression = HahCompression.new(type)
    compression.setInterval($intervalControl.getValue())
    if (usesDictionaryAlphabetOrder) {
      compression.setAlphabetOrder($dictionaryAlphabetOrder)
    } else if (usesUnicodeOrder) {
      compression.setAlphabetOrder(null)
    } else {
      compression.setAlphabetOrder($alphabetOrderControl.getText())
    }
    String output = compression.compress($inputControl.getText())
    $outputControl.setText(output)
  }

  private void setupIntervalControl() {
    $intervalControl.getEditor().setTextFormatter(TextFormatter.new(IntegerUnaryOperator.new()))
  }

  private void setupAlphabetOrderControl() {
    Callable<Boolean> function = (Callable){
      return $typeControl.getValue() != HahCompressionType.SORT || $usesDictionaryAlphabetOrderControl.isSelected() || $usesUnicodeOrderControl.isSelected()
    }
    BooleanBinding binding = Bindings.createBooleanBinding(function, $typeControl.valueProperty(), $usesDictionaryAlphabetOrderControl.selectedProperty(), $usesUnicodeOrderControl.selectedProperty()) 
    $alphabetOrderControl.disableProperty().bind(binding)
  }

  private void setupUsesDictionaryAlphabetOrderControl() {
    Callable<Boolean> function = (Callable){
      return $typeControl.getValue() != HahCompressionType.SORT || $dictionaryAlphabetOrder == null
    }
    BooleanBinding binding = Bindings.createBooleanBinding(function, $typeControl.valueProperty()) 
    $usesDictionaryAlphabetOrderControl.disableProperty().bind(binding)
  }

  private void setupUsesUnicodeOrderControl() {
    Callable<Boolean> function = (Callable){
      return $typeControl.getValue() != HahCompressionType.SORT
    }
    BooleanBinding binding = Bindings.createBooleanBinding(function, $typeControl.valueProperty()) 
    $usesUnicodeOrderControl.disableProperty().bind(binding)
  }

}