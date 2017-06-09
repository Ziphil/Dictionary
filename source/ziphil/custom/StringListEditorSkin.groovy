package ziphil.custom

import groovy.transform.CompileStatic
import javafx.collections.ObservableList
import javafx.fxml.FXML
import javafx.scene.control.ComboBox
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
  @FXML private ComboBox $additionModeControl

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
  private void addInput() {
    String input = $inputControl.getText()
    AdditionMode additionMode = $additionModeControl.getValue()
    if (additionMode == AdditionMode.NORMAL) {
      $listView.getItems().add(input)
    } else if (additionMode == AdditionMode.SPLIT_SINGLE) {
      for (String character : input) {
        $listView.getItems().add(character)
      }
    } else if (additionMode == AdditionMode.SPLIT_COMMA) {
      List<String> splitInput = input.split(/\s*(,|、)\s*/).toList()
      $listView.getItems().addAll(splitInput)
    }
  }

  private void bindProperties() {
    $listView.itemsProperty().bindBidirectional($control.stringsProperty())
  }

}