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
  private ContentPaneFactory $contentPaneFactory
  private ContentPaneFactory $plainContentPaneFactory

  public abstract void update()

  protected abstract ContentPaneFactory createContentPaneFactory()

  protected abstract ContentPaneFactory createPlainContentPaneFactory()

  protected void changeContentPaneFactory() {
    if ($contentPaneFactory != null) {
      $contentPaneFactory.change()
    }
  }

  protected void changePlainContentPaneFactory() {
    if ($plainContentPaneFactory != null) {
      $plainContentPaneFactory.change()
    }
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
      $contentPaneFactory = createContentPaneFactory()
    }
    return $contentPaneFactory
  }

  public ContentPaneFactory getPlainContentPaneFactory() {
    if ($plainContentPaneFactory == null) {
      $plainContentPaneFactory = createPlainContentPaneFactory()
    }
    return $plainContentPaneFactory
  }

}