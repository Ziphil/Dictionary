package ziphil.controller

import groovy.transform.CompileStatic
import javafx.fxml.FXML
import javafx.scene.control.ComboBox
import javafx.scene.control.CheckBox
import javafx.scene.control.Spinner
import javafx.scene.control.TextField
import javafx.scene.control.TextFormatter
import javafx.scene.text.Font
import ziphil.custom.IntegerUnaryOperator
import ziphil.custom.Measurement
import ziphil.custom.SwitchButton
import ziphil.custom.UtilityStage
import ziphil.dictionary.exporter.SlimePdfExportConfig
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class SlimePdfExportConfigController extends Controller<SlimePdfExportConfig> {

  private static final String RESOURCE_PATH = "resource/fxml/controller/slime_pdf_export_config.fxml"
  private static final String TITLE = "スクリプト検索"
  private static final Double DEFAULT_WIDTH = Measurement.rpx(580)
  private static final Double DEFAULT_HEIGHT = -1

  @FXML private ComboBox<String> $captionFontFamilyControl
  @FXML private Spinner<IntegerClass> $captionFontSizeControl
  @FXML private CheckBox $usesDefaultCaptionFontFamilyControl
  @FXML private ComboBox<String> $headFontFamilyControl
  @FXML private Spinner<IntegerClass> $headFontSizeControl
  @FXML private CheckBox $usesDefaultHeadFontFamilyControl
  @FXML private ComboBox<String> $mainFontFamilyControl
  @FXML private Spinner<IntegerClass> $mainFontSizeControl
  @FXML private CheckBox $usesDefaultMainFontFamilyControl
  @FXML private TextField $variationMarkerControl
  @FXML private CheckBox $usesDefaultVariationMarkerControl
  @FXML private TextField $relationMarkerControl
  @FXML private CheckBox $usesDefaultRelationMarkerControl
  @FXML private SwitchButton $modifiesControl
  @FXML private TextField $externalCommandControl
  @FXML private CheckBox $usesEmbeddedProcessorControl

  public SlimePdfExportConfigController(UtilityStage<? super SlimePdfExportConfig> stage) {
    super(stage)
    loadResource(RESOURCE_PATH, TITLE, DEFAULT_WIDTH, DEFAULT_HEIGHT, false)
  }

  @FXML
  private void initialize() {
    setupFontFamilyControls()
    setupIntegerControls()
    bindFontControlProperties()
    bindMarkerControlProperties()
    bindExternalCommandControlProperty()
  }

  @FXML
  protected void commit() {
    String captionFontFamily = $captionFontFamilyControl.getValue()
    Int captionFontSize = $captionFontSizeControl.getValue()
    Boolean usesDefaultCaptionFontFamily = $usesDefaultCaptionFontFamilyControl.isSelected()
    String headFontFamily = $headFontFamilyControl.getValue()
    Int headFontSize = $headFontSizeControl.getValue()
    Boolean usesDefaultHeadFontFamily = $usesDefaultHeadFontFamilyControl.isSelected()
    String mainFontFamily = $mainFontFamilyControl.getValue()
    Int mainFontSize = $mainFontSizeControl.getValue()
    Boolean usesDefaultMainFontFamily = $usesDefaultMainFontFamilyControl.isSelected()
    String variationMarker = $variationMarkerControl.getText()
    Boolean usesDefaultVariationMarker = $usesDefaultVariationMarkerControl.isSelected()
    String relationMarker = $relationMarkerControl.getText()
    Boolean usesDefaultRelationMarker = $usesDefaultRelationMarkerControl.isSelected()
    Boolean modifies = $modifiesControl.isSelected()
    String externalCommand = $externalCommandControl.getText()
    Boolean usesEmbeddedProcessor = $usesEmbeddedProcessorControl.isSelected()
    SlimePdfExportConfig config = SlimePdfExportConfig.new()
    if (!usesDefaultCaptionFontFamily) {
      config.setCaptionFontFamily(captionFontFamily)
    }
    config.setCaptionFontSize(captionFontSize)
    if (!usesDefaultHeadFontFamily) {
      config.setHeadFontFamily(headFontFamily)
    }
    config.setHeadFontSize(headFontSize)
    if (!usesDefaultMainFontFamily) {
      config.setMainFontFamily(mainFontFamily)
    }
    config.setMainFontSize(mainFontSize)
    if (!usesDefaultVariationMarker) {
      config.setVariationMarker(variationMarker)
    }
    if (!usesDefaultRelationMarker) {
      config.setRelationMarker(relationMarker)
    }
    config.setModifies(modifies)
    if (!usesEmbeddedProcessor) {
      config.setExternalCommand(externalCommand)
    }
    $stage.commit(config)
  }

  private void setupFontFamilyControls() {
    List<String> fontFamilies = Font.getFamilies()
    $captionFontFamilyControl.getItems().addAll(fontFamilies)
    $headFontFamilyControl.getItems().addAll(fontFamilies)
    $mainFontFamilyControl.getItems().addAll(fontFamilies)
  }

  private void setupIntegerControls() {
    $captionFontSizeControl.getEditor().setTextFormatter(TextFormatter.new(IntegerUnaryOperator.new()))
    $headFontSizeControl.getEditor().setTextFormatter(TextFormatter.new(IntegerUnaryOperator.new()))
    $mainFontSizeControl.getEditor().setTextFormatter(TextFormatter.new(IntegerUnaryOperator.new()))
  }

  private void bindFontControlProperties() {
    $captionFontFamilyControl.disableProperty().bind($usesDefaultCaptionFontFamilyControl.selectedProperty())
    $headFontFamilyControl.disableProperty().bind($usesDefaultHeadFontFamilyControl.selectedProperty())
    $mainFontFamilyControl.disableProperty().bind($usesDefaultMainFontFamilyControl.selectedProperty())
  }

  private void bindMarkerControlProperties() {
    $variationMarkerControl.disableProperty().bind($usesDefaultVariationMarkerControl.selectedProperty())
    $relationMarkerControl.disableProperty().bind($usesDefaultRelationMarkerControl.selectedProperty())
  }

  private void bindExternalCommandControlProperty() {
    $externalCommandControl.disableProperty().bind($usesEmbeddedProcessorControl.selectedProperty())
  }

}