package ziphil.dictionary

import groovy.transform.CompileStatic
import javafx.scene.layout.Pane
import javafx.scene.layout.VBox


@CompileStatic @Newify
public abstract class Word {

  protected String $name = ""
  protected List<String> $equivalents = ArrayList.new()
  protected String $content = ""
  protected VBox $contentPane = VBox.new()
  protected Boolean $isChanged = true

  public abstract void createContentPane()

  public void change() {
    $isChanged = true
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

  public Boolean isChanged() {
    return $isChanged
  }

}