package ziphil.custom

import groovy.transform.CompileStatic
import javafx.scene.control.ListCell
import javafx.scene.layout.Pane
import ziphil.dictionary.slime.SlimeWord
import ziphilib.transform.Ziphilify
import ziphilib.type.PrimBoolean


@CompileStatic @Ziphilify
public class PlainWordCell extends ListCell<SlimeWord> {

  public PlainWordCell() {
    super()
  }

  protected void updateItem(SlimeWord word, PrimBoolean isEmpty) {
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