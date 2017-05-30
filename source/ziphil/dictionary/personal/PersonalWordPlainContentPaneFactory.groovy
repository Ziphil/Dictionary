package ziphil.dictionary.personal

import groovy.transform.CompileStatic
import javafx.geometry.Pos
import javafx.scene.layout.Pane
import javafx.scene.text.Text
import javafx.scene.text.TextFlow
import ziphil.dictionary.ContentPaneFactoryBase
import ziphil.module.Strings
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class PersonalWordPlainContentPaneFactory extends ContentPaneFactoryBase<PersonalWord, PersonalDictionary> {

  private static final String PERSONAL_HEAD_NAME_CLASS = "personal-head-name"

  public PersonalWordPlainContentPaneFactory(PersonalWord word, PersonalDictionary dictionary) {
    super(word, dictionary)
  }

  public Pane create() {
    TextFlow contentPane = TextFlow.new()
    contentPane.getStyleClass().add(CONTENT_PANE_CLASS)
    addNameNode(contentPane, $word.getName())
    modifyBreak(contentPane)
    return contentPane
  }

  private void addNameNode(TextFlow contentPane, String name) {
    Text nameText = Text.new(name + " ")
    Text breakText = Text.new("\n")
    nameText.getStyleClass().addAll(CONTENT_CLASS, HEAD_NAME_CLASS, PERSONAL_HEAD_NAME_CLASS)
    contentPane.getChildren().addAll(nameText, breakText)
  }

}