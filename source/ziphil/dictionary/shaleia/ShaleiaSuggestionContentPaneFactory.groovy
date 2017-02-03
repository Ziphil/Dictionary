package ziphil.dictionary.shaleia

import groovy.transform.CompileStatic
import javafx.geometry.Pos
import javafx.scene.input.MouseEvent
import javafx.scene.text.Text
import javafx.scene.text.TextFlow
import ziphil.dictionary.ContentPaneFactory
import ziphil.module.Strings
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class ShaleiaSuggestionContentPaneFactory extends ContentPaneFactory<ShaleiaSuggestion, ShaleiaDictionary> {

  private static final String SHALEIA_LINK_CLASS = "shaleia-link"
  private static final String SHALEIA_POSSIBILITY_CLASS = "shaleia-possibility"

  public ShaleiaSuggestionContentPaneFactory(ShaleiaSuggestion word, ShaleiaDictionary dictionary) {
    super(word, dictionary)
  }

  protected void update() {
    $contentPane.getStyleClass().clear()
    $contentPane.getStyleClass().add(CONTENT_PANE_CLASS)
    $contentPane.getChildren().clear()
    $contentPane.setLineSpacing($lineSpacing)
    for (ShaleiaPossibility possibility : $word.getPossibilities()) {
      addPossibilityNode(possibility.getName(), possibility.getPossibilityName())
    }
    modifyBreak()
  }

  private void addPossibilityNode(String name, String possibilityName) {
    Text prefixText = Text.new("もしかして: ")
    Text nameText = Text.new(name)
    Text possibilityNameText = Text.new(" の${possibilityName}?")
    Text breakText = Text.new("\n")
    nameText.addEventHandler(MouseEvent.MOUSE_CLICKED) { MouseEvent event ->
      if ($dictionary.getOnLinkClicked() != null) {
        $dictionary.getOnLinkClicked().accept(name)
      }
    }
    prefixText.getStyleClass().addAll(CONTENT_CLASS, SHALEIA_POSSIBILITY_CLASS)
    nameText.getStyleClass().addAll(CONTENT_CLASS, SHALEIA_LINK_CLASS)
    possibilityNameText.getStyleClass().add(CONTENT_CLASS)
    $contentPane.getChildren().addAll(prefixText, nameText, possibilityNameText, breakText)
  }

}