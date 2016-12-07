package ziphil.custom

import groovy.transform.CompileStatic
import javafx.scene.control.ListCell
import ziphilib.transform.ConvertPrimitiveArgs
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class SimpleListCell<T> extends ListCell<T> {

  public SimpleListCell() {
    super()
  }

  @ConvertPrimitiveArgs
  protected void updateItem(T item, Boolean isEmpty) {
    super.updateItem(item, isEmpty)
    if (isEmpty || item == null) {
      setText(null)
      setGraphic(null)
    } else {
      setText(item.toString())
      setGraphic(null)
    }
  }

}