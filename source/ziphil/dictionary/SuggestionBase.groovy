package ziphil.dictionary

import groovy.transform.CompileStatic
import javafx.scene.layout.Pane
import javafx.scene.text.TextFlow
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public abstract class SuggestionBase<P extends Possibility> implements Suggestion<P> {

  protected List<P> $possibilities = ArrayList.new()
  private PaneFactory $paneFactory
  private Boolean $displayed = false

  public abstract void update()

  protected abstract PaneFactory createPaneFactory()

  protected void changePaneFactory() {
    if ($paneFactory != null) {
      $paneFactory.change()
    }
  }

  public Boolean isDisplayed() {
    return $displayed
  }

  public void setDisplayed(Boolean displayed) {
    $displayed = displayed
  }

  public List<P> getPossibilities() {
    return $possibilities
  }

  public void setPossibilities(List<P> possibilities) {
    $possibilities = possibilities
  }

  public PaneFactory getPaneFactory() {
    if ($paneFactory == null) {
      $paneFactory = createPaneFactory()
    }
    return $paneFactory
  }

}