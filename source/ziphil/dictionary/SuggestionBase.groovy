package ziphil.dictionary

import groovy.transform.CompileStatic
import javafx.scene.layout.Pane
import javafx.scene.text.TextFlow
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public abstract class SuggestionBase<P> implements Suggestion<P> {

  protected List<P> $possibilities = ArrayList.new()
  protected TextFlow $contentPane = TextFlow.new()
  protected Boolean $isChanged = true

  public abstract void createContentPane()

  public abstract void update()

  public void change() {
    $isChanged = true
  }

  public List<P> getPossibilities() {
    return $possibilities
  }

  public void setPossibilities(List<P> possibilities) {
    $possibilities = possibilities
  }

  public Pane getContentPane() {
    return $contentPane
  }

  public Boolean isChanged() {
    return $isChanged
  }

}