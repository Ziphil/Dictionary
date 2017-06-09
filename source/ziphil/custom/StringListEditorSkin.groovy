package ziphil.custom

import groovy.transform.CompileStatic
import javafx.collections.ObservableList
import javafx.fxml.FXML
import javafx.scene.control.ListView
import javafx.scene.control.TextField
import javafx.scene.layout.VBox
import ziphilib.transform.InnerClass
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class StringListEditorSkin extends CustomSkinBase<StringListEditor, VBox> {

  private static final String RESOURCE_PATH = "resource/fxml/custom/string_list_editor.fxml"

  @FXML private ListView<String> $listView
  @FXML private TextField $inputControl

  public StringListEditorSkin(StringListEditor control) {
    super(control)
    $node = VBox.new()
    loadResource(RESOURCE_PATH)
    setupNode()
  }

  @FXML
  private void initialize() {
    bindProperties()
  }

  @FXML
  private void add() {
    String input = $inputControl.getText()
    $listView.getItems().add(input)
  }

  @FXML
  private void addSeparate() {
    String input = $inputControl.getText()
    for (String character : input) {
      $listView.getItems().add(character)
    }
  }

  private void bindProperties() {
    $listView.itemsProperty().bindBidirectional($control.stringsProperty())
  }

}