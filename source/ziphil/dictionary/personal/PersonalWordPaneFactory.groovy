package ziphil.dictionary.personal

import groovy.transform.CompileStatic
import javafx.scene.control.Label
import javafx.scene.layout.Pane
import javafx.scene.text.Text
import javafx.scene.text.TextFlow
import ziphil.dictionary.PaneFactoryBase
import ziphil.module.Setting
import ziphil.module.Strings
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class PersonalWordPaneFactory extends PaneFactoryBase<PersonalWord, PersonalDictionary> {

  private static final String PERSONAL_HEAD_NAME_CLASS = "personal-head-name"
  private static final String PERSONAL_PRONUNCIATION_CLASS = "persoanl-pronunciation"

  public PersonalWordPaneFactory(PersonalWord word, PersonalDictionary dictionary, Boolean persisted) {
    super(word, dictionary, persisted)
  }

  public PersonalWordPaneFactory(PersonalWord word, PersonalDictionary dictionary) {
    super(word, dictionary)
  }

  protected Pane doCreate() {
    Int lineSpacing = Setting.getInstance().getLineSpacing()
    TextFlow pane = TextFlow.new()
    pane.getStyleClass().add(CONTENT_PANE_CLASS)
    pane.setLineSpacing(lineSpacing)
    addNameNode(pane, $word.getName(), $word.createPronunciation())
    addContentNode(pane, $word.getTranslation())
    addContentNode(pane, $word.getUsage())
    modifyBreak(pane)
    return pane
  }

  private void addNameNode(TextFlow pane, String name, String pronunciation) {
    if (pronunciation != "") {
      Label nameText = Label.new(name + " ")
      Text pronunciationText = Text.new(pronunciation)
      Text breakText = Text.new("\n")
      nameText.getStyleClass().addAll(CONTENT_CLASS, HEAD_NAME_CLASS, PERSONAL_HEAD_NAME_CLASS)
      pronunciationText.getStyleClass().addAll(CONTENT_CLASS, PERSONAL_PRONUNCIATION_CLASS)
      pane.getChildren().addAll(nameText, pronunciationText, breakText)
    } else {
      Label nameText = Label.new(name)
      Text breakText = Text.new("\n")
      nameText.getStyleClass().addAll(CONTENT_CLASS, HEAD_NAME_CLASS, PERSONAL_HEAD_NAME_CLASS)
      pane.getChildren().addAll(nameText, breakText)
    }
  }

  private void addContentNode(TextFlow pane, String content) {
    Boolean modifiesPunctuation = Setting.getInstance().getModifiesPunctuation()
    String modifiedContent = (modifiesPunctuation) ? Strings.modifyPunctuation(content) : content
    Text contentText = Text.new(modifiedContent)
    Text breakText = Text.new("\n")
    contentText.getStyleClass().add(CONTENT_CLASS)
    if (content != "") {
      pane.getChildren().addAll(contentText, breakText)
    }
  }

}