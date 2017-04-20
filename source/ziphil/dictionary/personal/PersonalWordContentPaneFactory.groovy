package ziphil.dictionary.personal

import groovy.transform.CompileStatic
import javafx.scene.control.Label
import javafx.scene.layout.Pane
import javafx.scene.text.Text
import javafx.scene.text.TextFlow
import ziphil.dictionary.ContentPaneFactoryBase
import ziphil.module.Setting
import ziphil.module.Strings
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class PersonalWordContentPaneFactory extends ContentPaneFactoryBase<PersonalWord, PersonalDictionary> {

  private static final String PERSONAL_PRONUNCIATION_CLASS = "persoanl-pronunciation"

  public PersonalWordContentPaneFactory(PersonalWord word, PersonalDictionary dictionary) {
    super(word, dictionary)
  }

  public Pane create() {
    Integer lineSpacing = Setting.getInstance().getLineSpacing()
    TextFlow contentPane = TextFlow.new()
    contentPane.getStyleClass().add(CONTENT_PANE_CLASS)
    contentPane.setLineSpacing(lineSpacing)
    addNameNode(contentPane, $word.getName(), $word.getPronunciation())
    addOtherNode(contentPane, $word.getTranslation())
    addOtherNode(contentPane, $word.getUsage())
    modifyBreak(contentPane)
    return contentPane
  }

  private void addNameNode(TextFlow contentPane, String name, String pronunciation) {
    if (pronunciation != "") {
      if (!pronunciation.startsWith("/") && !pronunciation.startsWith("[")) {
        pronunciation = "/" + pronunciation
      }
      if (!pronunciation.endsWith("/") && !pronunciation.endsWith("[")) {
        pronunciation = pronunciation + "/"
      }
      Label nameText = Label.new(name + " ")
      Text pronunciationText = Text.new(pronunciation)
      Text breakText = Text.new("\n")
      nameText.getStyleClass().addAll(CONTENT_CLASS, HEAD_NAME_CLASS)
      pronunciationText.getStyleClass().addAll(CONTENT_CLASS, PERSONAL_PRONUNCIATION_CLASS)
      contentPane.getChildren().addAll(nameText, pronunciationText, breakText)
    } else {
      Label nameText = Label.new(name)
      Text breakText = Text.new("\n")
      nameText.getStyleClass().addAll(CONTENT_CLASS, HEAD_NAME_CLASS)
      contentPane.getChildren().addAll(nameText, breakText)
    }
  }

  private void addOtherNode(TextFlow contentPane, String other) {
    Boolean modifiesPunctuation = Setting.getInstance().getModifiesPunctuation()
    String modifiedOther = (modifiesPunctuation) ? Strings.modifyPunctuation(other) : other
    Text otherText = Text.new(modifiedOther)
    Text breakText = Text.new("\n")
    otherText.getStyleClass().add(CONTENT_CLASS)
    if (other != "") {
      contentPane.getChildren().addAll(otherText, breakText)
    }
  }

}