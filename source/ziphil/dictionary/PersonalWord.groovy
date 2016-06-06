package ziphil.dictionary

import groovy.transform.CompileStatic
import javafx.scene.control.Label
import javafx.scene.layout.HBox
import javafx.scene.layout.Pane
import javafx.scene.layout.VBox
import javafx.scene.text.Text
import javafx.scene.text.TextFlow


@CompileStatic @Newify
public class PersonalWord extends Word {

  private String $name = ""
  private List<String> $equivalents = ArrayList.new()
  private String $pronunciation = ""
  private String $translation = ""
  private String $usage = ""
  private Integer $level = 0
  private Integer $memory = 0
  private Integer $modification = 0
  private String $content = ""
  private VBox $contentPane = VBox.new()

  public PersonalWord(String name, String pronunciation, String translation, String usage, Integer level, Integer memory, Integer modification) {
    update(name, pronunciation, translation, usage, level, memory, modification)
  }

  public void update(String name, String pronunciation, String translation, String usage, Integer level, Integer memory, Integer modification) {
    $name = name
    $translation = translation
    $usage = usage
    $level = level
    $memory = memory
    $modification = modification
    $pronunciation = pronunciation
    Label nameText = Label.new(name)
    Text translationText = Text.new(translation)
    Text usageText = Text.new(usage)
    TextFlow translationTextFlow = TextFlow.new(translationText)
    TextFlow usageTextFlow = TextFlow.new(usageText)
    nameText.getStyleClass().addAll("content-text", "head-name")
    translationText.getStyleClass().add("content-text")
    usageText.getStyleClass().add("content-text")
    $contentPane.getChildren().clear()
    $contentPane.getChildren().addAll(nameText, translationTextFlow, usageTextFlow)
    $content = name + "\n" + translation + "\n" + usage
  }

  public static PersonalWord emptyWord() {
    return PersonalWord.new("", "", "", "", 0, 0, 0)
  }

  public static PersonalWord copyFrom(PersonalWord oldWord) {
    String name = oldWord.getName()
    String pronunciation = oldWord.getPronunciation()
    String translation = oldWord.getTranslation()
    String usage = oldWord.getUsage()
    Integer level = oldWord.getLevel()
    Integer memory = oldWord.getMemory()
    Integer modification = oldWord.getModification()
    return PersonalWord.new(name, pronunciation, translation, usage, level, memory, modification)
  }

  public String getName() {
    return $name
  }

  public List<String> getEquivalents() {
    return null
  }

  public String getPronunciation() {
    return $pronunciation
  }

  public String getTranslation() {
    return $translation
  }

  public String getUsage() {
    return $usage
  }

  public Integer getLevel() {
    return $level
  }

  public Integer getMemory() {
    return $memory
  }

  public Integer getModification() {
    return $modification
  }

  public String getContent() {
    return $content
  }

  public Pane getContentPane() {
    return $contentPane
  }

}