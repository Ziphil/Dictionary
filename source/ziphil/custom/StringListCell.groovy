package ziphil.custom

import groovy.transform.CompileStatic
import javafx.event.EventHandler
import javafx.scene.control.ContentDisplay
import javafx.scene.control.ListCell
import javafx.scene.input.MouseEvent
import javafx.scene.layout.Pane
import javafx.scene.layout.StackPane
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class StringListCell extends ListCell<String> {

  private static Double CLOSE_BUTTON_SIZE = 16

  private EventHandler<? super MouseEvent> $onCloseButtonClicked = null

  public StringListCell() {
    super()
  }

  protected void updateItem(String item, Boolean empty) {
    super.updateItem(item, empty)
    if (empty || item == null) {
      setText(null)
      setGraphic(null)
    } else {
      Pane graphic = StackPane.new()
      graphic.setPrefWidth(CLOSE_BUTTON_SIZE)
      graphic.setPrefHeight(CLOSE_BUTTON_SIZE)
      graphic.setOnMouseClicked($onCloseButtonClicked)
      graphic.getStyleClass().setAll("list-close-button")
      setText(item)
      setGraphic(graphic)
      setGraphicTextGap(0)
      setContentDisplay(ContentDisplay.TOP)
    }
  }

  public EventHandler<? super MouseEvent> getOnCloseButtonClicked() {
    return $onCloseButtonClicked
  }

  public void setOnCloseButtonClicked(EventHandler<? super MouseEvent> onCloseButtonClicked) {
    $onCloseButtonClicked = onCloseButtonClicked
  }

}