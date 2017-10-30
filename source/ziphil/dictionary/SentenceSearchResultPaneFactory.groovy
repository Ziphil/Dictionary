package ziphil.dictionary

import groovy.transform.CompileStatic
import javafx.scene.control.Label
import javafx.scene.control.Separator
import javafx.scene.layout.Pane
import javafx.scene.layout.VBox
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class SentenceSearchResultPaneFactory implements PaneFactory {

  private SentenceSearcher.Result $result

  public SentenceSearchResultPaneFactory(SentenceSearcher.Result result) {
    $result = result
  }

  public Pane create(Boolean forcesCreate) {
    VBox pane = VBox.new()
    Label searchText = Label.new($result.getSearch())
    Separator separator = Separator.new()
    pane.getStyleClass().add(CONTENT_PANE_CLASS)
    searchText.getStyleClass().add(CONTENT_CLASS)
    separator.getStyleClass().addAll(CONTENT_CLASS, SEPARATOR_CLASS)
    pane.getChildren().addAll(searchText, separator)
    for (Word word : $result.getWords()) {
      Pane wordPane = word.getPlainPaneFactory().create(true)
      pane.getChildren().add(wordPane)
    }
    if ($result.getWords().isEmpty()) {
      Label emptyText = Label.new("該当なし")
      emptyText.getStyleClass().add(CONTENT_CLASS)
      pane.getChildren().add(emptyText)
    }
    return pane
  }

  public void destroy() {
  }

  public void change() {
  }

  public void setPersisted(Boolean persisted) {
  }

}