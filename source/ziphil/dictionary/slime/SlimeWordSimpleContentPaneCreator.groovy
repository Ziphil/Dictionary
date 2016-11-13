package ziphil.dictionary.slime

import groovy.transform.CompileStatic
import javafx.geometry.Pos
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import javafx.scene.text.Text
import javafx.scene.text.TextFlow
import ziphil.dictionary.ContentPaneCreator
import ziphil.module.Strings


@CompileStatic @Newify
public class SlimeWordSimpleContentPaneCreator extends ContentPaneCreator<SlimeWord, SlimeDictionary> {

  private static final String SLIME_HEAD_NAME_CLASS = "slime-head-name"
  private static final String SLIME_EQUIVALENT_CLASS = "slime-equivalent"
  private static final String SLIME_ID_CLASS = "slime-id"

  public SlimeWordSimpleContentPaneCreator(VBox contentPane, SlimeWord word, SlimeDictionary dictionary) {
    super(contentPane, word, dictionary)
  }

  public void create() {
    HBox headBox = HBox.new()
    VBox equivalentBox = VBox.new()
    $contentPane.getStyleClass().clear()
    $contentPane.getStyleClass().add(CONTENT_PANE_CLASS)
    $contentPane.getChildren().clear()
    $contentPane.getChildren().addAll(headBox, equivalentBox)
    addNameNode(headBox, $word.getName(), $word.getId())
    addEquivalentNode(equivalentBox, $word.getEquivalents().join(", "))
  }

  private void addNameNode(HBox box, String name, Integer id) {
    Text nameText = Text.new(name + " ")
    Text idText = Text.new("#${id}")
    nameText.getStyleClass().addAll(CONTENT_CLASS, HEAD_NAME_CLASS, SLIME_HEAD_NAME_CLASS)
    idText.getStyleClass().addAll(CONTENT_CLASS, SLIME_ID_CLASS)
    box.getChildren().addAll(nameText, idText)
    box.setAlignment(Pos.CENTER_LEFT)
  }

  private void addEquivalentNode(VBox box, String equivalent) {
    TextFlow textFlow = TextFlow.new()
    Text equivalentText = Text.new(equivalent)
    equivalentText.getStyleClass().addAll(CONTENT_CLASS, SLIME_EQUIVALENT_CLASS)
    textFlow.getChildren().add(equivalentText)
    box.getChildren().add(textFlow)
  }

}