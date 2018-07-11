package ziphil.custom

import groovy.transform.CompileStatic
import javafx.scene.Node
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
      ElementPane pane = word.getPaneFactory().create(false)
      Pane graphic = pane.getPane()
      Map<BadgeType, Node> badgeNodes = pane.getBadgeNodes()
      graphic.prefWidthProperty().bind(getListView().widthProperty().subtract(Measurement.rpx(29)))
      if (word instanceof Word) {
        Int styleClassSize = getStyleClass().size()
        for (int i = 0 ; i < styleClassSize - 3 ; i ++) {
          getStyleClass().removeLast()
        }
        for (Map.Entry<BadgeType, Set<String>> entry : $individualSetting.getBadgedIdentifiers()) {
          BadgeType type = entry.getKey()
          Boolean contains = entry.getValue().contains(word.getIdentifier())
          if (badgeNodes != null) {
            badgeNodes[type].setVisible(contains)
            badgeNodes[type].setManaged(contains)
          }
          if (contains) {
            getStyleClass().add(type.getStyleClass())
          }
        }
      }
      setText(null)
      setGraphic(graphic)
    }
  }

}