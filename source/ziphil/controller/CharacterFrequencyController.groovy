package ziphil.controller

import groovy.transform.CompileStatic
import javafx.application.Platform
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.fxml.FXML
import javafx.geometry.Side
import javafx.scene.Node
import javafx.scene.chart.PieChart
import javafx.scene.control.TableColumn
import javafx.scene.control.TableView
import ziphil.dictionary.Dictionary
import ziphil.dictionary.Word
import ziphil.custom.ExtensionFilter
import ziphil.custom.Measurement
import ziphil.custom.PercentageTableCell
import ziphil.custom.PopupPieChart
import ziphil.custom.UtilityStage
import ziphil.module.CharacterFrequencyAnalyzer
import ziphil.module.CharacterStatus
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class CharacterFrequencyController extends Controller<Void> {

  private static final String RESOURCE_PATH = "resource/fxml/controller/character_frequency.fxml"
  private static final String TITLE = "文字頻度"
  private static final Double DEFAULT_WIDTH = Measurement.rpx(480)
  private static final Double DEFAULT_HEIGHT = Measurement.rpx(410)
  protected static final Int MAX_PIE_SIZE = 20

  @FXML private PopupPieChart $frequencyChart
  @FXML private TableView<CharacterStatus> $frequencyView
  @FXML private TableColumn<CharacterStatus, DoubleClass> $frequencyPercentageColumn
  @FXML private TableColumn<CharacterStatus, DoubleClass> $usingWordSizePercentageColumn
  private CharacterFrequencyAnalyzer $analyzer

  public CharacterFrequencyController(UtilityStage<? super Void> stage) {
    super(stage)
    loadResource(RESOURCE_PATH, TITLE, DEFAULT_WIDTH, DEFAULT_HEIGHT, true)
  }

  @FXML
  private void initialize() {
    setupFrequencyViewColumns()
  }

  public void prepare(CharacterFrequencyAnalyzer analyzer) {
    List<CharacterStatus> characterStatuses = analyzer.characterStatuses()
    List<PieChart.Data> data = ArrayList.new()
    Int otherFrequency = 0
    for (Int i = 0 ; i < characterStatuses.size() ; i ++) {
      CharacterStatus status = characterStatuses[i]
      if (i < MAX_PIE_SIZE) {
        PieChart.Data singleData = PieChart.Data.new(status.getCharacter(), status.getFrequency())
        data.add(singleData)
      } else {
        otherFrequency += status.getFrequency()
      }
    }
    PieChart.Data otherSingleData = null
    if (otherFrequency > 0) {
      otherSingleData = PieChart.Data.new("その他", otherFrequency)
      data.add(otherSingleData)
    }
    $frequencyChart.getChart().setData(FXCollections.observableArrayList(data))
    if (otherSingleData != null) {
      otherSingleData.getNode().getStyleClass().add("other")
      Platform.runLater() {
        Node otherLegendNode = $frequencyChart.lookup(".data${MAX_PIE_SIZE}.pie-legend-symbol")
        otherLegendNode.getStyleClass().add("other-legend-symbol")
      }
    }
    $frequencyView.getItems().addAll(characterStatuses)
    $analyzer = analyzer
  }

  @FXML
  private void saveCsv() {
    UtilityStage<File> nextStage = createStage()
    FileChooserController controller = FileChooserController.new(nextStage)
    List<ExtensionFilter> extensionFilters = ArrayList.new()
    ExtensionFilter csvExtensionFilter = ExtensionFilter.new("CSVファイル", "csv")
    ExtensionFilter tsvExtensionFilter = ExtensionFilter.new("TSVファイル", "tsv")
    extensionFilters.addAll(csvExtensionFilter, tsvExtensionFilter)
    controller.prepare(extensionFilters, csvExtensionFilter, true)
    nextStage.showAndWait()
    if (nextStage.isCommitted() && nextStage.getResult() != null) {
      File file = nextStage.getResult()
      $analyzer.save(file.getAbsolutePath())
    }
  }

  private void setupFrequencyViewColumns() {
    $frequencyPercentageColumn.setCellFactory() { TableColumn<CharacterStatus, DoubleClass> column ->
      return PercentageTableCell.new(3)
    }
    $usingWordSizePercentageColumn.setCellFactory() { TableColumn<CharacterStatus, DoubleClass> column ->
      return PercentageTableCell.new(3)
    }
  }

}