package ziphil.controller

import groovy.transform.CompileStatic
import javafx.beans.value.ObservableValue
import javafx.concurrent.Task
import javafx.fxml.FXML
import javafx.scene.Group
import javafx.scene.Node
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.control.ComboBox
import javafx.scene.control.Spinner
import javafx.scene.control.SpinnerValueFactory.IntegerSpinnerValueFactory
import javafx.scene.layout.Pane
import javafx.scene.layout.Region
import javafx.scene.layout.VBox
import javafx.print.PageLayout
import javafx.print.Printer
import javafx.print.PrinterJob
import ziphil.custom.Measurement
import ziphil.custom.SimpleTask
import ziphil.custom.UtilityStage
import ziphil.dictionary.Dictionary
import ziphil.dictionary.Element
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class PrintController extends Controller<Void> {

  private static final String RESOURCE_PATH = "resource/fxml/controller/print.fxml"
  private static final String PRINT_STYLESHEET_PATH = "resource/css/main/print.css"
  private static final String TITLE = "印刷"
  private static final Double DEFAULT_WIDTH = -1
  private static final Double DEFAULT_HEIGHT = -1
  private static final Int MAX_WORD_SIZE = 50

  @FXML private ComboBox<Printer> $printerControl
  @FXML private Spinner<IntegerClass> $fontSizeControl
  @FXML private Spinner<IntegerClass> $startIndexControl
  @FXML private Spinner<IntegerClass> $endIndexControl
  private List<Element> $words
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
    $words = ArrayList.new(dictionary.getWholeWords())
    setupIndexControls()
  }

  @FXML
  protected void commit() {
    Task<Void> task = SimpleTask.new() {
      Pane mainPane = createMainPane()
      Scene scene = createScene(mainPane)
      PageLayout layout = $printerJob.getJobSettings().getPageLayout()
      Int startIndex = $startIndexControl.getValue() - 1
      Int endIndex = $endIndexControl.getValue()
      for (Int i = startIndex ; i < endIndex ; i ++) {
        Element word = $words[i]
        Pane pane = word.getContentPaneFactory().create(true)
        Parent root = scene.getRoot()
        mainPane.getChildren().add(pane)
        root.applyCss()
        root.layout()
        if (mainPane.getHeight() > layout.getPrintableHeight()) {
          mainPane.getChildren().remove(pane)
          $printerJob.printPage(root)
          mainPane = createMainPane()
          scene = createScene(mainPane)
          mainPane.getChildren().add(pane)
        }
      }
      $printerJob.printPage(scene.getRoot())
      $printerJob.endJob()
    }
    Thread thread = Thread.new(task)
    thread.setDaemon(true)
    thread.start()
    $stage.commit(null)
  }

  private Pane createMainPane() {
    VBox box = VBox.new(Measurement.rpx(3))
    URL stylesheetURL = getClass().getClassLoader().getResource(PRINT_STYLESHEET_PATH)
    PageLayout layout = $printerJob.getJobSettings().getPageLayout()
    box.setPrefWidth(layout.getPrintableWidth())
    box.getStylesheets().add(stylesheetURL.toString())
    StringBuilder style = StringBuilder.new()
    style.append("-fx-font-size: ")
    style.append($fontSizeControl.getValue())
    style.append(";")
    box.setStyle(style.toString())
    return box
  }

  private Scene createScene(Node node) {
    Group group = Group.new()
    Scene scene = Scene.new(group)
    group.getChildren().add(node)
    return scene
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
    endIndexValueFactory.setValue(MAX_WORD_SIZE)
    $startIndexControl.valueProperty().addListener() { ObservableValue<? extends IntegerClass> observableValue, IntegerClass oldValue, IntegerClass newValue ->
      if (newValue > $endIndexControl.getValue()) {
        $endIndexControl.getValueFactory().setValue(newValue)
      }
      if (newValue <= $endIndexControl.getValue() - MAX_WORD_SIZE) {
        $endIndexControl.getValueFactory().setValue(newValue + MAX_WORD_SIZE - 1)
      }
    }
    $endIndexControl.valueProperty().addListener() { ObservableValue<? extends IntegerClass> observableValue, IntegerClass oldValue, IntegerClass newValue ->
      if (newValue < $startIndexControl.getValue()) {
        $startIndexControl.getValueFactory().setValue(newValue)
      }
      if (newValue >= $startIndexControl.getValue() + MAX_WORD_SIZE) {
        $startIndexControl.getValueFactory().setValue(newValue - MAX_WORD_SIZE + 1)
      }
    }
  }

}