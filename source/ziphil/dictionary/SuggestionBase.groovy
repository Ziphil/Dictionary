package ziphil.dictionary

import groovy.transform.CompileStatic
import javafx.scene.layout.Pane
import javafx.scene.text.TextFlow
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public abstract class SuggestionBase<P> implements Suggestion<P> {

  protected List<P> $possibilities = ArrayList.new()
  protected ContentPaneFactoryBase $contentPaneFactory
  protected Boolean $isDisplayed = false

  public abstract void update()

  protected abstract void makeContentPaneFactory()

  protected void changeContentPaneFactory() {
    if ($contentPaneFactory != null) {
      $contentPaneFactory.change()
    }
  }

  public Boolean isDisplayed() {
    return $isDisplayed
  }

  public void setDisplayed(Boolean isDisplayed) {
    $isDisplayed = isDisplayed
  }

  public List<P> getPossibilities() {
    return $possibilities
  }

  public void setPossibilities(List<P> possibilities) {
    $possibilities = possibilities
  }

  public ContentPaneFactory getContentPaneFactory() {
    if ($contentPaneFactory == null) {
      makeContentPaneFactory()
    }
    return $contentPaneFactory
  }

}