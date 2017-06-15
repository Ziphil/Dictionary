package ziphil.controller

import groovy.transform.CompileStatic
import javafx.beans.value.ObservableValue
import javafx.concurrent.Task
import javafx.fxml.FXML
import javafx.print.PageLayout
import javafx.print.Printer
import javafx.print.PrinterJob
import javafx.scene.Node
import javafx.scene.control.ComboBox
import javafx.scene.control.Spinner
import javafx.scene.control.SpinnerValueFactory.IntegerSpinnerValueFactory
import javafx.scene.control.TextFormatter
import javafx.stage.Modality
import javafx.stage.StageStyle
import ziphil.custom.Dialog
import ziphil.custom.IntegerUnaryOperator
import ziphil.custom.SimpleTask
import ziphil.custom.UtilityStage
import ziphil.dictionary.Dictionary
import ziphil.dictionary.Element
import ziphil.dictionary.PageBuilder
import ziphil.module.JavaVersion
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
    Boolean cancelled = false
    JavaVersion version = JavaVersion.current()
    if (version < JavaVersion.parseString("1.8.0_112")) {
      Dialog dialog = Dialog.new(StageStyle.UTILITY)
      dialog.initOwner($stage)
      dialog.setTitle("注意")
      dialog.setContentText("現在実行中のJavaのバージョンでは、印刷開始までに数分かかる可能性があります。Javaを最新のものにアップデートすることをお勧めします。このまま印刷を実行しますか?")
      dialog.showAndWait()
      if (dialog.isCancelled()) {
        cancelled = true
      }
    }
    if (!cancelled) {
      Task<Void> task = SimpleTask.new() {
        PageBuilder pageBuilder = createPageBuilder()
        for (Int i = 0 ; i < pageBuilder.pageSize() ; i ++) {
          Node page = pageBuilder.createPage(i)
          $printerJob.printPage(page)
        }
        $printerJob.endJob()
      }
      Thread thread = Thread.new(task)
      thread.setDaemon(true)
      thread.start()
      $stage.commit(null)
    }
  }

  private PageBuilder createPageBuilder() {
    Int startIndex = $startIndexControl.getValue() - 1
    Int endIndex = $endIndexControl.getValue()
    PageLayout pageLayout = $printerJob.getJobSettings().getPageLayout()
    Int fontSize = $fontSizeControl.getValue()
    Int columnSize = $columnSizeControl.getValue()
    PageBuilder pageBuilder = PageBuilder.new($words, startIndex, endIndex)
    pageBuilder.setPageLayout(pageLayout)
    pageBuilder.setFontSize(fontSize)
    pageBuilder.setColumnSize(columnSize)
    pageBuilder.prepare()
    return pageBuilder
  }

  @FXML
  private void configPrinter() {
    $printerJob.showPrintDialog($stage)
  }

  @FXML
  private void configPageLayout() {
    $printerJob.showPageSetupDialog($stage)
  }

  @FXML
  private void showPreview() {
    UtilityStage<Void> nextStage = UtilityStage.new(StageStyle.UTILITY)
    PrintPreviewController controller = PrintPreviewController.new(nextStage)
    nextStage.initModality(Modality.APPLICATION_MODAL)
    nextStage.initOwner($stage)
    controller.prepare($printerJob, createPageBuilder())
    nextStage.showAndWait()
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