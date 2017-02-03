package ziphil.dictionary

import groovy.transform.CompileStatic
import javafx.scene.Node
import javafx.scene.layout.Pane
import javafx.scene.text.Text
import javafx.scene.text.TextFlow
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public abstract class ContentPaneFactory<E extends Element, D extends Dictionary> {

  public static final String CONTENT_CLASS = "content"
  public static final String CONTENT_PANE_CLASS = "content-pane"
  public static final String HEAD_NAME_CLASS = "head-name"

  protected TextFlow $contentPane = TextFlow.new()
  protected E $word
  protected D $dictionary
  protected Boolean $isChanged = true
  protected Integer $lineSpacing = 0
  protected Boolean $modifiesPunctuation = false

  public ContentPaneFactory(E word, D dictionary) {
    $word = word
    $dictionary = dictionary
  }

  public Pane create() {
    if ($isChanged) {
      update()
      $isChanged = false
    }
    return $contentPane
  }

  protected abstract void update()

  public void change() {
    $isChanged = true
  }

  protected void modifyBreak() {
    Node lastChild = $contentPane.getChildren().last()
    if (lastChild instanceof Text && lastChild.getText() == "\n") {
      $contentPane.getChildren().removeAt($contentPane.getChildren().size() - 1)
    }
  }

  public void setLineSpacing(Integer lineSpacing) {
    $lineSpacing = lineSpacing
  }

  public void setModifiesPunctuation(Boolean modifiesPunctuation) {
    $modifiesPunctuation = modifiesPunctuation
  }

}