package ziphil.dictionary.slime

import groovy.transform.CompileStatic
import javafx.geometry.Pos
import javafx.scene.input.MouseEvent
import javafx.scene.layout.VBox
import javafx.scene.text.Text
import javafx.scene.text.TextFlow
import ziphil.dictionary.ContentPaneCreator
import ziphil.module.Strings
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class SlimeSuggestionContentPaneCreator extends ContentPaneCreator<SlimeSuggestion, SlimeDictionary> {

  public static final String SLIME_LINK_CLASS = "slime-link"
  public static final String SLIME_POSSIBILITY_CLASS = "slime-possibility"

  public SlimeSuggestionContentPaneCreator(VBox contentPane, SlimeSuggestion word, SlimeDictionary dictionary) {
    super(contentPane, word, dictionary)
  }

  public void create() {
    VBox possibilityBox = VBox.new()
    $contentPane.getStyleClass().clear()
    $contentPane.getStyleClass().add(CONTENT_PANE_CLASS)
    $contentPane.getChildren().clear()
    $contentPane.getChildren().addAll(possibilityBox)
    for (SlimePossibility possibility : $word.getPossibilities()) {
      addPossibilityNode(possibilityBox, possibility.getWord().getId(), possibility.getWord().getName(), possibility.getPossibilityName())
    }
  }

  private void addPossibilityNode(VBox box, Integer id, String name, String possibilityName) {
    TextFlow textFlow = TextFlow.new()
    Text prefixText = Text.new("もしかして: ")
    Text nameText = Text.new(name)
    Text possibilityNameText = Text.new(" の${possibilityName}?")
    nameText.addEventHandler(MouseEvent.MOUSE_CLICKED) { MouseEvent event ->
      if ($dictionary.getOnLinkClicked() != null) {
        $dictionary.getOnLinkClicked().accept(id)
      }
    }
    prefixText.getStyleClass().addAll(CONTENT_CLASS, SLIME_POSSIBILITY_CLASS)
    nameText.getStyleClass().addAll(CONTENT_CLASS, SLIME_LINK_CLASS)
    possibilityNameText.getStyleClass().add(CONTENT_CLASS)
    textFlow.getChildren().addAll(prefixText, nameText, possibilityNameText)
    box.getChildren().add(textFlow)
  }

}