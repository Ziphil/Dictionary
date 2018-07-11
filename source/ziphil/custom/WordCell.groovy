package ziphil.custom

import groovy.transform.CompileStatic
import javafx.scene.control.ListCell
import javafx.scene.layout.Pane
import ziphil.dictionary.BadgeType
import ziphil.dictionary.Element
import ziphil.dictionary.IndividualSetting
import ziphil.dictionary.Word
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class WordCell extends ListCell<Element> {

  private IndividualSetting $individualSetting

  public WordCell(IndividualSetting individualSetting) {
    super()
    $individualSetting = individualSetting
  }

  protected void updateItem(Element word, Boolean empty) {
    super.updateItem(word, empty)
    if (empty || word == null) {
      setText(null)
      setGraphic(null)
    } else {
      Pane graphic = word.getPaneFactory().create(false)
      graphic.prefWidthProperty().bind(getListView().widthProperty().subtract(Measurement.rpx(29)))
      if (word instanceof Word) {
        Int styleClassSize = getStyleClass().size()
        for (int i = 0 ; i < styleClassSize - 3 ; i ++) {
          getStyleClass().removeLast()
        }
        for (Map.Entry<BadgeType, Set<String>> entry : $individualSetting.getBadgedIdentifiers()) {
          BadgeType type = entry.getKey()
          if (entry.getValue().contains(word.getIdentifier())) {
            getStyleClass().add(type.getStyleClass())
          }
        }
      }
      setText(null)
      setGraphic(graphic)
    }
  }

}