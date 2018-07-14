package ziphil.dictionary.shaleia

import groovy.transform.CompileStatic
import javafx.event.EventHandler
import javafx.geometry.Pos
import javafx.scene.input.MouseEvent
import javafx.scene.layout.Pane
import javafx.scene.text.Text
import javafx.scene.text.TextFlow
import ziphil.custom.ElementPane
import ziphil.dictionary.PaneFactoryBase
import ziphil.dictionary.SearchParameter
import ziphil.module.Setting
import ziphil.module.Strings
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class ShaleiaSuggestionPaneFactory extends PaneFactoryBase<ShaleiaSuggestion, ShaleiaDictionary, ElementPane> {

  private static final String SHALEIA_LINK_CLASS = "shaleia-link"
  private static final String SHALEIA_POSSIBILITY_CLASS = "shaleia-possibility"

  public ShaleiaSuggestionPaneFactory(ShaleiaSuggestion word, ShaleiaDictionary dictionary, Boolean persisted) {
    super(word, dictionary, persisted)
  }

  public ShaleiaSuggestionPaneFactory(ShaleiaSuggestion word, ShaleiaDictionary dictionary) {
    super(word, dictionary)
  }

  protected ElementPane doCreate() {
    Int lineSpacing = Setting.getInstance().getLineSpacing()
    TextFlow pane = TextFlow.new()
    pane.getStyleClass().add(CONTENT_PANE_CLASS)
    pane.setLineSpacing(lineSpacing)
    for (ShaleiaPossibility possibility : $word.getPossibilities()) {
      if (possibility.getWords() != null) {
        addPossibilityNode(pane, possibility.createParameter(), possibility.getWords().collect{it.getName()}, possibility.getExplanation())
      } else {
        addPossibilityNode(pane, possibility.createParameter(), [possibility.getName()], possibility.getExplanation())
      }
    }
    modifyBreak(pane)
    return ElementPane.new(pane)
  }

  private void addPossibilityNode(TextFlow pane, SearchParameter parameter, List<String> names, String explanation) {
    Text prefixText = Text.new("もしかして:")
    Text spaceText = Text.new(" ")
    prefixText.getStyleClass().addAll(CONTENT_CLASS, SHALEIA_POSSIBILITY_CLASS)
    spaceText.getStyleClass().add(CONTENT_CLASS)
    pane.getChildren().addAll(prefixText, spaceText)
    for (Int i = 0 ; i < names.size() ; i ++) {
      Text nameText = Text.new(names[i])
      Text punctuationText = Text.new(", ")
      nameText.addEventHandler(MouseEvent.MOUSE_CLICKED, createLinkEventHandler(parameter))
      nameText.getStyleClass().addAll(CONTENT_CLASS, SHALEIA_LINK_CLASS)
      punctuationText.getStyleClass().add(CONTENT_CLASS)
      pane.getChildren().add(nameText)
      if (i < names.size() - 1) {
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