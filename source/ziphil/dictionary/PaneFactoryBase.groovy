package ziphil.dictionary

import groovy.transform.CompileStatic
import javafx.beans.value.ObservableValue
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.image.ImageView
import javafx.scene.layout.HBox
import javafx.scene.layout.Pane
import javafx.scene.text.Text
import javafx.scene.text.TextFlow
import ziphil.custom.ClickType
import ziphil.custom.Measurement
import ziphil.dictionary.BadgeType
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public abstract class PaneFactoryBase<E extends Element, D extends Dictionary, P> implements PaneFactory<P> {

  private static final String BADGE_CONTAINER_CLASS = "badge-container"

  protected E $word
  protected D $dictionary
  private P $pane = null 
  protected ClickType $linkClickType = null
  private Boolean $changed = true
  private Boolean $persisted = false

  public PaneFactoryBase(E word, D dictionary, Boolean persisted) {
    $word = word
    $dictionary = dictionary
    $persisted = persisted
  }

  public PaneFactoryBase(E word, D dictionary) {
    this(word, dictionary, false)
  }

  protected abstract P doCreate()

  public P create(Boolean forcesCreate) {
    if ($pane == null || $changed || forcesCreate) {
      P pane = doCreate()
      if ($persisted && !forcesCreate) {
        $pane = pane
        $changed = false
      }
      return pane
    } else {
      return $pane
    }
  }

  public void destroy() {
    $pane = null
  }

  public void change() {
    $changed = true
  }

  protected void addBadgeNodes(Pane pane, Map<BadgeType, Node> badgeNodes) {
    HBox box = HBox.new(Measurement.rpx(2))
    Text text = Text.new(" ")
    for (BadgeType type : BadgeType.values()) {
      ImageView view = ImageView.new(type.getImage())       
      view.getStyleClass().add(type.getStyleClass())
      box.getChildren().add(view)
      badgeNodes[type] = view
    }
    box.setAlignment(Pos.BASELINE_CENTER)
    box.getStyleClass().add(BADGE_CONTAINER_CLASS)
    pane.getChildren().addAll(box, text)
    for (Map.Entry<BadgeType, Node> entry : badgeNodes) {
      entry.getValue().managedProperty().addListener() { ObservableValue<? extends BooleanClass> observableValue, BooleanClass oldValue, BooleanClass newValue ->
        Boolean anyManaged = false
        for (Map.Entry<BadgeType, Node> otherEntry : badgeNodes) {
          if (otherEntry.getValue().isManaged()) {
            anyManaged = true
            break
          }
        }
        text.setVisible(anyManaged)
        text.setManaged(anyManaged)
      }
    }
  }

  protected void modifyBreak(TextFlow pane) {
    if (pane.getChildren().size() >= 1) {
      Node lastChild = pane.getChildren().last()
      if (lastChild instanceof Text && lastChild.getText() == "\n") {
        pane.getChildren().removeAt(pane.getChildren().size() - 1)
      }
    }
  }

  public void setLinkClickType(ClickType linkClickType) {
    $linkClickType = linkClickType
  }

  public void setPersisted(Boolean persisted) {
    $persisted = persisted
    if (!persisted) {
      $pane = null
    }
  }

}