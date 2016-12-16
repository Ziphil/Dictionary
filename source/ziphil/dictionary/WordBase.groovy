package ziphil.dictionary

import groovy.transform.CompileStatic
import javafx.scene.layout.Pane
import javafx.scene.text.TextFlow
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public abstract class WordBase implements Word {

  protected String $name = ""
  protected List<String> $equivalents = ArrayList.new()
  protected String $content = ""
  protected TextFlow $contentPane = TextFlow.new()
  protected Boolean $isChanged = true

  public abstract void createContentPane()

  public abstract void update()

  public void change() {
    $isChanged = true
  }

  public String getName() {
    return $name
  }

  public void setName(String name) {
    $name = name
  }

  public List<String> getEquivalents() {
    return $equivalents
  }

  public void setEquivalents(List<String> equivalents) {
    $equivalents = equivalents
  }

  public String getContent() {
    return $content
  }

  public void setContent(String content) {
    $content = content
  }

  public Pane getContentPane() {
    return $contentPane
  }

  public Boolean isChanged() {
    return $isChanged
  }

}