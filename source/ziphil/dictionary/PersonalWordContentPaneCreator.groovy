package ziphil.dictionary

import groovy.transform.CompileStatic
import javafx.scene.control.Label
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import javafx.scene.text.Text
import javafx.scene.text.TextFlow
import ziphil.module.Strings


@CompileStatic @Newify
public class PersonalWordContentPaneCreator extends ContentPaneCreator<PersonalWord, PersonalDictionary> {

  public PersonalWordContentPaneCreator(VBox contentPane, PersonalWord word, PersonalDictionary dictionary) {
    super(contentPane, word, dictionary)
  }

  public void create() {
    HBox headBox = HBox.new()
    VBox translationBox = VBox.new()
    VBox usageBox = VBox.new()
    $contentPane.getStyleClass().clear()
    $contentPane.getStyleClass().add(CONTENT_PANE_CLASS)
    $contentPane.getChildren().clear()
    $contentPane.getChildren().addAll(headBox, translationBox, usageBox)
    addNameNode(headBox, $word.getName())
    addOtherNode(translationBox, $word.getTranslation())
    addOtherNode(usageBox, $word.getUsage())
  }

  private void addNameNode(HBox box, String name) {
    Label nameText = Label.new(name)
    nameText.getStyleClass().addAll(CONTENT_CLASS, HEAD_NAME_CLASS)
    box.getChildren().add(nameText)
  }

  private void addOtherNode(VBox box, String other) {
    String newOther = ($modifiesPunctuation) ? Strings.modifyPunctuation(other) : other
    TextFlow textFlow = TextFlow.new()
    Text otherText = Text.new(newOther)
    otherText.getStyleClass().add(CONTENT_CLASS)
    textFlow.getChildren().add(otherText)
    box.getChildren().add(textFlow)
  }

}