package ziphil.dictionary.slime

import groovy.transform.CompileStatic
import javafx.geometry.Pos
import javafx.scene.text.Text
import javafx.scene.text.TextFlow
import ziphil.dictionary.ContentPaneCreator
import ziphil.module.Strings
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class SlimeWordPlainContentPaneCreator extends ContentPaneCreator<SlimeWord, SlimeDictionary> {

  private static final String SLIME_HEAD_NAME_CLASS = "slime-head-name"
  private static final String SLIME_EQUIVALENT_CLASS = "slime-equivalent"
  private static final String SLIME_ID_CLASS = "slime-id"

  public SlimeWordPlainContentPaneCreator(TextFlow contentPane, SlimeWord word, SlimeDictionary dictionary) {
    super(contentPane, word, dictionary)
  }

  public void create() {
    $contentPane.getStyleClass().clear()
    $contentPane.getStyleClass().add(CONTENT_PANE_CLASS)
    $contentPane.getChildren().clear()
    addNameNode($word.getName(), $word.getId())
    addEquivalentNode($word.getEquivalents().join(", "))
    modifyBreak()
  }

  private void addNameNode(String name, Integer id) {
    Text nameText = Text.new(name + " ")
    Text idText = Text.new("#${id}")
    Text breakText = Text.new("\n")
    nameText.getStyleClass().addAll(CONTENT_CLASS, HEAD_NAME_CLASS, SLIME_HEAD_NAME_CLASS)
    idText.getStyleClass().addAll(CONTENT_CLASS, SLIME_ID_CLASS)
    $contentPane.getChildren().addAll(nameText, idText, breakText)
  }

  private void addEquivalentNode(String equivalent) {
    Text equivalentText = Text.new(equivalent)
    Text breakText = Text.new("\n")
    equivalentText.getStyleClass().addAll(CONTENT_CLASS, SLIME_EQUIVALENT_CLASS)
    $contentPane.getChildren().addAll(equivalentText, breakText)
  }

}