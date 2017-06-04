package ziphil.dictionary

import groovy.transform.CompileStatic
import javafx.scene.Node
import javafx.scene.layout.Pane
import javafx.scene.text.Text
import javafx.scene.text.TextFlow
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public abstract class ContentPaneFactoryBase<E extends Element, D extends Dictionary> implements ContentPaneFactory {

  protected E $word
  protected D $dictionary
  protected Pane $contentPane = null 
  protected Boolean $changed = true
  protected Boolean $persisted = false

  public ContentPaneFactoryBase(E word, D dictionary, Boolean persisted) {
    $word = word
    $dictionary = dictionary
    $persisted = persisted
  }

  public ContentPaneFactoryBase(E word, D dictionary) {
    this(word, dictionary, false)
  }

  protected abstract Pane doCreate()

  public Pane create(Boolean forcesCreate) {
    if ($contentPane == null || $changed || forcesCreate) {
      Pane contentPane = doCreate()
      if ($persisted) {
        $contentPane = contentPane
      }
      $changed = false
      return contentPane
    } else {
      return $contentPane
    }
  }

  public void destroy() {
    $contentPane = null
  }

  public void change() {
    $changed = true
  }

  protected void modifyBreak(TextFlow contentPane) {
    if (contentPane.getChildren().size() >= 1) {
      Node lastChild = contentPane.getChildren().last()
      if (lastChild instanceof Text && lastChild.getText() == "\n") {
        contentPane.getChildren().removeAt(contentPane.getChildren().size() - 1)
      }
    }
  }

  public Boolean isPersisted() {
    return $persisted
  }

  public void setPersisted(Boolean persisted) {
    $persisted = persisted
    if (!persisted) {
      $contentPane = null
    }
  }

}