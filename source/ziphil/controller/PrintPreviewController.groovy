package ziphil.controller

import groovy.transform.CompileStatic
import javafx.beans.value.ObservableValue
import javafx.fxml.FXML
import javafx.print.PageLayout
import javafx.print.PageOrientation
import javafx.print.Paper
import javafx.print.PrinterJob
import javafx.scene.Node
import javafx.scene.control.Spinner
import javafx.scene.control.SpinnerValueFactory.IntegerSpinnerValueFactory
import javafx.scene.control.TextFormatter
import javafx.scene.layout.Pane
import ziphil.custom.IntegerUnaryOperator
import ziphil.custom.UtilityStage
import ziphil.dictionary.PageBuilder
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
  private PageBuilder $pageBuilder

  public PrintPreviewController(UtilityStage<Void> stage) {
    super(stage)
    loadResource(RESOURCE_PATH, TITLE, DEFAULT_WIDTH, DEFAULT_HEIGHT, false)
  }

  @FXML
  private void initialize() {
    setupPageNumberControl()
    setupIntegerControl()
  }

  public void prepare(PrinterJob printerJob, PageBuilder pageBuilder) {
    $printerJob = printerJob
    $pageBuilder = pageBuilder
    PageLayout pageLayout = $printerJob.getJobSettings().getPageLayout()
    Paper paper = pageLayout.getPaper()
    PageOrientation orientation = pageLayout.getPageOrientation()
    Double width = (orientation == PageOrientation.PORTRAIT || orientation == PageOrientation.REVERSE_PORTRAIT) ? paper.getWidth() : paper.getHeight()
    Double height = (orientation == PageOrientation.PORTRAIT || orientation == PageOrientation.REVERSE_PORTRAIT) ? paper.getHeight() : paper.getWidth()
    $previewPane.setPrefWidth(width)
    $previewPane.setPrefHeight(height)
    $stage.sizeToScene()
    updatePage(0)
  }

  private Boolean updatePage(Int pageNumber) {
    Node page = $pageBuilder.createPage(pageNumber)
    if (page != null) {
      PageLayout pageLayout = $printerJob.getJobSettings().getPageLayout()
      $previewPane.getChildren().clear()
      $previewPane.getChildren().add(page)
      page.relocate(pageLayout.getLeftMargin(), pageLayout.getTopMargin())
      return true
    } else {
      return false
    }
  }

  private void setupPageNumberControl() {
    $pageNumberControl.valueProperty().addListener() { ObservableValue<? extends IntegerClass> observableValue, IntegerClass oldValue, IntegerClass newValue ->
      Boolean exists = updatePage(newValue - 1)
      if (!exists) {
        $pageNumberControl.getValueFactory().setValue(oldValue)
      }
    }
  }

  private void setupIntegerControl() {
    $pageNumberControl.getEditor().setTextFormatter(TextFormatter.new(IntegerUnaryOperator.new()))
  }

}