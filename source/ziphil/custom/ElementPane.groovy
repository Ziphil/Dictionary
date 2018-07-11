package ziphil.custom

import groovy.transform.CompileStatic
import javafx.scene.Node
import javafx.scene.layout.Pane
import ziphil.dictionary.BadgeType
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class ElementPane {

  private Pane $pane
  private Map<BadgeType, Node> $badgeNodes = null

  public ElementPane(Pane pane, Map<BadgeType, Node> badgeNodes) {
    $pane = pane
    $badgeNodes = badgeNodes
  }

  public ElementPane(Pane pane) {
    $pane = pane
  }

  public Pane getPane() {
    return $pane
  }

  public Map<BadgeType, Node> getBadgeNodes() {
    return $badgeNodes
  }

}