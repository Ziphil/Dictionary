package ziphil.dictionary

import groovy.transform.CompileStatic
import javafx.scene.Node
import javafx.scene.layout.Pane
import javafx.scene.text.Text
import javafx.scene.text.TextFlow
import ziphil.module.ClickType
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public abstract class PaneFactoryBase<E extends Element, D extends Dictionary> implements PaneFactory {

  protected E $word
  protected D $dictionary
  private Pane $pane = null 
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

  protected abstract Pane doCreate()

  public Pane create(Boolean forcesCreate) {
    if ($pane == null || $changed || forcesCreate) {
      Pane pane = doCreate()
      if ($persisted) {
        $pane = pane
      }
      $changed = false
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