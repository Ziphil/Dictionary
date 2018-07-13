package ziphil.custom

import groovy.transform.CompileStatic
import javafx.scene.Node
import javafx.scene.control.ListCell
import javafx.scene.layout.Pane
import ziphil.dictionary.Badge
import ziphil.dictionary.Element
import ziphil.dictionary.IndividualSetting
import ziphil.dictionary.Word
import ziphil.module.Setting
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class ElementCell extends ListCell<Element> {

  private IndividualSetting $individualSetting

  public ElementCell(IndividualSetting individualSetting) {
    super()
    $individualSetting = individualSetting
  }

  protected void updateItem(Element word, Boolean empty) {
    super.updateItem(word, empty)
    Int styleClassSize = getStyleClass().size()
    for (int i = 0 ; i < styleClassSize - 3 ; i ++) {
      getStyleClass().removeLast()
    }
    if (empty || word == null) {
      setText(null)
      setGraphic(null)
    } else {
      ElementPane pane = word.getPaneFactory().create(false)
      Pane graphic = pane.getPane()
      Map<Badge, Node> badgeNodes = pane.getBadgeNodes()
      graphic.prefWidthProperty().bind(getListView().widthProperty().subtract(Measurement.rpx(29)))
      if (word instanceof Word) {
        Boolean colorsBadgedWord = Setting.getInstance().getColorsBadgedWord()
        Map<Badge, Set<String>> badgedIdentifiers = $individualSetting.getBadgedIdentifiers()
        for (Badge badge : Badge.values()) {
          Boolean contains = (badgedIdentifiers[badge] != null) ? badgedIdentifiers[badge].contains(word.getIdentifier()) : false
          if (badgeNodes != null) {
            badgeNodes[badge].setVisible(contains)
            badgeNodes[badge].setManaged(contains)
          }
          if (colorsBadgedWord && contains) {
            getStyleClass().add(badge.getStyleClass())
          }
        }
      }
      setText(null)
      setGraphic(graphic)
    }
  }

}