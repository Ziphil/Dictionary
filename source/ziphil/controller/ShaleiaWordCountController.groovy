package ziphil.controller

import groovy.transform.CompileStatic
import javafx.beans.value.ObservableValue
import javafx.fxml.FXML
import javafx.geometry.Side
import javafx.scene.chart.NumberAxis
import javafx.scene.chart.XYChart
import javafx.scene.control.Spinner
import javafx.scene.control.SpinnerValueFactory.IntegerSpinnerValueFactory
import javafx.scene.layout.Priority
import javafx.scene.layout.VBox
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
  @FXML private Spinner<Integer> $startDateControl
  @FXML private Spinner<Integer> $endDateControl

  public ShaleiaWordCountController(UtilityStage<? super Void> stage) {
    super(stage)
    loadResource(RESOURCE_PATH, TITLE, DEFAULT_WIDTH, DEFAULT_HEIGHT, true)
  }

  public void prepare(NumberAxis xAxis, NumberAxis yAxis, XYChart.Series<Number, Number> series) {
    prepareDateControls(xAxis, yAxis, series)
    prepareMainPane(xAxis, yAxis, series)
  }

  private void prepareDateControls(NumberAxis xAxis, NumberAxis yAxis, XYChart.Series<Number, Number> series) {
    IntegerSpinnerValueFactory startDateValueFactory = (IntegerSpinnerValueFactory)$startDateControl.getValueFactory()
    IntegerSpinnerValueFactory endDateValueFactory = (IntegerSpinnerValueFactory)$endDateControl.getValueFactory()
    Int maxDate = (Int)series.getData()[-1].getXValue()
    startDateValueFactory.setMin(1)
    startDateValueFactory.setMax(maxDate)
    startDateValueFactory.setValue(1000)
    endDateValueFactory.setMin(1)
    endDateValueFactory.setMax(maxDate)
    endDateValueFactory.setValue(maxDate)
    $startDateControl.valueProperty().addListener() { ObservableValue<? extends IntegerClass> observableValue, IntegerClass oldValue, IntegerClass newValue ->
      if (newValue > $endDateControl.getValue()) {
        $endDateControl.getValueFactory().setValue(newValue)
      }
      xAxis.setLowerBound(newValue)
    }
    $endDateControl.valueProperty().addListener() { ObservableValue<? extends IntegerClass> observableValue, IntegerClass oldValue, IntegerClass newValue ->
      if (newValue < $startDateControl.getValue()) {
        $startDateControl.getValueFactory().setValue(newValue)
      }
      xAxis.setUpperBound(newValue)
    }
  }

  private void prepareMainPane(NumberAxis xAxis, NumberAxis yAxis, XYChart.Series<Number, Number> series) {
    PopupAreaChart<Number, Number> chart = PopupAreaChart.new(xAxis, yAxis)
    chart.getChart().getData().add(series)
    chart.getChart().setLegendSide(Side.RIGHT)
    chart.getChart().setAnimated(false)
    chart.getChart().getStyleClass().add("right-legend-chart")
    $mainPane.getChildren().add(0, chart)
    $mainPane.setVgrow(chart, Priority.ALWAYS)
  }

}