package ziphil.dictionary.slime

import groovy.transform.CompileStatic
import javafx.geometry.Pos
import javafx.scene.layout.Pane
import javafx.scene.text.Text
import javafx.scene.text.TextFlow
import ziphil.dictionary.ContentPaneFactoryBase
import ziphil.module.Strings
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class SlimeWordPlainContentPaneFactory extends ContentPaneFactoryBase<SlimeWord, SlimeDictionary> {

  private static final String SLIME_HEAD_NAME_CLASS = "slime-head-name"
  private static final String SLIME_EQUIVALENT_CLASS = "slime-equivalent"

  public SlimeWordPlainContentPaneFactory(SlimeWord word, SlimeDictionary dictionary, Boolean persisted) {
    super(word, dictionary, persisted)
  }

  public SlimeWordPlainContentPaneFactory(SlimeWord word, SlimeDictionary dictionary) {
    super(word, dictionary)
  }

  protected Pane doCreate() {
    TextFlow contentPane = TextFlow.new()
    contentPane.getStyleClass().add(CONTENT_PANE_CLASS)
    addNameNode(contentPane, $word.getName(), $word.getId())
    addEquivalentNode(contentPane, $word.getEquivalents().join(", "))
    modifyBreak(contentPane)
    return contentPane
  }

  private void addNameNode(TextFlow contentPane, String name, Int id) {
    Text nameText = Text.new(name + " ")
    Text breakText = Text.new("\n")
    nameText.getStyleClass().addAll(CONTENT_CLASS, HEAD_NAME_CLASS, SLIME_HEAD_NAME_CLASS)
    contentPane.getChildren().addAll(nameText, breakText)
  }

  private void addEquivalentNode(TextFlow contentPane, String equivalent) {
    Text equivalentText = Text.new(equivalent)
    Text breakText = Text.new("\n")
    equivalentText.getStyleClass().addAll(CONTENT_CLASS, SLIME_EQUIVALENT_CLASS)
    contentPane.getChildren().addAll(equivalentText, breakText)
  }

}