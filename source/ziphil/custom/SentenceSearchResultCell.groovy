package ziphil.custom

import groovy.transform.CompileStatic
import javafx.scene.control.ListCell
import javafx.scene.layout.Pane
import javafx.scene.layout.VBox
import ziphil.dictionary.Word
import ziphilib.transform.Ziphilify
import ziphilib.type.PrimBoolean


@CompileStatic @Ziphilify
public class SentenceSearchResultCell extends ListCell<List<Word>> {

  public SentenceSearchResultCell() {
    super()
  }

  protected void updateItem(List<Word> words, PrimBoolean empty) {
    super.updateItem(words, empty)
    if (empty || words == null) {
      setText(null)
      setGraphic(null)
    } else {
      VBox graphic = VBox.new(Measurement.rpx(3))
      for (Word word : words) {
        Pane pane = word.getPlainContentPaneFactory().create()
        pane.prefWidthProperty().bind(getListView().fixedCellSizeProperty().subtract(Measurement.rpx(14)))
        graphic.getChildren().add(pane)
      }
      setText(null)
      setGraphic(graphic)
    }
  }

}