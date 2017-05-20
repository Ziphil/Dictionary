package ziphil.custom

import groovy.transform.CompileStatic
import javafx.beans.property.IntegerProperty
import javafx.beans.property.ReadOnlyObjectProperty
import javafx.beans.property.ReadOnlyObjectWrapper
import javafx.beans.property.SimpleIntegerProperty
import javafx.scene.chart.PieChart
import javafx.scene.control.Control
import javafx.scene.control.Skin
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class PopupPieChart extends Control {

  private ReadOnlyObjectWrapper<PieChart> $chart = ReadOnlyObjectWrapper.new(PieChart.new())
  private IntegerProperty $pieValuePrecision = SimpleIntegerProperty.new(0)
  private IntegerProperty $percentagePrecision = SimpleIntegerProperty.new(2)

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

  public Integer getPieValuePrecision() {
    return $pieValuePrecision.get()
  }

  public void setPieValuePrecision(Integer pieValuePrecision) {
    $pieValuePrecision.set(pieValuePrecision)
  }

  public IntegerProperty pieValuePrecisionProperty() {
    return $pieValuePrecision
  }

  public Integer getPercentagePrecision() {
    return $percentagePrecision.get()
  }

  public void setPercentagePrecision(Integer percentagePrecision) {
    $percentagePrecision.set(percentagePrecision)
  }

  public IntegerProperty percentagePrecisionProperty() {
    return $percentagePrecision
  }

}