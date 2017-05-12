package ziphil.controller

import groovy.transform.CompileStatic
import javafx.application.Platform
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.fxml.FXML
import javafx.geometry.Side
import javafx.scene.Node
import javafx.scene.chart.PieChart
import ziphil.dictionary.Dictionary
import ziphil.dictionary.Word
import ziphil.custom.Measurement
import ziphil.custom.UtilityStage
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class CharacterFrequencyController extends Controller<Void> {

  private static final String RESOURCE_PATH = "resource/fxml/controller/character_frequency.fxml"
  private static final String TITLE = "文字頻度"
  private static final Double DEFAULT_WIDTH = -1
  private static final Double DEFAULT_HEIGHT = -1

  @FXML private PieChart $frequencyChart

  public CharacterFrequencyController(UtilityStage<Void> stage) {
    super(stage)
    loadResource(RESOURCE_PATH, TITLE, DEFAULT_WIDTH, DEFAULT_HEIGHT, false)
  }

  @FXML
  private void initialize() {
    setupFrequencyChart()
  }

  public void prepare(Dictionary dictionary) {
    List<PieChart.Data> data = ArrayList.new()
    for (Word word : dictionary.getRawWords()) {
      for (String character : word.getName()) {
        PieChart.Data singleData = data.find{singleData -> singleData.getName() == character}
        if (singleData != null) {
          singleData.setPieValue(singleData.getPieValue() + 1)
        } else {
          PieChart.Data newData = PieChart.Data.new(character, 1)
          data.add(newData)
        }
      }
    }
    data.sort() { PieChart.Data firstData, PieChart.Data secondData ->
      return secondData.getPieValue() <=> firstData.getPieValue()
    }
    List<PieChart.Data> displayedData = ArrayList.new()
    Integer otherFrequency = 0
    for (Integer i : 0 ..< data.size()) {
      PieChart.Data singleData = data[i]
      if (i < 20) {
        displayedData.add(singleData)
      } else {
        otherFrequency += singleData.getPieValue().toInteger()
      }
    }
    PieChart.Data otherData = null
    if (otherFrequency > 0) {
      otherData = PieChart.Data.new("その他", otherFrequency)
      displayedData.add(otherData)
    }
    $frequencyChart.setData(FXCollections.observableArrayList(displayedData))
    if (otherData != null) {
      otherData.getNode().getStyleClass().add("other")
      Platform.runLater() {
        Node otherLegendNode = $frequencyChart.lookup(".data20.pie-legend-symbol")
        otherLegendNode.getStyleClass().add("other-legend-symbol")
      }
    }
  }

  private void setupFrequencyChart() {
    $frequencyChart.setLegendSide(Side.RIGHT)
    $frequencyChart.setStartAngle(90)
  }

}