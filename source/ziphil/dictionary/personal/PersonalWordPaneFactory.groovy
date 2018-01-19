package ziphil.dictionary.personal

import groovy.transform.CompileStatic
import javafx.scene.control.Label
import javafx.scene.control.Separator
import javafx.scene.layout.Pane
import javafx.scene.layout.VBox
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
    VBox pane = VBox.new()
    TextFlow mainPane = TextFlow.new()
    TextFlow contentPane = TextFlow.new()
    Boolean hasContent = false
    pane.getStyleClass().add(CONTENT_PANE_CLASS)
    mainPane.setLineSpacing(lineSpacing)
    contentPane.setLineSpacing(lineSpacing)
    addNameNode(mainPane, $word.getName(), $word.createPronunciation())
    if (!$word.getTranslation().isEmpty()) {
      addContentNode(contentPane, $word.getTranslation())
      hasContent = true
    }
    if (!$word.getUsage().isEmpty()) {
      addContentNode(contentPane, $word.getUsage())
      hasContent = true
    }
    modifyBreak(mainPane)
    modifyBreak(contentPane)
    pane.getChildren().add(mainPane)
    if (hasContent) {
      addSeparator(pane)
      pane.getChildren().add(contentPane)
    }
    return pane
  }

  private void addNameNode(TextFlow pane, String name, String pronunciation) {
    if (!pronunciation.isEmpty()) {
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
    pane.getChildren().addAll(contentText, breakText)
  }

  private void addSeparator(Pane pane) {
    Boolean showsSeparator = Setting.getInstance().getShowsSeparator()
    if (showsSeparator) {
      Separator separator = Separator.new()
      separator.getStyleClass().addAll(CONTENT_CLASS, SEPARATOR_CLASS)
      pane.getChildren().add(separator)
    } else {
      VBox box = VBox.new()
      box.getStyleClass().addAll(CONTENT_CLASS, MARGIN_CLASS)
      pane.getChildren().add(box)
    }
  }

}