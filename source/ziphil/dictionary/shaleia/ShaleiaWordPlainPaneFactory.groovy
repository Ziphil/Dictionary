package ziphil.dictionary.shaleia

import groovy.transform.CompileStatic
import javafx.geometry.Pos
import javafx.scene.layout.Pane
import javafx.scene.text.Text
import javafx.scene.text.TextFlow
import ziphil.dictionary.PaneFactoryBase
import ziphil.module.Strings
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class ShaleiaWordPlainPaneFactory extends PaneFactoryBase<ShaleiaWord, ShaleiaDictionary> {

  private static final String SHALEIA_HEAD_NAME_CLASS = "shaleia-head-name"
  private static final String SHALEIA_EQUIVALENT_CLASS = "shaleia-equivalent"

  public ShaleiaWordPlainPaneFactory(ShaleiaWord word, ShaleiaDictionary dictionary, Boolean persisted) {
    super(word, dictionary, persisted)
  }

  public ShaleiaWordPlainPaneFactory(ShaleiaWord word, ShaleiaDictionary dictionary) {
    super(word, dictionary)
  }

  protected Pane doCreate() {
    TextFlow pane = TextFlow.new()
    pane.getStyleClass().add(CONTENT_PANE_CLASS)
    addNameNode(pane, $word.getName())
    addEquivalentNode(pane, $word.getEquivalents().join(", "))
    modifyBreak(pane)
    return pane
  }

  private void addNameNode(TextFlow pane, String name) {
    Text nameText = Text.new(name + " ")
    Text breakText = Text.new("\n")
    nameText.getStyleClass().addAll(CONTENT_CLASS, HEAD_NAME_CLASS, SHALEIA_HEAD_NAME_CLASS)
    pane.getChildren().addAll(nameText, breakText)
  }

  private void addEquivalentNode(TextFlow pane, String equivalent) {
    Text equivalentText = Text.new(equivalent)
    Text breakText = Text.new("\n")
    equivalentText.getStyleClass().addAll(CONTENT_CLASS, SHALEIA_EQUIVALENT_CLASS)
    pane.getChildren().addAll(equivalentText, breakText)
  }

}