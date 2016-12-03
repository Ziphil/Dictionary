package ziphil.dictionary.slime

import groovy.transform.CompileStatic
import java.util.Map.Entry
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.control.Label
import javafx.scene.input.MouseEvent
import javafx.scene.text.Text
import javafx.scene.text.TextFlow
import ziphil.custom.Measurement
import ziphil.dictionary.ContentPaneCreator
import ziphil.module.Strings
import ziphilib.transform.VoidClosure
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class SlimeWordContentPaneCreator extends ContentPaneCreator<SlimeWord, SlimeDictionary> {

  private static final String SLIME_HEAD_NAME_CLASS = "slime-head-name"
  private static final String SLIME_TAG_CLASS = "slime-tag"
  private static final String SLIME_EQUIVALENT_CLASS = "slime-equivalent"
  private static final String SLIME_EQUIVALENT_TITLE_CLASS = "slime-equivalent-title"
  private static final String SLIME_TITLE_CLASS = "slime-title"
  private static final String SLIME_LINK_CLASS = "slime-link"

  public SlimeWordContentPaneCreator(TextFlow contentPane, SlimeWord word, SlimeDictionary dictionary) {
    super(contentPane, word, dictionary)
  }

  public void create() {
    Boolean hasInformation = false
    Boolean hasRelation = false
    $contentPane.getStyleClass().clear()
    $contentPane.getStyleClass().add(CONTENT_PANE_CLASS)
    $contentPane.getChildren().clear()
    $contentPane.setLineSpacing($lineSpacing)
    addNameNode($word.getName())
    addTagNode($word.getTags())
    for (SlimeEquivalent equivalent : $word.getRawEquivalents()) {
      String equivalentString = equivalent.getNames().join(", ")
      addEquivalentNode(equivalent.getTitle(), equivalentString)
    }
    for (SlimeInformation information : $word.getInformations()) {
      addInformationNode(information.getTitle(), information.getText())
      hasInformation = true
    }
    Map<String, List<SlimeRelation>> groupedRelation = $word.getRelations().groupBy{relation -> relation.getTitle()}
    for (Entry<String, List<SlimeRelation>> entry : groupedRelation) {
      String title = entry.getKey()
      List<SlimeRelation> relationGroup = entry.getValue()
      List<Integer> ids = relationGroup.collect{relation -> relation.getId()}
      List<String> names = relationGroup.collect{relation -> relation.getName()}
      addRelationNode(title, ids, names)
      hasRelation = true
    }
    modifyBreak()
  }

  private void addNameNode(String name) {
    Text nameText = Text.new(name + "  ")
    nameText.getStyleClass().addAll(CONTENT_CLASS, HEAD_NAME_CLASS, SLIME_HEAD_NAME_CLASS)
    $contentPane.getChildren().add(nameText)
  }

  private void addTagNode(List<String> tags) {
    for (String tag : tags) {
      Label tagText = Label.new(tag)
      Text spaceText = Text.new(" ")
      tagText.getStyleClass().addAll(CONTENT_CLASS, SLIME_TAG_CLASS)
      spaceText.getStyleClass().addAll(CONTENT_CLASS, SLIME_TITLE_CLASS)
      $contentPane.getChildren().addAll(tagText, spaceText)
    }
    Text breakText = Text.new("\n")
    $contentPane.getChildren().add(breakText)
  }

  private void addEquivalentNode(String title, String equivalent) {
    Label titleText = Label.new(title)
    Text equivalentText = Text.new(" " + equivalent)
    Text breakText = Text.new("\n")
    titleText.getStyleClass().addAll(CONTENT_CLASS, SLIME_EQUIVALENT_TITLE_CLASS)
    equivalentText.getStyleClass().addAll(CONTENT_CLASS, SLIME_EQUIVALENT_CLASS)
    $contentPane.getChildren().addAll(titleText, equivalentText, breakText)
  }

  private void addInformationNode(String title, String information) {
    String modifiedInformation = ($modifiesPunctuation) ? Strings.modifyPunctuation(information) : information
    Boolean insertsBreak = !$dictionary.getPlainInformationTitles().contains(title)
    Text titleText = Text.new("【${title}】")
    Text innerBreakText = Text.new((insertsBreak) ? " \n" : " ")
    Text informationText = Text.new(modifiedInformation)
    Text breakText = Text.new("\n")
    titleText.getStyleClass().addAll(CONTENT_CLASS, SLIME_TITLE_CLASS)
    innerBreakText.getStyleClass().add(CONTENT_CLASS)
    informationText.getStyleClass().add(CONTENT_CLASS)
    $contentPane.getChildren().addAll(titleText, innerBreakText, informationText, breakText)
  }

  @VoidClosure
  private void addRelationNode(String title, List<Integer> ids, List<String> names) {
    Text formerTitleText = Text.new("cf:")
    Text titleText = Text.new("〈${title}〉" + " ")
    formerTitleText.getStyleClass().addAll(CONTENT_CLASS, SLIME_TITLE_CLASS)
    titleText.getStyleClass().addAll(CONTENT_CLASS, SLIME_TITLE_CLASS)
    $contentPane.getChildren().addAll(formerTitleText, titleText)
    for (Integer i : 0 ..< names.size()) {
      Integer id = ids[i]
      String name = names[i]
      Text nameText = Text.new(name)
      nameText.addEventHandler(MouseEvent.MOUSE_CLICKED) { MouseEvent event ->
        if ($dictionary.getOnLinkClicked() != null) {
          $dictionary.getOnLinkClicked().accept(id)
        }
      }
      nameText.getStyleClass().addAll(CONTENT_CLASS, SLIME_LINK_CLASS)
      $contentPane.getChildren().add(nameText)
      if (i < names.size() - 1) {
        Text punctuationText = Text.new(", ")
        punctuationText.getStyleClass().add(CONTENT_CLASS)
        $contentPane.getChildren().add(punctuationText)
      }      
    }
    Text breakText = Text.new("\n")
    $contentPane.getChildren().add(breakText)
  }

  public void setModifiesPunctuation(Boolean modifiesPunctuation) {
    $modifiesPunctuation = modifiesPunctuation
  }

}