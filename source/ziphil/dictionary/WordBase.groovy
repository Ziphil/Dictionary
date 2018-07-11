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
  private PaneFactory $paneFactory
  private PaneFactory $plainPaneFactory

  public abstract void update()

  protected abstract PaneFactory createPaneFactory()

  protected abstract PaneFactory createPlainPaneFactory()

  protected void changePaneFactory() {
    if ($paneFactory != null) {
      $paneFactory.change()
    }
  }

  protected void changePlainPaneFactory() {
    if ($plainPaneFactory != null) {
      $plainPaneFactory.change()
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

  public abstract String getIdentifier()

  public PaneFactory getPaneFactory() {
    if ($paneFactory == null) {
      $paneFactory = createPaneFactory()
    }
    return $paneFactory
  }

  public PaneFactory getPlainPaneFactory() {
    if ($plainPaneFactory == null) {
      $plainPaneFactory = createPlainPaneFactory()
    }
    return $plainPaneFactory
  }

}