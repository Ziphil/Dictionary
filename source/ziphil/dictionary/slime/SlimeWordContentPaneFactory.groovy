package ziphil.dictionary.slime

import groovy.transform.CompileStatic
import java.util.Map.Entry
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.control.Label
import javafx.scene.input.MouseEvent
import javafx.scene.layout.Pane
import javafx.scene.text.Text
import javafx.scene.text.TextFlow
import ziphil.custom.Measurement
import ziphil.dictionary.ContentPaneFactoryBase
import ziphil.module.Strings
import ziphil.module.akrantiain.Akrantiain
import ziphil.module.akrantiain.AkrantiainException
import ziphilib.transform.VoidClosure
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class SlimeWordContentPaneFactory extends ContentPaneFactoryBase<SlimeWord, SlimeDictionary> {

  private static final String SLIME_HEAD_NAME_CLASS = "slime-head-name"
  private static final String SLIME_PRONUNCIATION_CLASS = "slime-pronunciation"
  private static final String SLIME_TAG_CLASS = "slime-tag"
  private static final String SLIME_EQUIVALENT_CLASS = "slime-equivalent"
  private static final String SLIME_EQUIVALENT_TITLE_CLASS = "slime-equivalent-title"
  private static final String SLIME_RELATION_TITLE_CLASS = "slime-relation-title"
  private static final String SLIME_TITLE_CLASS = "slime-title"
  private static final String SLIME_LINK_CLASS = "slime-link"

  public SlimeWordContentPaneFactory(SlimeWord word, SlimeDictionary dictionary) {
    super(word, dictionary)
  }

  public Pane create() {
    TextFlow contentPane = TextFlow.new()
    Boolean hasInformation = false
    Boolean hasRelation = false
    contentPane.getStyleClass().add(CONTENT_PANE_CLASS)
    contentPane.setLineSpacing($lineSpacing)
    addNameNode(contentPane, $word.getName())
    addTagNode(contentPane, $word.getTags())
    for (SlimeEquivalent equivalent : $word.getRawEquivalents()) {
      String equivalentString = equivalent.getNames().join(", ")
      addEquivalentNode(contentPane, equivalent.getTitle(), equivalentString)
    }
    List<SlimeInformation> sortedInformations = calculateSortedInformations()
    for (SlimeInformation information : sortedInformations) {
      addInformationNode(contentPane, information.getTitle(), information.getText())
      hasInformation = true
    }
    Map<String, List<SlimeRelation>> groupedRelations = $word.getRelations().groupBy{relation -> relation.getTitle()}
    for (Entry<String, List<SlimeRelation>> entry : groupedRelations) {
      String title = entry.getKey()
      List<SlimeRelation> relationGroup = entry.getValue()
      List<Integer> ids = relationGroup.collect{relation -> relation.getId()}
      List<String> names = relationGroup.collect{relation -> relation.getName()}
      addRelationNode(contentPane, title, ids, names)
      hasRelation = true
    }
    modifyBreak(contentPane)
    return contentPane
  }

  private List<SlimeInformation> calculateSortedInformations() {
    if ($dictionary.getInformationTitleOrder() != null) {
      List<SlimeInformation> sortedInformations = $word.getInformations().toSorted() { SlimeInformation firstInformation, SlimeInformation secondInformation ->
        String firstTitle = firstInformation.getTitle()
        String secondTitle = secondInformation.getTitle()
        Integer firstIndex = $dictionary.getInformationTitleOrder().indexOf(firstTitle)
        Integer secondIndex = $dictionary.getInformationTitleOrder().indexOf(secondTitle)
        if (firstIndex == -1) {
          if (secondIndex == -1) {
            return 0
          } else {
            return -1
          }
        } else {
          if (secondIndex == -1) {
            return 1
          } else {
            return firstIndex <=> secondIndex
          }
        }
      }
      return sortedInformations
    } else {
      return $word.getInformations()
    }
  }

  private void addNameNode(TextFlow contentPane, String name) {
    Akrantiain akrantiain = $dictionary.getAkrantiain()
    String pronunciation = null
    if (akrantiain != null) {
      try {
        pronunciation = akrantiain.convert(name)
      } catch (AkrantiainException exception) {
        pronunciation = null
      }
    }
    if (pronunciation != null) {
      Text nameText = Text.new(name + " ")
      Text pronunciationText = Text.new("/" + pronunciation + "/")
      Text spaceText = Text.new("  ")
      nameText.getStyleClass().addAll(CONTENT_CLASS, HEAD_NAME_CLASS, SLIME_HEAD_NAME_CLASS)
      pronunciationText.getStyleClass().addAll(CONTENT_CLASS, SLIME_PRONUNCIATION_CLASS)
      spaceText.getStyleClass().addAll(CONTENT_CLASS, HEAD_NAME_CLASS, SLIME_HEAD_NAME_CLASS)
      contentPane.getChildren().addAll(nameText, pronunciationText, spaceText)
    } else {
      Text nameText = Text.new(name + "  ")
      nameText.getStyleClass().addAll(CONTENT_CLASS, HEAD_NAME_CLASS, SLIME_HEAD_NAME_CLASS)
      contentPane.getChildren().add(nameText)
    }
  }

  private void addTagNode(TextFlow contentPane, List<String> tags) {
    for (String tag : tags) {
      Label tagText = Label.new(tag)
      Text spaceText = Text.new(" ")
      tagText.getStyleClass().addAll(CONTENT_CLASS, SLIME_TAG_CLASS)
      spaceText.getStyleClass().addAll(CONTENT_CLASS, SLIME_TITLE_CLASS)
      contentPane.getChildren().addAll(tagText, spaceText)
    }
    Text breakText = Text.new("\n")
    contentPane.getChildren().add(breakText)
  }

  private void addEquivalentNode(TextFlow contentPane, String title, String equivalent) {
    Label titleText = Label.new(title)
    Text equivalentText = Text.new(" " + equivalent)
    Text breakText = Text.new("\n")
    titleText.getStyleClass().addAll(CONTENT_CLASS, SLIME_EQUIVALENT_TITLE_CLASS)
    equivalentText.getStyleClass().addAll(CONTENT_CLASS, SLIME_EQUIVALENT_CLASS)
    contentPane.getChildren().addAll(titleText, equivalentText, breakText)
  }

  private void addInformationNode(TextFlow contentPane, String title, String information) {
    String modifiedInformation = ($modifiesPunctuation) ? Strings.modifyPunctuation(information) : information
    Boolean insertsBreak = !$dictionary.getPlainInformationTitles().contains(title)
    Text titleText = Text.new("【${title}】")
    Text innerBreakText = Text.new((insertsBreak) ? " \n" : " ")
    Text informationText = Text.new(modifiedInformation)
    Text breakText = Text.new("\n")
    titleText.getStyleClass().addAll(CONTENT_CLASS, SLIME_TITLE_CLASS)
    innerBreakText.getStyleClass().add(CONTENT_CLASS)
    informationText.getStyleClass().add(CONTENT_CLASS)
    contentPane.getChildren().addAll(titleText, innerBreakText, informationText, breakText)
  }

  @VoidClosure
  private void addRelationNode(TextFlow contentPane, String title, List<Integer> ids, List<String> names) {
    Text formerTitleText = Text.new("cf:")
    Label titleText = Label.new(title)
    Text spaceText = Text.new(" ")
    formerTitleText.getStyleClass().addAll(CONTENT_CLASS, SLIME_TITLE_CLASS)
    titleText.getStyleClass().addAll(CONTENT_CLASS, SLIME_RELATION_TITLE_CLASS)
    spaceText.getStyleClass().addAll(CONTENT_CLASS, SLIME_TITLE_CLASS)
    if (title != "") {
      contentPane.getChildren().addAll(formerTitleText, titleText, spaceText)
    } else {
      contentPane.getChildren().addAll(formerTitleText, spaceText)
    }
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
      contentPane.getChildren().add(nameText)
      if (i < names.size() - 1) {
        Text punctuationText = Text.new(", ")
        punctuationText.getStyleClass().add(CONTENT_CLASS)
        contentPane.getChildren().add(punctuationText)
      }      
    }
    Text breakText = Text.new("\n")
    contentPane.getChildren().add(breakText)
  }

}