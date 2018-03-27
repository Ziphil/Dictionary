package ziphil.dictionary.shaleia

import groovy.transform.CompileStatic
import javafx.event.EventHandler
import javafx.geometry.Pos
import javafx.scene.input.MouseEvent
import javafx.scene.layout.Pane
import javafx.scene.text.Text
import javafx.scene.text.TextFlow
import ziphil.dictionary.PaneFactoryBase
import ziphil.dictionary.SearchParameter
import ziphil.module.Setting
import ziphil.module.Strings
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class ShaleiaSuggestionPaneFactory extends PaneFactoryBase<ShaleiaSuggestion, ShaleiaDictionary> {

  private static final String SHALEIA_LINK_CLASS = "shaleia-link"
  private static final String SHALEIA_POSSIBILITY_CLASS = "shaleia-possibility"

  public ShaleiaSuggestionPaneFactory(ShaleiaSuggestion word, ShaleiaDictionary dictionary, Boolean persisted) {
    super(word, dictionary, persisted)
  }

  public ShaleiaSuggestionPaneFactory(ShaleiaSuggestion word, ShaleiaDictionary dictionary) {
    super(word, dictionary)
  }

  protected Pane doCreate() {
    Int lineSpacing = Setting.getInstance().getLineSpacing()
    TextFlow pane = TextFlow.new()
    pane.getStyleClass().add(CONTENT_PANE_CLASS)
    pane.setLineSpacing(lineSpacing)
    for (ShaleiaPossibility possibility : $word.getPossibilities()) {
      addPossibilityNode(pane, possibility.createParameter(), possibility.getWords(), possibility.getExplanation())
    }
    modifyBreak(pane)
    return pane
  }

  private void addPossibilityNode(TextFlow pane, SearchParameter parameter, List<ShaleiaWord> words, String explanation) {
    Text prefixText = Text.new("もしかして:")
    Text spaceText = Text.new(" ")
    prefixText.getStyleClass().addAll(CONTENT_CLASS, SHALEIA_POSSIBILITY_CLASS)
    spaceText.getStyleClass().add(CONTENT_CLASS)
    pane.getChildren().addAll(prefixText, spaceText)
    for (ShaleiaWord word : words) {
      Text nameText = Text.new(word.getName())
      Text punctuationText = Text.new(", ")
      nameText.addEventHandler(MouseEvent.MOUSE_CLICKED, createLinkEventHandler(parameter))
      nameText.getStyleClass().addAll(CONTENT_CLASS, SHALEIA_LINK_CLASS)
      punctuationText.getStyleClass().add(CONTENT_CLASS)
      pane.getChildren().add(nameText)
      if (!word.is(words.last())) {
        pane.getChildren().add(punctuationText)
      }
    }
    Text explanationText = Text.new(" の${explanation}?")
    Text breakText = Text.new("\n")
    explanationText.getStyleClass().add(CONTENT_CLASS)
    pane.getChildren().addAll(explanationText, breakText)
  }

  private EventHandler<MouseEvent> createLinkEventHandler(SearchParameter parameter) {
    EventHandler<MouseEvent> handler = { MouseEvent event ->
      if ($dictionary.getOnLinkClicked() != null) {
        if ($linkClickType != null && $linkClickType.matches(event)) {
          $dictionary.getOnLinkClicked().accept(parameter)
        }
      }
    }
    return handler
  }

}