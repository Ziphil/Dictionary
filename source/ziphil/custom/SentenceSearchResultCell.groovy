package ziphil.custom

import groovy.transform.CompileStatic
import javafx.geometry.Pos
import javafx.scene.control.ListCell
import javafx.scene.layout.Pane
import ziphil.dictionary.SentenceSearcher
import ziphil.dictionary.SentenceSearchResultPaneFactory
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class SentenceSearchResultCell extends ListCell<SentenceSearcher.Result> {

  public SentenceSearchResultCell() {
    super()
  }

  protected void updateItem(SentenceSearcher.Result result, Boolean empty) {
    super.updateItem(result, empty)
    if (empty || result == null) {
      setText(null)
      setGraphic(null)
    } else {
      Pane graphic = SentenceSearchResultPaneFactory.new(result).create(true)
      graphic.prefWidthProperty().bind(getListView().fixedCellSizeProperty().subtract(Measurement.rpx(14)))
      setAlignment(Pos.TOP_CENTER)
      setText(null)
      setGraphic(graphic)
    }
  }

}