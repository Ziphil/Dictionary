package ziphil.dictionary.database

import groovy.transform.CompileStatic
import javafx.scene.Node
import javafx.scene.control.Label
import javafx.scene.layout.Pane
import javafx.scene.text.Text
import javafx.scene.text.TextFlow
import ziphil.dictionary.ContentPaneFactory
import ziphil.dictionary.ContentPaneFactoryBase
import ziphil.module.Strings
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class DatabaseWordContentPaneFactory implements ContentPaneFactory {

  private static final String SLIME_HEAD_NAME_CLASS = "slime-head-name"

  private TextFlow $contentPane
  private DatabaseWord $word
  private DatabaseDictionary $dictionary
  private Integer $lineSpacing = 0
  private Boolean $modifiesPunctuation = false

  public DatabaseWordContentPaneFactory(DatabaseWord word, DatabaseDictionary dictionary) {
    $word = word
    $dictionary = dictionary
  }

  public Pane create() {
    $contentPane = TextFlow.new()
    $contentPane.getStyleClass().add(CONTENT_PANE_CLASS)
    $contentPane.setLineSpacing($lineSpacing)
    addNameNode($word.getName())
    return $contentPane
  }

  private void addNameNode(String name) {
    Text nameText = Text.new(name + "  ")
    nameText.getStyleClass().addAll(CONTENT_CLASS, HEAD_NAME_CLASS, SLIME_HEAD_NAME_CLASS)
    $contentPane.getChildren().add(nameText)
  }

  private void modifyBreak() {
    Node lastChild = $contentPane.getChildren().last()
    if (lastChild instanceof Text && lastChild.getText() == "\n") {
      $contentPane.getChildren().removeAt($contentPane.getChildren().size() - 1)
    }
  }

  public void setLineSpacing(Integer lineSpacing) {
    $lineSpacing = lineSpacing
  }

  public void setModifiesPunctuation(Boolean modifiesPunctuation) {
    $modifiesPunctuation = modifiesPunctuation
  }

}