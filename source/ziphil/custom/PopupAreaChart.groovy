package ziphil.custom

import groovy.transform.CompileStatic
import javafx.beans.property.ReadOnlyObjectProperty
import javafx.beans.property.ReadOnlyObjectWrapper
import javafx.collections.ObservableList
import javafx.scene.chart.AreaChart
import javafx.scene.chart.Axis
import javafx.scene.chart.XYChart
import javafx.scene.control.Control
import javafx.scene.control.Skin
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class PopupAreaChart<X, Y> extends Control {

  private ReadOnlyObjectWrapper<AreaChart<X, Y>> $chart = ReadOnlyObjectWrapper.new()

  public PopupAreaChart(Axis<X> xAxis, Axis<Y> yAxis) {
    $chart.set(AreaChart.new(xAxis, yAxis))
  }

  public PopupAreaChart(Axis<X> xAxis, Axis<Y> yAxis, ObservableList<XYChart.Series<X, Y>> data) {
    $chart.set(AreaChart.new(xAxis, yAxis, data))
  }

  protected Skin<PopupAreaChart<X, Y>> createDefaultSkin() {
    return PopupAreaChartSkin.new(this)
  }

  public AreaChart getChart() {
    return $chart.get()
  }

  public ReadOnlyObjectProperty<AreaChart<X, Y>> chartProperty() {
    return $chart.getReadOnlyProperty()
  }

}