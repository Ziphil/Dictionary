package ziphil.custom

import groovy.transform.CompileStatic
import javafx.collections.ObservableList
import javafx.fxml.FXML
import javafx.scene.control.ComboBox
import javafx.scene.control.ListCell
import javafx.scene.control.ListView
import javafx.scene.control.TextField
import javafx.scene.input.ClipboardContent
import javafx.scene.input.Dragboard
import javafx.scene.input.DragEvent
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.scene.input.MouseEvent
import javafx.scene.input.TransferMode
import javafx.scene.layout.HBox
import ziphilib.transform.VoidClosure
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class StringListEditorSkin extends CustomSkinBase<StringListEditor, HBox> {

  private static final String RESOURCE_PATH = "resource/fxml/custom/string_list_editor.fxml"

  @FXML private ListView<String> $listView
  @FXML private TextField $inputControl

  public StringListEditorSkin(StringListEditor control) {
    super(control)
    $node = HBox.new()
    loadResource(RESOURCE_PATH)
    setupNode()
  }

  @FXML
  private void initialize() {
    setupInputControl()
    setupDragAndDrop()
    bindProperties()
  }

  private void setupInputControl() {
    $inputControl.addEventHandler(KeyEvent.KEY_PRESSED) { KeyEvent event ->
      if (event.getCode() == KeyCode.SPACE) {
        $listView.getItems().add($inputControl.getText().trim())
        $inputControl.setText("")
      }
    }
    $inputControl.addEventHandler(KeyEvent.KEY_TYPED) { KeyEvent event ->
      if (event.getCharacter() == " ") {
        event.consume()
      }
    }
  }

  @VoidClosure
  private void setupDragAndDrop() {
    $listView.setCellFactory() { ListView<String> view ->
      ListCell<String> cell = StringListCell.new()
      cell.addEventHandler(MouseEvent.DRAG_DETECTED) { MouseEvent event ->
        String movedItem = cell.getItem()
        if (movedItem != null) {
          Dragboard dragboard = cell.startDragAndDrop(TransferMode.MOVE)
          ClipboardContent content = ClipboardContent.new()
          content.putString(movedItem)
          dragboard.setContent(content)
        }
        event.consume()
      }
      cell.addEventHandler(DragEvent.DRAG_OVER) { DragEvent event ->
        Dragboard dragboard = event.getDragboard()
        Object gestureSource = event.getGestureSource()
        if (gestureSource instanceof ListCell) {
          if (gestureSource.getListView() == $listView && gestureSource != cell && cell.getItem() != null && dragboard.hasString()) {
            event.acceptTransferModes(TransferMode.MOVE)
          }
        }
        event.consume()
      }
      cell.addEventHandler(DragEvent.DRAG_DROPPED) { DragEvent event ->
        Boolean completed = false
        Dragboard dragboard = event.getDragboard()
        if (dragboard.hasString()) {
          ObservableList<String> items = $listView.getItems()
          String movedItem = dragboard.getString()
          Int movedIndex = items.findIndexOf{it == movedItem}
          Int index = cell.getIndex()
          if (index < movedIndex) {
            items.add(index, movedItem)
            items.removeAt(movedIndex + 1)
            $listView.getSelectionModel().select(index)
          } else {
            items.add(index + 1, movedItem)
            items.removeAt(movedIndex)
            $listView.getSelectionModel().select(index)
          }
          completed = true
        }
        event.setDropCompleted(completed)
        event.consume()
      }
      cell.setOnCloseButtonClicked() { MouseEvent event ->
        $listView.getItems().remove(cell.getItem())
      }
      return cell
    }
  }

  private void bindProperties() {
    $listView.itemsProperty().bindBidirectional($control.stringsProperty())
  }

}