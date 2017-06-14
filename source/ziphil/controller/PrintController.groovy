package ziphil.controller

import groovy.transform.CompileStatic
import javafx.beans.value.ObservableValue
import javafx.concurrent.Task
import javafx.fxml.FXML
import javafx.scene.Node
import javafx.scene.control.ComboBox
import javafx.scene.control.Spinner
import javafx.scene.control.SpinnerValueFactory.IntegerSpinnerValueFactory
import javafx.scene.control.TextFormatter
import javafx.print.PageLayout
import javafx.print.Printer
import javafx.print.PrinterJob
import ziphil.custom.IntegerUnaryOperator
import ziphil.custom.SimpleTask
import ziphil.custom.UtilityStage
import ziphil.dictionary.Dictionary
import ziphil.dictionary.Element
import ziphil.dictionary.PrintPageBuilder
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class PrintController extends Controller<Void> {

  private static final String RESOURCE_PATH = "resource/fxml/controller/print.fxml"
  private static final String TITLE = "印刷"
  private static final Double DEFAULT_WIDTH = -1
  private static final Double DEFAULT_HEIGHT = -1

  @FXML private ComboBox<Printer> $printerControl
  @FXML private Spinner<IntegerClass> $startIndexControl
  @FXML private Spinner<IntegerClass> $endIndexControl
  @FXML private Spinner<IntegerClass> $fontSizeControl
  @FXML private Spinner<IntegerClass> $columnSizeControl
  private List<Element> $words
  private PrinterJob $printerJob = PrinterJob.createPrinterJob()

  public PrintController(UtilityStage<Void> stage) {
    super(stage)
    loadResource(RESOURCE_PATH, TITLE, DEFAULT_WIDTH, DEFAULT_HEIGHT, false)
  }

  @FXML
  private void initialize() {
    setupPrinterControl()
    setupIntegerControls()
  }

  public void prepare(Dictionary dictionary) {
    $words = ArrayList.new(dictionary.getWholeWords())
    setupIndexControls()
  }

  @FXML
  protected void commit() {
    Task<Void> task = SimpleTask.new() {
      Int startIndex = $startIndexControl.getValue() - 1
      Int endIndex = $endIndexControl.getValue()
      PageLayout pageLayout = $printerJob.getJobSettings().getPageLayout()
      Int fontSize = $fontSizeControl.getValue()
      Int columnSize = $columnSizeControl.getValue()
      PrintPageBuilder builder = PrintPageBuilder.new($words, startIndex, endIndex)
      builder.setPageLayout(pageLayout)
      builder.setFontSize(fontSize)
      builder.setColumnSize(columnSize)
      for (Node page ; (page = builder.nextPage()) != null ;) {
        $printerJob.printPage(page)
      }
      $printerJob.endJob()
    }
    Thread thread = Thread.new(task)
    thread.setDaemon(true)
    thread.start()
    $stage.commit(null)
  }

  @FXML
  private void configPrinter() {
    $printerJob.showPrintDialog($stage)
  }

  @FXML
  private void configPageLayout() {
    $printerJob.showPageSetupDialog($stage)
  }

  private void setupPrinterControl() {
    $printerControl.getItems().addAll(Printer.getAllPrinters())
    $printerControl.setValue(Printer.getDefaultPrinter())
    $printerControl.valueProperty().bindBidirectional($printerJob.printerProperty())
  }

  private void setupIndexControls() {
    IntegerSpinnerValueFactory startIndexValueFactory = (IntegerSpinnerValueFactory)$startIndexControl.getValueFactory()
    IntegerSpinnerValueFactory endIndexValueFactory = (IntegerSpinnerValueFactory)$endIndexControl.getValueFactory()
    startIndexValueFactory.setMax($words.size())
    startIndexValueFactory.setMin(1)
    startIndexValueFactory.setValue(1)
    endIndexValueFactory.setMax($words.size())
    endIndexValueFactory.setMin(1)
    endIndexValueFactory.setValue($words.size())
    $startIndexControl.valueProperty().addListener() { ObservableValue<? extends IntegerClass> observableValue, IntegerClass oldValue, IntegerClass newValue ->
      if (newValue > $endIndexControl.getValue()) {
        $endIndexControl.getValueFactory().setValue(newValue)
      }
    }
    $endIndexControl.valueProperty().addListener() { ObservableValue<? extends IntegerClass> observableValue, IntegerClass oldValue, IntegerClass newValue ->
      if (newValue < $startIndexControl.getValue()) {
        $startIndexControl.getValueFactory().setValue(newValue)
      }
    }
  }

  private void setupIntegerControls() {
    $startIndexControl.getEditor().setTextFormatter(TextFormatter.new(IntegerUnaryOperator.new()))
    $endIndexControl.getEditor().setTextFormatter(TextFormatter.new(IntegerUnaryOperator.new()))
    $fontSizeControl.getEditor().setTextFormatter(TextFormatter.new(IntegerUnaryOperator.new()))
    $columnSizeControl.getEditor().setTextFormatter(TextFormatter.new(IntegerUnaryOperator.new()))
  }

}