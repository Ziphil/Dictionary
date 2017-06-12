package ziphil.custom

import groovy.transform.CompileStatic
import javafx.geometry.Pos
import javafx.scene.control.ListCell
import javafx.scene.layout.Pane
import javafx.scene.layout.VBox
import ziphil.dictionary.SentenceSearcher
import ziphil.dictionary.Word
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class SentenceSearchResultCell extends ListCell<SentenceSearcher.Result> {

  public SentenceSearchResultCell() {
    super()
  }

  protected void updateItem(SentenceSearcher.Result result, Boolean empty) {
    super.updateItem(result, empty)
    VBox graphic = VBox.new(Measurement.rpx(3))
    graphic.prefWidthProperty().bind(getListView().fixedCellSizeProperty().subtract(Measurement.rpx(14)))
    if (!empty && result != null) {
      for (Word word : result.getWords()) {
        Pane pane = word.getPlainContentPaneFactory().create(true)
        graphic.getChildren().add(pane)
      }
    } 
    setAlignment(Pos.TOP_CENTER)
    setText(null)
    setGraphic(graphic)
  }

}