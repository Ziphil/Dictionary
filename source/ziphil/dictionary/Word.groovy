package ziphil.dictionary

import groovy.transform.CompileStatic
import javafx.scene.layout.Pane
import javafx.scene.layout.VBox


@CompileStatic @Newify
public abstract class Word {

  public static final String CONTENT_CLASS = "content"
  public static final String CONTENT_PANE_CLASS = "content-pane"
  public static final String HEAD_NAME_CLASS = "head-name"

  protected String $name = ""
  protected List<String> $equivalents = ArrayList.new()
  protected String $content = ""
  protected VBox $contentPane = VBox.new()
  protected Boolean $isChanged = true

  public abstract void createContentPane()

  public Boolean isChanged() {
    return $isChanged
  }

  public String getName() {
    return $name
  }

  public List<String> getEquivalents() {
    return $equivalents
  }

  public String getContent() {
    return $content
  }

  public Pane getContentPane() {
    return $contentPane
  }

}