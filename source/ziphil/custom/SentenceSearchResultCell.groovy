package ziphil.custom

import groovy.transform.CompileStatic
import javafx.geometry.Pos
import javafx.scene.control.ListCell
import javafx.scene.control.Separator
import javafx.scene.layout.Pane
import javafx.scene.layout.VBox
import javafx.scene.text.Text
import ziphil.dictionary.PaneFactory
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
    VBox graphic = VBox.new()
    graphic.prefWidthProperty().bind(getListView().fixedCellSizeProperty().subtract(Measurement.rpx(14)))
    if (!empty && result != null) {
      Text nameText = Text.new(result.getName())
      Separator separator = Separator.new()
      nameText.getStyleClass().add(PaneFactory.CONTENT_CLASS)
      separator.getStyleClass().addAll(PaneFactory.CONTENT_CLASS, PaneFactory.SEPARATOR_CLASS)
      graphic.getChildren().addAll(nameText, separator)
      for (Word word : result.getWords()) {
        Pane pane = word.getPlainPaneFactory().create(true)
        graphic.getChildren().add(pane)
      }
      if (result.getWords().isEmpty()) {
        Text emptyText = Text.new("該当なし")
        emptyText.getStyleClass().add(PaneFactory.CONTENT_CLASS)
        graphic.getChildren().add(emptyText)
      }
    } 
    setAlignment(Pos.TOP_CENTER)
    setText(null)
    setGraphic(graphic)
  }

}