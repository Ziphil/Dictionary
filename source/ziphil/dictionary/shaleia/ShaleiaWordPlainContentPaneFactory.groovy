package ziphil.dictionary.shaleia

import groovy.transform.CompileStatic
import javafx.geometry.Pos
import javafx.scene.layout.Pane
import javafx.scene.text.Text
import javafx.scene.text.TextFlow
import ziphil.dictionary.ContentPaneFactoryBase
import ziphil.module.Strings
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class ShaleiaWordPlainContentPaneFactory extends ContentPaneFactoryBase<ShaleiaWord, ShaleiaDictionary> {

  private static final String SHALEIA_HEAD_NAME_CLASS = "shaleia-head-name"
  private static final String SHALEIA_EQUIVALENT_CLASS = "shaleia-equivalent"

  public ShaleiaWordPlainContentPaneFactory(ShaleiaWord word, ShaleiaDictionary dictionary) {
    super(word, dictionary)
  }

  public Pane create() {
    TextFlow contentPane = TextFlow.new()
    contentPane.getStyleClass().add(CONTENT_PANE_CLASS)
    addNameNode(contentPane, $word.getName())
    addEquivalentNode(contentPane, $word.getEquivalents().join(", "))
    modifyBreak(contentPane)
    return contentPane
  }

  private void addNameNode(TextFlow contentPane, String name) {
    Text nameText = Text.new(name + " ")
    Text breakText = Text.new("\n")
    nameText.getStyleClass().addAll(CONTENT_CLASS, HEAD_NAME_CLASS, SHALEIA_HEAD_NAME_CLASS)
    contentPane.getChildren().addAll(nameText, breakText)
  }

  private void addEquivalentNode(TextFlow contentPane, String equivalent) {
    Text equivalentText = Text.new(equivalent)
    Text breakText = Text.new("\n")
    equivalentText.getStyleClass().addAll(CONTENT_CLASS, SHALEIA_EQUIVALENT_CLASS)
    contentPane.getChildren().addAll(equivalentText, breakText)
  }

}