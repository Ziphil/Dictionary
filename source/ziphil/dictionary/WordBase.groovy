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
  protected ContentPaneFactory $contentPaneFactory

  public abstract void update()

  protected abstract void makeContentPaneFactory()

  protected void changeContentPaneFactory() {
    if ($contentPaneFactory != null) {
      $contentPaneFactory.change()
    }
  }

  public Boolean isDisplayed() {
    return true
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

  public ContentPaneFactory getContentPaneFactory() {
    if ($contentPaneFactory == null) {
      makeContentPaneFactory()
    }
    return $contentPaneFactory
  }

}