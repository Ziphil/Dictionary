package ziphil.custom

import groovy.transform.CompileStatic
import javafx.scene.control.ListCell
import javafx.scene.layout.Pane
import ziphil.dictionary.slime.SlimeWord
import ziphilib.transform.ConvertPrimitiveArgs
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class PlainWordCell extends ListCell<SlimeWord> {

  public PlainWordCell() {
    super()
  }

  @ConvertPrimitiveArgs
  protected void updateItem(SlimeWord word, Boolean isEmpty) {
    super.updateItem(word, isEmpty)
    if (isEmpty || word == null) {
      setText(null)
      setGraphic(null)
    } else {
      Pane graphic = word.getPlainContentPaneFactory().create()
      graphic.prefWidthProperty().bind(getListView().widthProperty().subtract(Measurement.rpx(29)))
      setText(null)
      setGraphic(graphic)
    }
  }

}