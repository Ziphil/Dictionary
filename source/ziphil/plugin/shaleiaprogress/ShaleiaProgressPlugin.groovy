package ziphil.plugin.shaleiaprogress

import groovy.transform.CompileStatic
import java.util.regex.Matcher
import javafx.stage.StageStyle
import javafx.stage.Modality
import javafx.scene.chart.NumberAxis
import javafx.scene.chart.XYChart
import javafx.scene.image.Image
import javafx.scene.input.KeyCode
import ziphil.controller.ProgressController
import ziphil.custom.UtilityStage
import ziphil.dictionary.Dictionary
import ziphil.dictionary.shaleia.ShaleiaDictionary
import ziphil.dictionary.shaleia.ShaleiaWord
import ziphil.plugin.Plugin
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class ShaleiaProgressPlugin implements Plugin {

  private static final String NAME = "単語数グラフ"

  public void call(Dictionary dictionary) {
    if (dictionary instanceof ShaleiaDictionary) {
      Map<IntegerClass, IntegerClass> wordSizes = HashMap.new()
      XYChart.Series series = XYChart.Series.new()
      Int maxDate = 0
      Int accumulatedWordSize = 0
      for (ShaleiaWord word : dictionary.getRawWords()) {
        if (!word.getUniqueName().startsWith("\$")) {
          Matcher matcher = word.getContent() =~ /(?m)^\+\s*(\d+)/
          if (matcher.find()) {
            Int date = IntegerClass.parseInt(matcher.group(1))
            if (wordSizes.containsKey(date)) {
              wordSizes[date] = wordSizes[date] + 1
            } else {
              wordSizes[date] = 1
            }
            if (date > maxDate) {
              maxDate = date
            }
          }
        }
      }
      for (Int date = 1 ; date <= maxDate ; date ++) {
        if (wordSizes.containsKey(date)) {
          accumulatedWordSize += wordSizes[date]
        }
        series.getData().add(XYChart.Data.new(date, accumulatedWordSize))
      }
      NumberAxis xAxis = NumberAxis.new(1000, maxDate, 100)
      NumberAxis yAxis = NumberAxis.new(0, accumulatedWordSize, 200)
      UtilityStage<Void> nextStage = UtilityStage.new(StageStyle.UTILITY)
      ProgressController controller = ProgressController.new(nextStage)
      nextStage.initModality(Modality.APPLICATION_MODAL)
      nextStage.initOwner(null)
      controller.prepare(xAxis, yAxis, series)
      nextStage.showAndWait()
    }
  }

  public Boolean isSupported(Dictionary dictionary) {
    if (dictionary instanceof ShaleiaDictionary) {
      if (dictionary.getVersion() == "5.5") {
        return true
      } else {
        return false
      }
    } else {
      return false
    }
  }

  public String getName() {
    return NAME
  }

  public KeyCode getKeyCode() {
    return null
  }

  public Image getIcon() {
    return null
  }

}