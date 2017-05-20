package ziphil.custom

import groovy.transform.CompileStatic
import javafx.scene.control.TableCell
import ziphilib.transform.Ziphilify
import ziphilib.type.PrimBoolean


@CompileStatic @Ziphilify
public class PercentageTableCell<S> extends TableCell<S, Double> {

  private Integer $precision = 2

  public PercentageTableCell() {
    super()
  }

  public PercentageTableCell(Integer precision) {
    super()
    $precision = precision
  }

  protected void updateItem(Double item, PrimBoolean isEmpty) {
    super.updateItem(item, isEmpty)
    if (isEmpty || item == null) {
      setText(null)
      setGraphic(null)
    } else {
      setText(String.format("%.${$precision}f%%", item))
      setGraphic(null)
    }
  }

}