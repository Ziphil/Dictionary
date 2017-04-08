package ziphil.dictionary.shaleia

import groovy.transform.CompileStatic
import javafx.geometry.Pos
import javafx.scene.input.MouseEvent
import javafx.scene.layout.Pane
import javafx.scene.text.Text
import javafx.scene.text.TextFlow
import ziphil.dictionary.ContentPaneFactoryBase
import ziphil.module.Setting
import ziphil.module.Strings
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class ShaleiaSuggestionContentPaneFactory extends ContentPaneFactoryBase<ShaleiaSuggestion, ShaleiaDictionary> {

  private static final String SHALEIA_LINK_CLASS = "shaleia-link"
  private static final String SHALEIA_POSSIBILITY_CLASS = "shaleia-possibility"

  public ShaleiaSuggestionContentPaneFactory(ShaleiaSuggestion word, ShaleiaDictionary dictionary) {
    super(word, dictionary)
  }

  public Pane create() {
    Integer lineSpacing = Setting.getLineSpacing()
    TextFlow contentPane = TextFlow.new()
    contentPane.getStyleClass().add(CONTENT_PANE_CLASS)
    contentPane.setLineSpacing(lineSpacing)
    for (ShaleiaPossibility possibility : $word.getPossibilities()) {
      addPossibilityNode(contentPane, possibility.getName(), possibility.getExplanation())
    }
    modifyBreak(contentPane)
    return contentPane
  }

  private void addPossibilityNode(TextFlow contentPane, String name, String explanation) {
    Text prefixText = Text.new("もしかして:")
    Text spaceText = Text.new(" ")
    Text nameText = Text.new(name)
    Text explanationText = Text.new(" の${explanation}?")
    Text breakText = Text.new("\n")
    nameText.addEventHandler(MouseEvent.MOUSE_CLICKED) { MouseEvent event ->
      if ($dictionary.getOnLinkClicked() != null) {
        $dictionary.getOnLinkClicked().accept(name)
      }
    }
    prefixText.getStyleClass().addAll(CONTENT_CLASS, SHALEIA_POSSIBILITY_CLASS)
    spaceText.getStyleClass().add(CONTENT_CLASS)
    nameText.getStyleClass().addAll(CONTENT_CLASS, SHALEIA_LINK_CLASS)
    explanationText.getStyleClass().add(CONTENT_CLASS)
    contentPane.getChildren().addAll(prefixText, spaceText, nameText, explanationText, breakText)
  }

}