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
import javafx.scene.control.Tooltip
import ziphil.dictionary.CharacterStatus
import ziphil.dictionary.Dictionary
import ziphil.dictionary.DictionaryStatisticsCalculator
import ziphil.dictionary.Word
import ziphil.custom.Measurement
import ziphil.custom.PercentageTableCell
import ziphil.custom.PopupPieChart
import ziphil.custom.UtilityStage
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class CharacterFrequencyController extends Controller<Void> {

  private static final String RESOURCE_PATH = "resource/fxml/controller/character_frequency.fxml"
  private static final String TITLE = "文字頻度"
  private static final Double DEFAULT_WIDTH = Measurement.rpx(480)
  private static final Double DEFAULT_HEIGHT = Measurement.rpx(410)
  protected static final Integer MAX_PIE_SIZE = 20

  @FXML private PopupPieChart $frequencyChart
  @FXML private TableView<CharacterStatus> $frequencyView
  @FXML private TableColumn<CharacterStatus, Double> $frequencyPercentageColumn
  @FXML private TableColumn<CharacterStatus, Double> $usingWordSizePercentageColumn

  public CharacterFrequencyController(UtilityStage<Void> stage) {
    super(stage)
    loadResource(RESOURCE_PATH, TITLE, DEFAULT_WIDTH, DEFAULT_HEIGHT, true)
  }

  @FXML
  private void initialize() {
    setupFrequencyChart()
    setupFrequencyViewColumns()
  }

  public void prepare(DictionaryStatisticsCalculator calculator) {
    List<CharacterStatus> characterStatuses = calculator.characterStatuses()
    List<PieChart.Data> data = ArrayList.new()
    Integer otherFrequency = 0
    for (Integer i : 0 ..< characterStatuses.size()) {
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
  }

  private void setupFrequencyChart() {
    $frequencyChart.getChart().setLegendSide(Side.RIGHT)
    $frequencyChart.getChart().setStartAngle(90)
    $frequencyChart.getChart().setAnimated(false)
  }

  private void setupFrequencyViewColumns() {
    $frequencyPercentageColumn.setCellFactory() { TableColumn<CharacterStatus, Double> column ->
      return PercentageTableCell.new()
    }
    $usingWordSizePercentageColumn.setCellFactory() { TableColumn<CharacterStatus, Double> column ->
      return PercentageTableCell.new()
    }
  }

}