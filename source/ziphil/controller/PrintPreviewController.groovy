package ziphil.controller

import groovy.transform.CompileStatic
import javafx.fxml.FXML
import javafx.print.PrinterJob
import javafx.scene.Node
import javafx.scene.control.Spinner
import javafx.scene.control.TextFormatter
import javafx.scene.layout.Pane
import ziphil.custom.IntegerUnaryOperator
import ziphil.custom.UtilityStage
import ziphil.dictionary.PrintPageBuilder
import ziphil.module.JavaVersion
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class PrintPreviewController extends Controller<Void> {

  private static final String RESOURCE_PATH = "resource/fxml/controller/print_preview.fxml"
  private static final String TITLE = "印刷プレビュー"
  private static final Double DEFAULT_WIDTH = -1
  private static final Double DEFAULT_HEIGHT = -1

  @FXML private Pane $previewPane
  @FXML private Spinner<IntegerClass> $pageNumberControl
  private PrinterJob $printerJob
  private PrintPageBuilder $builder

  public PrintPreviewController(UtilityStage<Void> stage) {
    super(stage)
    loadResource(RESOURCE_PATH, TITLE, DEFAULT_WIDTH, DEFAULT_HEIGHT, false)
  }

  @FXML
  private void initialize() {
    setupIntegerControl()
  }

  public void prepare(PrinterJob printerJob, PrintPageBuilder builder) {
    $printerJob = printerJob
    $builder = builder
  }

  private void setupIntegerControl() {
    $pageNumberControl.getEditor().setTextFormatter(TextFormatter.new(IntegerUnaryOperator.new()))
  }

}