package ziphil.dictionary.slime

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
public class SlimeSuggestionPaneFactory extends PaneFactoryBase<SlimeSuggestion, SlimeDictionary> {

  public static final String SLIME_LINK_CLASS = "slime-link"
  public static final String SLIME_POSSIBILITY_CLASS = "slime-possibility"

  public SlimeSuggestionPaneFactory(SlimeSuggestion word, SlimeDictionary dictionary, Boolean persisted) {
    super(word, dictionary, persisted)
  }

  public SlimeSuggestionPaneFactory(SlimeSuggestion word, SlimeDictionary dictionary) {
    super(word, dictionary)
  }

  protected Pane doCreate() {
    Int lineSpacing = Setting.getInstance().getLineSpacing()
    TextFlow pane = TextFlow.new()
    pane.getStyleClass().add(CONTENT_PANE_CLASS)
    pane.setLineSpacing(lineSpacing)
    for (SlimePossibility possibility : $word.getPossibilities()) {
      addPossibilityNode(pane, possibility.createParameter(), possibility.getWord().getName(), possibility.getTitle())
    }
    modifyBreak(pane)
    return pane
  }

  private void addPossibilityNode(TextFlow pane, SearchParameter parameter, String name, String title) {
    Text prefixText = Text.new("もしかして:")
    Text spaceText = Text.new(" ")
    Text nameText = Text.new(name)
    Text titleText = Text.new(" の${title}?")
    Text breakText = Text.new("\n")
    nameText.addEventHandler(MouseEvent.MOUSE_CLICKED, createLinkEventHandler(parameter)) 
    prefixText.getStyleClass().addAll(CONTENT_CLASS, SLIME_POSSIBILITY_CLASS)
    spaceText.getStyleClass().add(CONTENT_CLASS)
    nameText.getStyleClass().addAll(CONTENT_CLASS, SLIME_LINK_CLASS)
    titleText.getStyleClass().add(CONTENT_CLASS)
    pane.getChildren().addAll(prefixText, spaceText, nameText, titleText, breakText)
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