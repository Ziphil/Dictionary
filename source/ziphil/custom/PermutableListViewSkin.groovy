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
public class PermutableListViewSkin<T> extends CustomSkinBase<PermutableListView<T>, GridPane> {

  private static final String RESOURCE_PATH = "resource/fxml/custom/permutable_list_view.fxml"

  @FXML private ListView<T> $itemsView

  public PermutableListViewSkin(PermutableListView<T> control) {
    super(control)
    $node = GridPane.new()
    loadResource(RESOURCE_PATH)
    setupNode()
  }

  @FXML
  private void initialize() {
    bindProperties()
  }

  @FXML
  private void exchangeUp() {
    Integer index = $itemsView.getSelectionModel().getSelectedIndex()
    if (index > 0) {
      $itemsView.getItems().swap(index, index - 1)
      $itemsView.getSelectionModel().select(index - 1)
    }
  }

  @FXML
  private void exchangeDown() {
    Integer index = $itemsView.getSelectionModel().getSelectedIndex()
    if (index < $itemsView.getItems().size() - 1) {
      $itemsView.getItems().swap(index, index + 1)
      $itemsView.getSelectionModel().select(index + 1)
    }
  }

  private void bindProperties() {
    $itemsView.itemsProperty().bindBidirectional(((PermutableListView<T>)$control).itemsProperty())
  }

}