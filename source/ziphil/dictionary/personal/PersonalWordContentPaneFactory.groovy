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

  private static final String PERSONAL_HEAD_NAME_CLASS = "personal-head-name"
  private static final String PERSONAL_PRONUNCIATION_CLASS = "persoanl-pronunciation"

  public PersonalWordContentPaneFactory(PersonalWord word, PersonalDictionary dictionary, Boolean persisted) {
    super(word, dictionary, persisted)
  }

  protected Pane doCreate() {
    Int lineSpacing = Setting.getInstance().getLineSpacing()
    TextFlow contentPane = TextFlow.new()
    contentPane.getStyleClass().add(CONTENT_PANE_CLASS)
    contentPane.setLineSpacing(lineSpacing)
    String pronunciation = $word.getPronunciation()
    if (pronunciation != "") {
      if (!pronunciation.startsWith("/") && !pronunciation.startsWith("[")) {
        pronunciation = "/" + pronunciation
      }
      if (!pronunciation.endsWith("/") && !pronunciation.endsWith("[")) {
        pronunciation = pronunciation + "/"
      }
    }
    addNameNode(contentPane, $word.getName(), pronunciation)
    addContentNode(contentPane, $word.getTranslation())
    addContentNode(contentPane, $word.getUsage())
    modifyBreak(contentPane)
    return contentPane
  }

  private void addNameNode(TextFlow contentPane, String name, String pronunciation) {
    if (pronunciation != "") {
      Label nameText = Label.new(name + " ")
      Text pronunciationText = Text.new(pronunciation)
      Text breakText = Text.new("\n")
      nameText.getStyleClass().addAll(CONTENT_CLASS, HEAD_NAME_CLASS, PERSONAL_HEAD_NAME_CLASS)
      pronunciationText.getStyleClass().addAll(CONTENT_CLASS, PERSONAL_PRONUNCIATION_CLASS)
      contentPane.getChildren().addAll(nameText, pronunciationText, breakText)
    } else {
      Label nameText = Label.new(name)
      Text breakText = Text.new("\n")
      nameText.getStyleClass().addAll(CONTENT_CLASS, HEAD_NAME_CLASS, PERSONAL_HEAD_NAME_CLASS)
      contentPane.getChildren().addAll(nameText, breakText)
    }
  }

  private void addContentNode(TextFlow contentPane, String content) {
    Boolean modifiesPunctuation = Setting.getInstance().getModifiesPunctuation()
    String modifiedContent = (modifiesPunctuation) ? Strings.modifyPunctuation(content) : content
    Text contentText = Text.new(modifiedContent)
    Text breakText = Text.new("\n")
    contentText.getStyleClass().add(CONTENT_CLASS)
    if (content != "") {
      contentPane.getChildren().addAll(contentText, breakText)
    }
  }

}