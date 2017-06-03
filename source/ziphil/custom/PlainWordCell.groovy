package ziphil.custom

import groovy.transform.CompileStatic
import javafx.scene.control.ListCell
import javafx.scene.layout.Pane
import ziphil.dictionary.Word
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class PlainWordCell extends ListCell<Word> {

  public PlainWordCell() {
    super()
  }

  protected void updateItem(Word word, Boolean empty) {
    super.updateItem(word, empty)
    if (empty || word == null) {
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