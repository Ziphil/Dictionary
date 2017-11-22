package ziphil.controller

import groovy.transform.CompileStatic
import javafx.beans.value.ObservableValue
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.collections.transformation.FilteredList
import javafx.fxml.FXML
import javafx.scene.chart.NumberAxis
import javafx.scene.chart.XYChart
import javafx.scene.control.Spinner
import javafx.scene.control.SpinnerValueFactory.IntegerSpinnerValueFactory
import javafx.scene.layout.VBox
import javafx.util.converter.NumberStringConverter
import ziphil.custom.Measurement
import ziphil.custom.PopupAreaChart
import ziphil.custom.UtilityStage
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class ShaleiaWordCountController extends Controller<Void> {

  private static final String RESOURCE_PATH = "resource/fxml/controller/sheleia_word_count.fxml"
  private static final String TITLE = "単語数グラフ"
  private static final Double DEFAULT_WIDTH = Measurement.rpx(640)
  private static final Double DEFAULT_HEIGHT = Measurement.rpx(480)

  @FXML private VBox $mainPane
  @FXML private PopupAreaChart<Number, Number> $chart
  @FXML private Spinner<Integer> $startDateControl
  @FXML private Spinner<Integer> $endDateControl
  private List<XYChart.Data<Number, Number>> $data

  public ShaleiaWordCountController(UtilityStage<? super Void> stage) {
    super(stage)
    loadResource(RESOURCE_PATH, TITLE, DEFAULT_WIDTH, DEFAULT_HEIGHT, true)
  }

  @FXML
  private void initialize() {
    setupChart()
  }

  public void prepare(List<XYChart.Data<Number, Number>> data) {
    $data = data
    prepareDateControls()
  }

  private void prepareDateControls() {
    IntegerSpinnerValueFactory startDateValueFactory = (IntegerSpinnerValueFactory)$startDateControl.getValueFactory()
    IntegerSpinnerValueFactory endDateValueFactory = (IntegerSpinnerValueFactory)$endDateControl.getValueFactory()
    Int maxDate = (Int)$data.last().getXValue()
    startDateValueFactory.setMax(maxDate)
    startDateValueFactory.setMin(1000)
    endDateValueFactory.setMax(maxDate)
    endDateValueFactory.setMin(1)
    $startDateControl.valueProperty().addListener() { ObservableValue<? extends IntegerClass> observableValue, IntegerClass oldValue, IntegerClass newValue ->
      if (newValue > $endDateControl.getValue()) {
        $endDateControl.getValueFactory().setValue(newValue)
      }
      resetData()
    }
    $endDateControl.valueProperty().addListener() { ObservableValue<? extends IntegerClass> observableValue, IntegerClass oldValue, IntegerClass newValue ->
      if (newValue < $startDateControl.getValue()) {
        $startDateControl.getValueFactory().setValue(newValue)
      }
      resetData()
    }
    startDateValueFactory.setValue(1000)
    endDateValueFactory.setValue(maxDate)
  }

  private void resetData() {
    Int startDate = $startDateControl.getValue()
    Int endDate = $endDateControl.getValue()
    ObservableList<XYChart.Data<Number, Number>> nextData = FXCollections.observableArrayList()
    for (XYChart.Data<Number, Number> singleData : $data) {
      if (singleData.getXValue() >= startDate && singleData.getXValue() <= endDate) {
        nextData.add(singleData)
      }
    }
    XYChart.Series<Number, Number> series = XYChart.Series.new(nextData)
    series.setName("単語数")
    $chart.getChart().getData().clear()
    $chart.getChart().getData().add(series)
    NumberAxis xAxis = (NumberAxis)$chart.getChart().getXAxis()
    xAxis.setLowerBound(startDate)
    xAxis.setUpperBound(endDate)
  }

  private void setupChart() {
    NumberAxis xAxis = (NumberAxis)$chart.getChart().getXAxis()
    NumberAxis yAxis = (NumberAxis)$chart.getChart().getYAxis()
    xAxis.setTickLabelFormatter(NumberStringConverter.new("0"))
    yAxis.setTickLabelFormatter(NumberStringConverter.new("0"))
  }

}