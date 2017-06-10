package ziphil.custom

import groovy.transform.CompileStatic
import javafx.collections.ObservableList
import javafx.fxml.FXML
import javafx.scene.control.Label
import javafx.scene.layout.HBox
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class HelpIndicatorSkin extends CustomSkinBase<HelpIndicator, HBox> {

  private static final String RESOURCE_PATH = "resource/fxml/custom/help_indicator.fxml"

  public HelpIndicatorSkin(HelpIndicator control) {
    super(control)
    $node = HBox.new()
    loadResource(RESOURCE_PATH)
    setupNode()
  }

  @FXML
  private void initialize() {
  }

}