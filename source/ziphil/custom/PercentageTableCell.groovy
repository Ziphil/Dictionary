package ziphil.custom

import groovy.transform.CompileStatic
import javafx.scene.control.TableCell
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class PercentageTableCell<S> extends TableCell<S, DoubleClass> {

  private Int $precision = 2

  public PercentageTableCell() {
    super()
  }

  public PercentageTableCell(Int precision) {
    super()
    $precision = precision
  }

  protected void updateItem(DoubleClass item, Boolean empty) {
    super.updateItem(item, empty)
    if (empty || item == null) {
      setText(null)
      setGraphic(null)
    } else {
      setText(String.format("%.${$precision}f%%", item))
      setGraphic(null)
    }
  }

}