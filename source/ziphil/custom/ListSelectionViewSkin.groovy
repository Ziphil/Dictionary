package ziphil.custom

import groovy.transform.CompileStatic
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
public class ListSelectionViewSkin<T> extends CustomSkinBase<ListSelectionView<T>, GridPane> {

  private static final String RESOURCE_PATH = "resource/fxml/custom/list_selection_view.fxml"

  @FXML private ListView<T> $sourceView
  @FXML private ListView<T> $targetView
  @FXML private Label $sourceNameControl
  @FXML private Label $targetNameControl

  public ListSelectionViewSkin(ListSelectionView<T> control) {
    super(control)
    $node = GridPane.new()
    loadResource(RESOURCE_PATH)
    setupNode()
  }

  @FXML
  private void initialize() {
    setupViews()
    setupDragAndDrop()
    bindProperties()
  }

  @FXML
  private void moveToTarget() {
    List<T> movedItems = ArrayList.new($sourceView.getSelectionModel().getSelectedItems())
    if (!movedItems.isEmpty()) {
      for (T movedItem : movedItems) {
        $sourceView.getItems().remove(movedItem)
        $targetView.getItems().add(movedItem)
      }
      Int size = $targetView.getItems().size()
      Int movedSize = movedItems.size()
      $sourceView.getSelectionModel().clearSelection()
      $targetView.getSelectionModel().clearSelection()
      $targetView.getSelectionModel().selectRange(size - movedSize, size)
      if ($sourceView.isFocused()) {
        $targetView.requestFocus()
      }
    }
  }

  @FXML
  private void moveToSource() {
    List<T> movedItems = ArrayList.new($targetView.getSelectionModel().getSelectedItems())
    if (!movedItems.isEmpty()) {
      for (T movedItem : movedItems) {
        $targetView.getItems().remove(movedItem)
        $sourceView.getItems().add(movedItem)
      }
      Int size = $sourceView.getItems().size()
      Int movedSize = movedItems.size()
      $targetView.getSelectionModel().clearSelection()
      $sourceView.getSelectionModel().clearSelection()
      $sourceView.getSelectionModel().selectRange(size - movedSize, size)
      if ($targetView.isFocused()) {
        $sourceView.requestFocus()
      }
    }
  }

  private void setupViews() {
    $sourceView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE)
    $targetView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE)
  }

  private void setupDragAndDrop() {
    setupUnidirectionalDragAndDrop($sourceView, $targetView)
    setupUnidirectionalDragAndDrop($targetView, $sourceView)
  }

  private void setupUnidirectionalDragAndDrop(ListView<T> firstView, ListView<T> secondView) {
    firstView.setCellFactory() { ListView<T> view ->
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
      return cell
    }
    secondView.addEventHandler(DragEvent.DRAG_OVER) { DragEvent event ->
      Dragboard dragboard = event.getDragboard()
      Object gestureSource = event.getGestureSource()
      if (gestureSource instanceof ListCell) {
        if (gestureSource.getListView() == firstView && dragboard.hasString()) {
          event.acceptTransferModes(TransferMode.MOVE)
        }
      }
      event.consume()
    }
    secondView.addEventHandler(DragEvent.DRAG_DROPPED) { DragEvent event ->
      Boolean completed = false
      Dragboard dragboard = event.getDragboard()
      if (dragboard.hasString()) {
        String movedString = dragboard.getString()
        T movedItem = firstView.getItems().find{it.toString() == movedString}
        firstView.getItems().remove(movedItem)
        secondView.getItems().add(movedItem)
        firstView.getSelectionModel().clearSelection()
        secondView.getSelectionModel().clearSelection()
        secondView.getSelectionModel().select(movedItem)
        secondView.requestFocus()
        completed = true
      }
      event.setDropCompleted(completed)
      event.consume()
    }
  }

  private void bindProperties() {
    $sourceView.itemsProperty().bindBidirectional(((ListSelectionView<T>)$control).sourcesProperty())
    $targetView.itemsProperty().bindBidirectional(((ListSelectionView<T>)$control).targetsProperty())
    $sourceNameControl.textProperty().bind(((ListSelectionView<T>)$control).sourceNameProperty())
    $targetNameControl.textProperty().bind(((ListSelectionView<T>)$control).targetNameProperty())
  }

}