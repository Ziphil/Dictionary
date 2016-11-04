package ziphil.custom

import groovy.transform.CompileStatic
import javafx.scene.control.ListCell
import ziphil.dictionary.slime.SlimeWord
import ziphilib.transform.ConvertPrimitive


@CompileStatic @Newify
public class SimpleWordCell extends ListCell<SlimeWord> {

  public SimpleWordCell() {
    super()
  }

  @ConvertPrimitive
  protected void updateItem(SlimeWord word, Boolean isEmpty) {
    super.updateItem(word, isEmpty)
    if (isEmpty || word == null) {
      setText(null)
      setGraphic(null)
    } else {
      if (word.isSimpleChanged()) {
        word.createSimpleContentPane()
      }
      word.getSimpleContentPane().prefWidthProperty().bind(getListView().widthProperty().subtract(Measurement.rpx(29)))
      setText(null)
      setGraphic(word.getSimpleContentPane())
    }
  }

}