package ziphil.dictionary

import groovy.transform.CompileStatic
import javafx.scene.Node
import javafx.scene.text.Text
import javafx.scene.text.TextFlow
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public abstract class ContentPaneCreator<W extends Word, D extends Dictionary> {

  public static final String CONTENT_CLASS = "content"
  public static final String CONTENT_PANE_CLASS = "content-pane"
  public static final String HEAD_NAME_CLASS = "head-name"

  protected TextFlow $contentPane
  protected W $word
  protected D $dictionary
  protected Integer $lineSpacing = 0
  protected Boolean $modifiesPunctuation = false

  public ContentPaneCreator(TextFlow contentPane, W word, D dictionary) {
    $contentPane = contentPane
    $word = word
    $dictionary = dictionary
  }

  public abstract void create()

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