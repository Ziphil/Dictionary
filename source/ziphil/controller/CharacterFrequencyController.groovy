package ziphil.controller

import groovy.transform.CompileStatic
import javafx.fxml.FXML
import javafx.scene.chart.PieChart
import ziphil.Launcher
import ziphil.custom.UtilityStage
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class CharacterFrequencyController extends Controller<Void> {

  private static final String RESOURCE_PATH = "resource/fxml/controller/character_frequency.fxml"
  private static final String TITLE = "統計情報"
  private static final Double DEFAULT_WIDTH = -1
  private static final Double DEFAULT_HEIGHT = -1

  @FXML private PieChart $frequencyChart

  public CharacterFrequencyController(UtilityStage<Void> stage) {
    super(stage)
    loadResource(RESOURCE_PATH, TITLE, DEFAULT_WIDTH, DEFAULT_HEIGHT, false)
  }

  @FXML
  private void initialize() {
  }

}