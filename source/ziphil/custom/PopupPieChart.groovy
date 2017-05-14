package ziphil.custom

import groovy.transform.CompileStatic
import javafx.beans.property.ReadOnlyObjectProperty
import javafx.beans.property.ReadOnlyObjectWrapper
import javafx.scene.chart.PieChart
import javafx.scene.control.Control
import javafx.scene.control.Skin
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class PopupPieChart extends Control {

  private ReadOnlyObjectWrapper<PieChart> $chart = ReadOnlyObjectWrapper.new(PieChart.new())

  public PopupPieChart() {
  }

  protected Skin<PopupPieChart> createDefaultSkin() {
    return PopupPieChartSkin.new(this)
  }

  public PieChart getChart() {
    return $chart.get()
  }

  public ReadOnlyObjectProperty<PieChart> chartProperty() {
    return $chart.getReadOnlyProperty()
  }

}