package ziphil.custom

import groovy.transform.CompileStatic
import javafx.beans.DefaultProperty
import javafx.beans.property.IntegerProperty
import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.scene.chart.PieChart
import javafx.scene.control.Control
import javafx.scene.control.Skin
import ziphilib.transform.Ziphilify


@DefaultProperty("chart")
@CompileStatic @Ziphilify
public class PopupPieChart extends Control {

  private ObjectProperty<PieChart> $chart = SimpleObjectProperty.new(null)
  private IntegerProperty $pieValuePrecision = SimpleIntegerProperty.new(0)
  private IntegerProperty $percentagePrecision = SimpleIntegerProperty.new(2)

  public PopupPieChart() {
  }

  public PopupPieChart(PieChart chart) {
    $chart.set(chart)
  }

  protected Skin<PopupPieChart> createDefaultSkin() {
    return PopupPieChartSkin.new(this)
  }

  public PieChart getChart() {
    return $chart.get()
  }

  public void setChart(PieChart chart) {
    $chart.set(chart)
  }

  public ObjectProperty<PieChart> chartProperty() {
    return $chart
  }

  public Int getPieValuePrecision() {
    return $pieValuePrecision.get()
  }

  public void setPieValuePrecision(Int pieValuePrecision) {
    $pieValuePrecision.set(pieValuePrecision)
  }

  public IntegerProperty pieValuePrecisionProperty() {
    return $pieValuePrecision
  }

  public Int getPercentagePrecision() {
    return $percentagePrecision.get()
  }

  public void setPercentagePrecision(Int percentagePrecision) {
    $percentagePrecision.set(percentagePrecision)
  }

  public IntegerProperty percentagePrecisionProperty() {
    return $percentagePrecision
  }

}