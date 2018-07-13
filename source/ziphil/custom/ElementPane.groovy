package ziphil.custom

import groovy.transform.CompileStatic
import javafx.scene.Node
import javafx.scene.layout.Pane
import ziphil.dictionary.Badge
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class ElementPane {

  private Pane $pane
  private Map<Badge, Node> $badgeNodes = null

  public ElementPane(Pane pane, Map<Badge, Node> badgeNodes) {
    $pane = pane
    $badgeNodes = badgeNodes
  }

  public ElementPane(Pane pane) {
    $pane = pane
  }

  public Pane getPane() {
    return $pane
  }

  public Map<Badge, Node> getBadgeNodes() {
    return $badgeNodes
  }

}