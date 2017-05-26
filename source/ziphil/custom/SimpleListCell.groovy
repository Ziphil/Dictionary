package ziphil.custom

import groovy.transform.CompileStatic
import javafx.scene.control.ListCell
import ziphilib.transform.Ziphilify
import ziphilib.type.PrimBoolean


@CompileStatic @Ziphilify
public class SimpleListCell<T> extends ListCell<T> {

  public SimpleListCell() {
    super()
  }

  protected void updateItem(T item, PrimBoolean empty) {
    super.updateItem(item, empty)
    if (empty || item == null) {
      setText(null)
      setGraphic(null)
    } else {
      setText(item.toString())
      setGraphic(null)
    }
  }

}