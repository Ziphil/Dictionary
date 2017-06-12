package ziphil.controller

import groovy.transform.CompileStatic
import javafx.fxml.FXML
import javafx.scene.control.ComboBox
import javafx.print.Printer
import javafx.print.PrinterJob
import ziphil.custom.Measurement
import ziphil.custom.UtilityStage
import ziphil.dictionary.Dictionary
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class PrintController extends Controller<Void> {

  private static final String RESOURCE_PATH = "resource/fxml/controller/print.fxml"
  private static final String TITLE = "印刷"
  private static final Double DEFAULT_WIDTH = -1
  private static final Double DEFAULT_HEIGHT = -1

  @FXML private ComboBox<Printer> $printerControl
  private PrinterJob $printerJob = PrinterJob.createPrinterJob()

  public PrintController(UtilityStage<Void> stage) {
    super(stage)
    loadResource(RESOURCE_PATH, TITLE, DEFAULT_WIDTH, DEFAULT_HEIGHT, false)
  }

  @FXML
  private void initialize() {
    setupPrinterControl()
  }

  public void prepare(Dictionary dictionary) {
  }

  @FXML
  private void configPrinter() {
    $printerJob.showPageSetupDialog($stage)
  }

  private void setupPrinterControl() {
    $printerControl.getItems().addAll(Printer.getAllPrinters())
    $printerControl.setValue(Printer.getDefaultPrinter())
    $printerControl.valueProperty().bindBidirectional($printerJob.printerProperty())
  }

}