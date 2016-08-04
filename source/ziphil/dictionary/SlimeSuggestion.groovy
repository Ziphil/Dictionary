package ziphil.dictionary

import groovy.transform.CompileStatic
import javafx.scene.layout.VBox
import javafx.scene.text.Text
import javafx.scene.text.TextFlow


@CompileStatic @Newify
public class SlimeSuggestion extends Suggestion<SlimePossibility> {

  public static final String SLIME_LINK_CLASS = "slime-link"

  private SlimeDictionary $dictionary

  public SlimeSuggestion() {
    update()
    setupContentPane()
  }

  public void update() {
    $isChanged = true
  }

  public void createContentPane() {
    VBox possibilityBox = VBox.new()
    $contentPane.getChildren().clear()
    $contentPane.getChildren().addAll(possibilityBox)
    $possibilities.each() { SlimePossibility possibility ->
      addPossibilityNode(possibilityBox, possibility.getWord().getId(), possibility.getWord().getName(), possibility.getPossibilityName())
    }
    $isChanged = false
  }

  private void addPossibilityNode(VBox box, Integer id, String name, String possibilityName) {
    TextFlow textFlow = TextFlow.new()
    Text prefixText = Text.new("もしかして: ")
    Text nameText = Text.new(name)
    Text possibilityNameText = Text.new(" の${possibilityName}?")
    prefixText.getStyleClass().add(CONTENT_CLASS)
    nameText.getStyleClass().addAll(CONTENT_CLASS, SLIME_LINK_CLASS)
    possibilityNameText.getStyleClass().add(CONTENT_CLASS)
    nameText.setOnMouseClicked() {
      println("${id} clicked")
    }
    textFlow.getChildren().addAll(prefixText, nameText, possibilityNameText)
    box.getChildren().add(textFlow)
  }

  private void setupContentPane() {
    $contentPane.getStyleClass().add(CONTENT_PANE_CLASS)
  }

  public SlimeDictionary getDictionary() {
    return $dictionary
  }

  public void setDictionary(SlimeDictionary dictionary) {
    $dictionary = dictionary
  }

}