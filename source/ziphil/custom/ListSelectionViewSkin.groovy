package ziphil.custom

import groovy.transform.CompileStatic
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.Node
import javafx.scene.control.Control
import javafx.scene.control.Label
import javafx.scene.control.ListView
import javafx.scene.control.SkinBase
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
  private ListSelectionView<T> $view

  public ListSelectionViewSkin(ListSelectionView<T> view) {
    super(view)
    $view = view
    loadResource()
    setupBasePane()
  }

  @FXML
  private void initialize() {
    bindProperties()
  }

  private void setupBasePane() {
    getChildren().add($basePane)
  }

  private void bindProperties() {
    $sourcesView.itemsProperty().bindBidirectional($view.sourcesProperty())
    $targetsView.itemsProperty().bindBidirectional($view.targetsProperty())
    $sourceNameControl.textProperty().bind($view.sourceNameProperty())
    $targetNameControl.textProperty().bind($view.targetNameProperty())
  }

  private void loadResource() {
    FXMLLoader loader = FXMLLoader.new(getClass().getClassLoader().getResource(RESOURCE_PATH), null, CustomBuilderFactory.new())
    loader.setRoot($basePane)
    loader.setController(this)
    loader.load()
  }

}