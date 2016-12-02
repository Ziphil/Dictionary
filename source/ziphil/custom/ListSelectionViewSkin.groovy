package ziphil.custom

import groovy.transform.CompileStatic
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.Node
import javafx.scene.control.Control
import javafx.scene.control.Label
import javafx.scene.control.ListView
import javafx.scene.control.SelectionMode
import javafx.scene.control.SkinBase
import javafx.scene.input.ClipboardContent
import javafx.scene.input.Dragboard
import javafx.scene.input.DragEvent
import javafx.scene.input.MouseEvent
import javafx.scene.input.TransferMode
import javafx.scene.layout.GridPane
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class ListSelectionViewSkin<T> extends SkinBase<ListSelectionView<T>> {

  private static final String RESOURCE_PATH = "resource/fxml/list_selection_view.fxml"

  @FXML private GridPane $basePane = GridPane.new()
  @FXML private ListView<T> $sourcesView
  @FXML private ListView<T> $targetsView
  @FXML private Label $sourceNameControl
  @FXML private Label $targetNameControl
  private ListSelectionView<T> $control

  public ListSelectionViewSkin(ListSelectionView<T> control) {
    super(control)
    $control = control
    loadResource()
    setupBasePane()
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
    $sourcesView.getSelectionModel().clearSelection()
  }

  @FXML
  private void moveToSource() {
    List<T> movedItems = ArrayList.new($targetsView.getSelectionModel().getSelectedItems())
    for (T movedItem : movedItems) {
      $targetsView.getItems().remove(movedItem)
      $sourcesView.getItems().add(movedItem)
    }
    $targetsView.getSelectionModel().clearSelection()
  }

  private void setupBasePane() {
    getChildren().add($basePane)
  }

  private void setupViews() {
    $sourcesView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE)
    $targetsView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE)
  }

  private void setupDragAndDrop() {
    setupSingleDragAndDrop($sourcesView, $targetsView)
    setupSingleDragAndDrop($targetsView, $sourcesView)
  }

  private void setupSingleDragAndDrop(ListView<?> firstView, ListView<?> secondView) {
    firstView.addEventHandler(MouseEvent.DRAG_DETECTED) { MouseEvent event ->
      String movedString = firstView.getSelectionModel().getSelectedItem().toString()
      Dragboard dragboard = firstView.startDragAndDrop(TransferMode.MOVE)
      ClipboardContent content = ClipboardContent.new()
      content.putString(movedString)
      dragboard.setContent(content)
      event.consume()
    }
    secondView.addEventHandler(DragEvent.DRAG_OVER) { DragEvent event ->
      Dragboard dragboard = event.getDragboard()
      if (event.getGestureSource() == firstView && dragboard.hasString()) {
        event.acceptTransferModes(TransferMode.MOVE)
      }
      event.consume()
    }
    secondView.addEventHandler(DragEvent.DRAG_DROPPED) { DragEvent event ->
      Boolean isCompleted = false
      Dragboard dragboard = event.getDragboard()
      if (dragboard.hasString()) {
        String movedString = dragboard.getString()
        T movedItem = firstView.getItems().find{item -> item.toString() == movedString}
        secondView.getItems().add(movedItem)
        isCompleted = true
      }
      event.setDropCompleted(isCompleted)
      event.consume()
    }
    firstView.addEventHandler(DragEvent.DRAG_DONE) { DragEvent event ->
      Dragboard dragboard = event.getDragboard()
      if (event.getTransferMode() == TransferMode.MOVE && dragboard.hasString()) {
        String movedString = dragboard.getString()
        T movedItem = firstView.getItems().find{item -> item.toString() == movedString}
        firstView.getItems().remove(movedItem)
      }
      event.consume()
    }
  }

  private void bindProperties() {
    $sourcesView.itemsProperty().bindBidirectional($control.sourcesProperty())
    $targetsView.itemsProperty().bindBidirectional($control.targetsProperty())
    $sourceNameControl.textProperty().bind($control.sourceNameProperty())
    $targetNameControl.textProperty().bind($control.targetNameProperty())
  }

  private void loadResource() {
    FXMLLoader loader = FXMLLoader.new(getClass().getClassLoader().getResource(RESOURCE_PATH), null, CustomBuilderFactory.new())
    loader.setRoot($basePane)
    loader.setController(this)
    loader.load()
  }

}