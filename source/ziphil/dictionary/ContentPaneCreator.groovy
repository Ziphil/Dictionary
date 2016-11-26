package ziphil.dictionary

import groovy.transform.CompileStatic
import javafx.scene.layout.VBox
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public abstract class ContentPaneCreator<W extends Word, D extends Dictionary> {

  public static final String CONTENT_CLASS = "content"
  public static final String CONTENT_PANE_CLASS = "content-pane"
  public static final String HEAD_NAME_CLASS = "head-name"

  protected VBox $contentPane
  protected W $word
  protected D $dictionary
  protected Boolean $modifiesPunctuation = false

  public ContentPaneCreator(VBox contentPane, W word, D dictionary) {
    $contentPane = contentPane
    $word = word
    $dictionary = dictionary
  }

  public abstract void create()

  public void setModifiesPunctuation(Boolean modifiesPunctuation) {
    $modifiesPunctuation = modifiesPunctuation
  }

}