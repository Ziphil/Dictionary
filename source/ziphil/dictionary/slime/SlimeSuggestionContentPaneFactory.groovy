package ziphil.dictionary.slime

import groovy.transform.CompileStatic
import javafx.geometry.Pos
import javafx.scene.input.MouseEvent
import javafx.scene.layout.Pane
import javafx.scene.text.Text
import javafx.scene.text.TextFlow
import ziphil.dictionary.ContentPaneFactoryBase
import ziphil.module.Strings
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class SlimeSuggestionContentPaneFactory extends ContentPaneFactoryBase<SlimeSuggestion, SlimeDictionary> {

  public static final String SLIME_LINK_CLASS = "slime-link"
  public static final String SLIME_POSSIBILITY_CLASS = "slime-possibility"

  public SlimeSuggestionContentPaneFactory(SlimeSuggestion word, SlimeDictionary dictionary) {
    super(word, dictionary)
  }

  public Pane create() {
    TextFlow contentPane = TextFlow.new()
    contentPane.getStyleClass().add(CONTENT_PANE_CLASS)
    contentPane.setLineSpacing($lineSpacing)
    for (SlimePossibility possibility : $word.getPossibilities()) {
      addPossibilityNode(contentPane, possibility.getWord().getId(), possibility.getWord().getName(), possibility.getTitle())
    }
    modifyBreak(contentPane)
    return contentPane
  }

  private void addPossibilityNode(TextFlow contentPane, Integer id, String name, String title) {
    Text prefixText = Text.new("もしかして:")
    Text spaceText = Text.new(" ")
    Text nameText = Text.new(name)
    Text titleText = Text.new(" の${title}?")
    Text breakText = Text.new("\n")
    nameText.addEventHandler(MouseEvent.MOUSE_CLICKED) { MouseEvent event ->
      if ($dictionary.getOnLinkClicked() != null) {
        $dictionary.getOnLinkClicked().accept(id)
      }
    }
    prefixText.getStyleClass().addAll(CONTENT_CLASS, SLIME_POSSIBILITY_CLASS)
    spaceText.getStyleClass().add(CONTENT_CLASS)
    nameText.getStyleClass().addAll(CONTENT_CLASS, SLIME_LINK_CLASS)
    titleText.getStyleClass().add(CONTENT_CLASS)
    contentPane.getChildren().addAll(prefixText, spaceText, nameText, titleText, breakText)
  }

}