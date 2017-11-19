package ziphil.controller

import groovy.transform.CompileStatic
import javafx.fxml.FXML
import javafx.geometry.Side
import javafx.scene.chart.Axis
import javafx.scene.chart.XYChart
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

  public ShaleiaWordCountController(UtilityStage<? super Void> stage) {
    super(stage)
    loadResource(RESOURCE_PATH, TITLE, DEFAULT_WIDTH, DEFAULT_HEIGHT, true)
  }

  public void prepare(Axis<Number> xAxis, Axis<Number> yAxis, XYChart.Series<Number, Number> series) {
    PopupAreaChart<Number, Number> chart = PopupAreaChart.new(xAxis, yAxis)
    chart.getChart().getData().add(series)
    chart.getChart().setLegendSide(Side.RIGHT)
    chart.getChart().setAnimated(false)
    chart.getChart().getStyleClass().add("right-legend-chart")
    $mainPane.getChildren().add(chart)
  }

}