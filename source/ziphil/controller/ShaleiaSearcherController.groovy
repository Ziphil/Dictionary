package ziphil.controller

import groovy.transform.CompileStatic
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.control.ComboBox
import javafx.scene.control.TextField
import ziphil.custom.CustomBuilderFactory
import ziphil.custom.Measurement
import ziphil.custom.UtilityStage
import ziphil.dictionary.SearchType
import ziphil.dictionary.ShaleiaSearchParameter


@CompileStatic @Newify
public class ShaleiaSearcherController {

  private static final String RESOURCE_PATH = "resource/fxml/shaleia_searcher.fxml"
  private static final String TITLE = "高度な検索"
  private static final Double DEFAULT_WIDTH = Measurement.rpx(480)
  private static final Double DEFAULT_HEIGHT = -1

  @FXML private TextField $name
  @FXML private ComboBox $searchType
  private UtilityStage<ShaleiaSearchParameter> $stage
  private Scene $scene

  public ShaleiaSearcherController(UtilityStage<ShaleiaSearchParameter> stage) {
    $stage = stage
    loadResource()
  }

  @FXML
  private void commitSearch() {
    String name = $name.getText()
    String searchTypeValue = $searchType.getValue()
    SearchType searchType
    if (searchTypeValue == "完全一致") {
      searchType = SearchType.EXACT
    } else if (searchTypeValue == "前方一致") {
      searchType = SearchType.PREFIX
    } else if (searchTypeValue == "後方一致") {
      searchType = SearchType.SUFFIX
    } else if (searchTypeValue == "部分一致") {
      searchType = SearchType.PART
    } else if (searchTypeValue == "正規表現") {
      searchType = SearchType.REGULAR_EXPRESSION
    } else if (searchTypeValue == "最小対語") {
      searchType = SearchType.MINIMAL_PAIR
    }
    ShaleiaSearchParameter parameter = ShaleiaSearchParameter.new(name, searchType)
    $stage.close(parameter)
  }

  @FXML
  private void cancelSearch() {
    $stage.close(null)
  }

  private void loadResource() {
    FXMLLoader loader = FXMLLoader.new(getClass().getClassLoader().getResource(RESOURCE_PATH), null, CustomBuilderFactory.new())
    loader.setController(this)
    Parent root = (Parent)loader.load()
    $scene = Scene.new(root, DEFAULT_WIDTH, DEFAULT_HEIGHT)
    $stage.setScene($scene)
    $stage.setTitle(TITLE)
    $stage.sizeToScene()
  }

}