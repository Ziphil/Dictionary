package ziphil.custom

import groovy.transform.CompileStatic
import javafx.collections.ObservableList
import javafx.fxml.FXML
import javafx.scene.Node
import javafx.scene.control.Control
import javafx.scene.control.Label
import javafx.scene.control.ListCell
import javafx.scene.control.ListView
import javafx.scene.control.SelectionMode
import javafx.scene.input.ClipboardContent
import javafx.scene.input.Dragboard
import javafx.scene.input.DragEvent
import javafx.scene.input.MouseEvent
import javafx.scene.input.TransferMode
import javafx.scene.layout.GridPane
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class PermutableListViewSkin<T> extends CustomSkinBase<PermutableListView<T>, GridPane> {

  private static final String RESOURCE_PATH = "resource/fxml/custom/permutable_list_view.fxml"

  @FXML private ListView<T> $itemView

  public PermutableListViewSkin(PermutableListView<T> control) {
    super(control)
    $node = GridPane.new()
    loadResource(RESOURCE_PATH)
    setupNode()
  }

  @FXML
  private void initialize() {
    setupDragAndDrop()
    bindProperties()
  }

  @FXML
  private void exchangeUp() {
    Integer index = $itemView.getSelectionModel().getSelectedIndex()
    if (index > 0) {
      $itemView.getItems().swap(index, index - 1)
      $itemView.getSelectionModel().select(index - 1)
    }
  }

  @FXML
  private void exchangeDown() {
    Integer index = $itemView.getSelectionModel().getSelectedIndex()
    if (index >= 0 && index < $itemView.getItems().size() - 1) {
      $itemView.getItems().swap(index, index + 1)
      $itemView.getSelectionModel().select(index + 1)
    }
  }

  private void setupDragAndDrop() {
    $itemView.setCellFactory() { ListView<T> view ->
      ListCell<T> cell = SimpleListCell.new()
      cell.addEventHandler(MouseEvent.DRAG_DETECTED) { MouseEvent event ->
        T movedItem = cell.getItem()
        if (movedItem != null) {
          String movedString = movedItem.toString()
          Dragboard dragboard = cell.startDragAndDrop(TransferMode.MOVE)
          ClipboardContent content = ClipboardContent.new()
          content.putString(movedString)
          dragboard.setContent(content)
        }
        event.consume()
      }
      cell.addEventHandler(DragEvent.DRAG_OVER) { DragEvent event ->
        Dragboard dragboard = event.getDragboard()
        Object gestureSource = event.getGestureSource()
        if (gestureSource instanceof ListCell) {
          if (gestureSource.getListView() == $itemView && gestureSource != cell && cell.getItem() != null && dragboard.hasString()) {
            event.acceptTransferModes(TransferMode.MOVE)
          }
        }
        event.consume()
      }
      cell.addEventHandler(DragEvent.DRAG_DROPPED) { DragEvent event ->
        Boolean isCompleted = false
        Dragboard dragboard = event.getDragboard()
        if (dragboard.hasString()) {
          ObservableList<T> items = $itemView.getItems()
          String movedString = dragboard.getString()
          T movedItem = items.find{item -> item.toString() == movedString}
          Integer movedIndex = items.findIndexOf{item -> item.toString() == movedString}
          Integer index = cell.getIndex()
          items.add(index + 1, movedItem)
          if (movedIndex < index + 1) {
            items.removeAt(movedIndex)
            $itemView.getSelectionModel().select(index)
          } else {
            items.removeAt(movedIndex + 1)
            $itemView.getSelectionModel().select(index + 1)
          }
          isCompleted = true
        }
        event.setDropCompleted(isCompleted)
        event.consume()
      }
      return cell
    }
  }

  private void bindProperties() {
    $itemView.itemsProperty().bindBidirectional(((PermutableListView<T>)$control).itemsProperty())
  }

}