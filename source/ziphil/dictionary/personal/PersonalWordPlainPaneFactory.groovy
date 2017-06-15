package ziphil.dictionary.personal

import groovy.transform.CompileStatic
import javafx.geometry.Pos
import javafx.scene.layout.Pane
import javafx.scene.text.Text
import javafx.scene.text.TextFlow
import ziphil.dictionary.PaneFactoryBase
import ziphil.module.Strings
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class PersonalWordPlainPaneFactory extends PaneFactoryBase<PersonalWord, PersonalDictionary> {

  private static final String PERSONAL_HEAD_NAME_CLASS = "personal-head-name"

  public PersonalWordPlainPaneFactory(PersonalWord word, PersonalDictionary dictionary, Boolean persisted) {
    super(word, dictionary, persisted)
  }

  public PersonalWordPlainPaneFactory(PersonalWord word, PersonalDictionary dictionary) {
    super(word, dictionary)
  }

  protected Pane doCreate() {
    TextFlow pane = TextFlow.new()
    pane.getStyleClass().add(CONTENT_PANE_CLASS)
    addNameNode(pane, $word.getName())
    modifyBreak(pane)
    return pane
  }

  private void addNameNode(TextFlow pane, String name) {
    Text nameText = Text.new(name + " ")
    Text breakText = Text.new("\n")
    nameText.getStyleClass().addAll(CONTENT_CLASS, HEAD_NAME_CLASS, PERSONAL_HEAD_NAME_CLASS)
    pane.getChildren().addAll(nameText, breakText)
  }

}