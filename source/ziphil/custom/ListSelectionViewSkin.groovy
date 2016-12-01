package ziphil.custom

import groovy.transform.CompileStatic
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.control.Control
import javafx.scene.control.SkinBase
import javafx.scene.layout.GridPane
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class ListSelectionViewSkin<T> extends SkinBase<ListSelectionView<T>> {

  private static final String RESOURCE_PATH = "resource/fxml/list_selection_view.fxml"

  @FXML private GridPane $baseBox

  public ListSelectionViewSkin(ListSelectionView<T> view) {
    super(view)
    loadResource()
  }

  @FXML
  private void initialize() {
  }

  private void loadResource() {
    FXMLLoader loader = FXMLLoader.new(getClass().getClassLoader().getResource(RESOURCE_PATH), null, CustomBuilderFactory.new())
    loader.setRoot($baseBox)
    loader.setController(this)
    loader.load()
  }

}