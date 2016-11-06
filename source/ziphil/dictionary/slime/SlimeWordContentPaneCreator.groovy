package ziphil.dictionary.slime

import groovy.transform.CompileStatic
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.control.Label
import javafx.scene.input.MouseEvent
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import javafx.scene.text.Text
import javafx.scene.text.TextFlow
import ziphil.custom.Measurement
import ziphil.dictionary.ContentPaneCreator
import ziphil.module.Strings
import ziphilib.transform.ReturnVoidClosure


@CompileStatic @Newify
public class SlimeWordContentPaneCreator extends ContentPaneCreator<SlimeWord, SlimeDictionary> {

  private static final String SLIME_HEAD_NAME_CLASS = "slime-head-name"
  private static final String SLIME_TAG_CLASS = "slime-tag"
  private static final String SLIME_EQUIVALENT_CLASS = "slime-equivalent"
  private static final String SLIME_EQUIVALENT_TITLE_CLASS = "slime-equivalent-title"
  private static final String SLIME_TITLE_CLASS = "slime-title"
  private static final String SLIME_LINK_CLASS = "slime-link"

  public SlimeWordContentPaneCreator(VBox contentPane, SlimeWord word, SlimeDictionary dictionary) {
    super(contentPane, word, dictionary)
  }

  public void create() {
    HBox headBox = HBox.new()
    VBox equivalentBox = VBox.new()
    VBox informationBox = VBox.new()
    VBox relationBox = VBox.new()
    Boolean hasInformation = false
    Boolean hasRelation = false
    $contentPane.getStyleClass().clear()
    $contentPane.getStyleClass().add(CONTENT_PANE_CLASS)
    $contentPane.getChildren().clear()
    $contentPane.getChildren().addAll(headBox, equivalentBox, informationBox, relationBox)
    addNameNode(headBox, $word.getName())
    addTagNode(headBox, $word.getTags())
    $word.getRawEquivalents().each() { SlimeEquivalent equivalent ->
      String equivalentString = equivalent.getNames().join(", ")
      addEquivalentNode(equivalentBox, equivalent.getTitle(), equivalentString)
    }
    $word.getInformations().each() { SlimeInformation information ->
      addInformationNode(informationBox, information.getTitle(), information.getText())
      hasInformation = true
    }
    $word.getRelations().groupBy{relation -> relation.getTitle()}.each() { String title, List<SlimeRelation> relationGroup ->
      List<Integer> ids = relationGroup.collect{relation -> relation.getId()}
      List<String> names = relationGroup.collect{relation -> relation.getName()}
      addRelationNode(relationBox, title, ids, names)
      hasRelation = true
    }
    if (hasInformation) {
      $contentPane.setMargin(equivalentBox, Insets.new(0, 0, Measurement.rpx(3), 0))
    }
    if (hasRelation) {
      $contentPane.setMargin(informationBox, Insets.new(0, 0, Measurement.rpx(3), 0))
    }
  }

  private void addNameNode(HBox box, String name) {
    Text nameText = Text.new(name + "  ")
    nameText.getStyleClass().addAll(CONTENT_CLASS, HEAD_NAME_CLASS, SLIME_HEAD_NAME_CLASS)
    box.getChildren().add(nameText)
    box.setAlignment(Pos.CENTER_LEFT)
  }

  private void addTagNode(HBox box, List<String> tags) {
    tags.each() { String tag ->
      Label tagText = Label.new(tag)
      Text spaceText = Text.new(" ")
      tagText.getStyleClass().addAll(CONTENT_CLASS, SLIME_TAG_CLASS)
      spaceText.getStyleClass().addAll(CONTENT_CLASS, SLIME_TITLE_CLASS)
      box.getChildren().addAll(tagText, spaceText)
    }
  }

  private void addEquivalentNode(VBox box, String title, String equivalent) {
    TextFlow textFlow = TextFlow.new()
    Label titleText = Label.new(title)
    Text equivalentText = Text.new(" " + equivalent)
    titleText.getStyleClass().addAll(CONTENT_CLASS, SLIME_EQUIVALENT_TITLE_CLASS)
    equivalentText.getStyleClass().addAll(CONTENT_CLASS, SLIME_EQUIVALENT_CLASS)
    textFlow.getChildren().addAll(titleText, equivalentText)
    box.getChildren().add(textFlow)
  }

  private void addInformationNode(VBox box, String title, String information) {
    String modifiedInformation = ($modifiesPunctuation) ? Strings.modifyPunctuation(information) : information
    TextFlow titleTextFlow = TextFlow.new()
    TextFlow textFlow = TextFlow.new()
    Text titleText = Text.new("【${title}】")
    Text dammyText = Text.new(" ")
    Text informationText = Text.new(modifiedInformation)
    titleText.getStyleClass().addAll(CONTENT_CLASS, SLIME_TITLE_CLASS)
    informationText.getStyleClass().add(CONTENT_CLASS)
    titleTextFlow.getChildren().addAll(titleText, dammyText)
    textFlow.getChildren().add(informationText)
    box.getChildren().addAll(titleTextFlow, textFlow)
  }

  @ReturnVoidClosure
  private void addRelationNode(VBox box, String title, List<Integer> ids, List<String> names) {
    TextFlow textFlow = TextFlow.new()
    Text formerTitleText = Text.new("cf:")
    Text titleText = Text.new("〈${title}〉" + " ")
    formerTitleText.getStyleClass().addAll(CONTENT_CLASS, SLIME_TITLE_CLASS)
    titleText.getStyleClass().addAll(CONTENT_CLASS, SLIME_TITLE_CLASS)
    textFlow.getChildren().addAll(formerTitleText, titleText)
    (0 ..< names.size()).each() { Integer i ->
      Integer id = ids[i]
      String name = names[i]
      Text nameText = Text.new(name)
      nameText.addEventHandler(MouseEvent.MOUSE_CLICKED) { MouseEvent event ->
        if ($dictionary.getOnLinkClicked() != null) {
          $dictionary.getOnLinkClicked().accept(id)
        }
      }
      nameText.getStyleClass().addAll(CONTENT_CLASS, SLIME_LINK_CLASS)
      textFlow.getChildren().add(nameText)
      if (i < names.size() - 1) {
        Text punctuationText = Text.new(", ")
        punctuationText.getStyleClass().add(CONTENT_CLASS)
        textFlow.getChildren().add(punctuationText)
      }      
    }
    box.getChildren().add(textFlow)
  }

  public void setModifiesPunctuation(Boolean modifiesPunctuation) {
    $modifiesPunctuation = modifiesPunctuation
  }

}