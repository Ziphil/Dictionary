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
  protected Boolean $isChanged = true
  protected Integer $lineSpacing = 0
  protected Boolean $modifiesPunctuation = false

  public ContentPaneFactoryBase(E word, D dictionary) {
    $word = word
    $dictionary = dictionary
  }

  public abstract Pane create()

  public void change() {
    $isChanged = true
  }

  protected void modifyBreak(TextFlow contentPane) {
    if (contentPane.getChildren().size() >= 1) {
      Node lastChild = contentPane.getChildren().last()
      if (lastChild instanceof Text && lastChild.getText() == "\n") {
        contentPane.getChildren().removeAt(contentPane.getChildren().size() - 1)
      }
    }
  }

  public void setLineSpacing(Integer lineSpacing) {
    $lineSpacing = lineSpacing
  }

  public void setModifiesPunctuation(Boolean modifiesPunctuation) {
    $modifiesPunctuation = modifiesPunctuation
  }

}