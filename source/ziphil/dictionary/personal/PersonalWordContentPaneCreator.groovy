package ziphil.dictionary.personal

import groovy.transform.CompileStatic
import javafx.scene.control.Label
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import javafx.scene.text.Text
import javafx.scene.text.TextFlow
import ziphil.dictionary.ContentPaneCreator
import ziphil.module.Strings
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class PersonalWordContentPaneCreator extends ContentPaneCreator<PersonalWord, PersonalDictionary> {

  public PersonalWordContentPaneCreator(TextFlow contentPane, PersonalWord word, PersonalDictionary dictionary) {
    super(contentPane, word, dictionary)
  }

  public void create() {
    $contentPane.getStyleClass().clear()
    $contentPane.getStyleClass().add(CONTENT_PANE_CLASS)
    $contentPane.getChildren().clear()
    addNameNode($word.getName())
    addOtherNode($word.getTranslation())
    addOtherNode($word.getUsage())
    modifyBreak()
  }

  private void addNameNode(String name) {
    Label nameText = Label.new(name)
    Text breakText = Text.new("\n")
    nameText.getStyleClass().addAll(CONTENT_CLASS, HEAD_NAME_CLASS)
    $contentPane.getChildren().addAll(nameText, breakText)
  }

  private void addOtherNode(String other) {
    String modifiedOther = ($modifiesPunctuation) ? Strings.modifyPunctuation(other) : other
    Text otherText = Text.new(modifiedOther)
    Text breakText = Text.new("\n")
    otherText.getStyleClass().add(CONTENT_CLASS)
    $contentPane.getChildren().addAll(otherText, breakText)
  }

}