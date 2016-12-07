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

  @FXML private ListView<T> $sourcesView
  @FXML private ListView<T> $targetsView
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
    List<T> movedItems = ArrayList.new($sourcesView.getSelectionModel().getSelectedItems())
    for (T movedItem : movedItems) {
      $sourcesView.getItems().remove(movedItem)
      $targetsView.getItems().add(movedItem)
    }
    Integer size = $targetsView.getItems().size()
    Integer movedSize = movedItems.size()
    $sourcesView.getSelectionModel().clearSelection()
    $targetsView.getSelectionModel().selectRange(size - movedSize, size)
    $targetsView.requestFocus()
  }

  @FXML
  private void moveToSource() {
    List<T> movedItems = ArrayList.new($targetsView.getSelectionModel().getSelectedItems())
    for (T movedItem : movedItems) {
      $targetsView.getItems().remove(movedItem)
      $sourcesView.getItems().add(movedItem)
    }
    Integer size = $sourcesView.getItems().size()
    Integer movedSize = movedItems.size()
    $targetsView.getSelectionModel().clearSelection()
    $sourcesView.getSelectionModel().selectRange(size - movedSize, size)
    $sourcesView.requestFocus()
  }

  private void setupViews() {
    $sourcesView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE)
    $targetsView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE)
  }

  private void setupDragAndDrop() {
    setupUnidirectionalDragAndDrop($sourcesView, $targetsView)
    setupUnidirectionalDragAndDrop($targetsView, $sourcesView)
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
      Boolean isCompleted = false
      Dragboard dragboard = event.getDragboard()
      if (dragboard.hasString()) {
        String movedString = dragboard.getString()
        T movedItem = firstView.getItems().find{item -> item.toString() == movedString}
        firstView.getItems().remove(movedItem)
        secondView.getItems().add(movedItem)
        firstView.getSelectionModel().clearSelection()
        secondView.getSelectionModel().select(movedItem)
        secondView.requestFocus()
        isCompleted = true
      }
      event.setDropCompleted(isCompleted)
      event.consume()
    }
  }

  private void bindProperties() {
    $sourcesView.itemsProperty().bindBidirectional(((ListSelectionView<T>)$control).sourcesProperty())
    $targetsView.itemsProperty().bindBidirectional(((ListSelectionView<T>)$control).targetsProperty())
    $sourceNameControl.textProperty().bind(((ListSelectionView<T>)$control).sourceNameProperty())
    $targetNameControl.textProperty().bind(((ListSelectionView<T>)$control).targetNameProperty())
  }

}