package ziphil.custom

import groovy.transform.CompileStatic
import javafx.beans.DefaultProperty
import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.collections.ObservableList
import javafx.scene.chart.AreaChart
import javafx.scene.chart.Axis
import javafx.scene.chart.XYChart
import javafx.scene.control.Control
import javafx.scene.control.Skin
import ziphilib.transform.Ziphilify


@DefaultProperty("chart")
@CompileStatic @Ziphilify
public class PopupAreaChart<X, Y> extends Control {

  private ObjectProperty<AreaChart<X, Y>> $chart = SimpleObjectProperty.new(null)

  public PopupAreaChart() {
  }

  public PopupAreaChart(AreaChart<X, Y> chart) {
    $chart.set(chart)
  }

  protected Skin<PopupAreaChart<X, Y>> createDefaultSkin() {
    return PopupAreaChartSkin.new(this)
  }

  public AreaChart<X, Y> getChart() {
    return $chart.get()
  }

  public void setChart(AreaChart<X, Y> chart) {
    $chart.set(chart)
  }

  public ObjectProperty<AreaChart<X, Y>> chartProperty() {
    return $chart
  }

}