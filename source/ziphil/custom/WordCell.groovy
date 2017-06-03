package ziphil.custom

import groovy.transform.CompileStatic
import javafx.scene.control.ListCell
import javafx.scene.layout.Pane
import ziphil.dictionary.Element
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class WordCell extends ListCell<Element> {

  public WordCell() {
    super()
  }

  protected void updateItem(Element word, Boolean empty) {
    super.updateItem(word, empty)
    if (empty || word == null) {
      setText(null)
      setGraphic(null)
    } else {
      Pane graphic = word.getContentPaneFactory().create()
      graphic.prefWidthProperty().bind(getListView().widthProperty().subtract(Measurement.rpx(29)))
      setText(null)
      setGraphic(graphic)
    }
  }

}