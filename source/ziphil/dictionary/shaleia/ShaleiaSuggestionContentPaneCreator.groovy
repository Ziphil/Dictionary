package ziphil.dictionary.shaleia

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
public class ShaleiaSuggestionContentPaneCreator extends ContentPaneCreator<ShaleiaSuggestion, ShaleiaDictionary> {

  private static final String SHALEIA_LINK_CLASS = "shaleia-link"
  private static final String SHALEIA_POSSIBILITY_CLASS = "shaleia-possibility"

  public ShaleiaSuggestionContentPaneCreator(VBox contentPane, ShaleiaSuggestion word, ShaleiaDictionary dictionary) {
    super(contentPane, word, dictionary)
  }

  public void create() {
    VBox possibilityBox = VBox.new()
    $contentPane.getStyleClass().clear()
    $contentPane.getStyleClass().add(CONTENT_PANE_CLASS)
    $contentPane.getChildren().clear()
    $contentPane.getChildren().addAll(possibilityBox)
    for (ShaleiaPossibility possibility : $word.getPossibilities()) {
      addPossibilityNode(possibilityBox, possibility.getName(), possibility.getPossibilityName())
    }
  }

  private void addPossibilityNode(VBox box, String name, String possibilityName) {
    TextFlow textFlow = TextFlow.new()
    Text prefixText = Text.new("もしかして: ")
    Text nameText = Text.new(name)
    Text possibilityNameText = Text.new(" の${possibilityName}?")
    nameText.addEventHandler(MouseEvent.MOUSE_CLICKED) { MouseEvent event ->
      if ($dictionary.getOnLinkClicked() != null) {
        $dictionary.getOnLinkClicked().accept(name)
      }
    }
    prefixText.getStyleClass().addAll(CONTENT_CLASS, SHALEIA_POSSIBILITY_CLASS)
    nameText.getStyleClass().addAll(CONTENT_CLASS, SHALEIA_LINK_CLASS)
    possibilityNameText.getStyleClass().add(CONTENT_CLASS)
    textFlow.getChildren().addAll(prefixText, nameText, possibilityNameText)
    box.getChildren().add(textFlow)
  }

}