package ziphil.dictionary.slime

import groovy.transform.CompileStatic
import javafx.geometry.Pos
import javafx.scene.input.MouseEvent
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

  protected void update() {
    $contentPane.getStyleClass().clear()
    $contentPane.getStyleClass().add(CONTENT_PANE_CLASS)
    $contentPane.getChildren().clear()
    $contentPane.setLineSpacing($lineSpacing)
    for (SlimePossibility possibility : $word.getPossibilities()) {
      addPossibilityNode(possibility.getWord().getId(), possibility.getWord().getName(), possibility.getPossibilityName())
    }
    modifyBreak()
  }

  private void addPossibilityNode(Integer id, String name, String possibilityName) {
    Text prefixText = Text.new("もしかして: ")
    Text nameText = Text.new(name)
    Text possibilityNameText = Text.new(" の${possibilityName}?")
    Text breakText = Text.new("\n")
    nameText.addEventHandler(MouseEvent.MOUSE_CLICKED) { MouseEvent event ->
      if ($dictionary.getOnLinkClicked() != null) {
        $dictionary.getOnLinkClicked().accept(id)
      }
    }
    prefixText.getStyleClass().addAll(CONTENT_CLASS, SLIME_POSSIBILITY_CLASS)
    nameText.getStyleClass().addAll(CONTENT_CLASS, SLIME_LINK_CLASS)
    possibilityNameText.getStyleClass().add(CONTENT_CLASS)
    $contentPane.getChildren().addAll(prefixText, nameText, possibilityNameText, breakText)
  }

}