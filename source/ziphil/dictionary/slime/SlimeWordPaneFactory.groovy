package ziphil.dictionary.slime

import groovy.transform.CompileStatic
import javafx.event.EventHandler
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.control.Label
import javafx.scene.control.Separator
import javafx.scene.input.MouseEvent
import javafx.scene.layout.Pane
import javafx.scene.layout.VBox
import javafx.scene.text.Text
import javafx.scene.text.TextFlow
import ziphil.custom.ElementPane
import ziphil.custom.Measurement
import ziphil.dictionary.BadgeType
import ziphil.dictionary.PaneFactoryBase
import ziphil.dictionary.SearchParameter
import ziphil.module.Setting
import ziphil.module.Strings
import ziphilib.transform.VoidClosure
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class SlimeWordPaneFactory extends PaneFactoryBase<SlimeWord, SlimeDictionary, ElementPane> {

  private static final String SLIME_HEAD_NAME_CLASS = "slime-head-name"
  private static final String SLIME_PRONUNCIATION_CLASS = "slime-pronunciation"
  private static final String SLIME_TAG_CLASS = "slime-tag"
  private static final String SLIME_EQUIVALENT_CLASS = "slime-equivalent"
  private static final String SLIME_EQUIVALENT_TITLE_CLASS = "slime-equivalent-title"
  private static final String SLIME_INFORMATION_TITLE_CLASS = "slime-information-title"
  private static final String SLIME_PLAIN_INFORMATION_TITLE_CLASS = "slime-plain-information-title"
  private static final String SLIME_RELATION_TITLE_CLASS = "slime-relation-title"
  private static final String SLIME_MARKER_CLASS = "slime-marker"
  private static final String SLIME_LINK_CLASS = "slime-link"

  public SlimeWordPaneFactory(SlimeWord word, SlimeDictionary dictionary, Boolean persisted) {
    super(word, dictionary, persisted)
  }

  public SlimeWordPaneFactory(SlimeWord word, SlimeDictionary dictionary) {
    super(word, dictionary)
  }

  protected ElementPane doCreate() {
    Setting setting = Setting.getInstance()
    Int lineSpacing = setting.getLineSpacing()
    Boolean showsVariation = setting.getShowsVariation()
    VBox pane = VBox.new()
    Map<BadgeType, Node> badgeNodes = EnumMap.new(BadgeType)
    TextFlow mainPane = TextFlow.new()
    TextFlow contentPane = TextFlow.new()
    TextFlow relationPane = TextFlow.new()
    Boolean hasContent = false
    Boolean hasRelation = false
    pane.getStyleClass().add(CONTENT_PANE_CLASS)
    mainPane.setLineSpacing(lineSpacing)
    contentPane.setLineSpacing(lineSpacing)
    addBadgeNodes(mainPane, badgeNodes)
    addNameNode(mainPane, $word.getName(), $word.createPronunciation())
    addTagNode(mainPane, $word.getTags())
    for (SlimeEquivalent equivalent : $word.getRawEquivalents()) {
      addEquivalentNode(mainPane, equivalent.getTitle(), equivalent.getNames())
    }
    for (SlimeInformation information : $word.sortedInformations()) {
      addInformationNode(contentPane, information.getTitle(), information.getText())
      hasContent = true
    }
    if (showsVariation) {
      for (Map.Entry<String, List<SlimeVariation>> entry : $word.groupedVariations()) {
        String title = entry.getKey()
        List<SlimeVariation> variationGroup = entry.getValue()
        List<String> names = variationGroup.collect{it.getName()}
        addVariationNode(relationPane, title, names)
        hasRelation = true
      }
    }
    for (Map.Entry<String, List<SlimeRelation>> entry : $word.groupedRelations()) {
      String title = entry.getKey()
      List<SlimeRelation> relationGroup = entry.getValue()
      List<IntegerClass> ids = relationGroup.collect{it.getId()}
      List<String> names = relationGroup.collect{it.getName()}
      addRelationNode(relationPane, title, ids, names)
      hasRelation = true
    }
    modifyBreak(mainPane)
    modifyBreak(contentPane)
    modifyBreak(relationPane)
    pane.getChildren().add(mainPane)
    if (hasContent) {
      addSeparator(pane)
      pane.getChildren().add(contentPane)
    }
    if (hasRelation) {
      addSeparator(pane)
      pane.getChildren().add(relationPane)
    }
    return ElementPane.new(pane, badgeNodes)
  }

  private void addNameNode(TextFlow pane, String name, String pronunciation) {
    if (!pronunciation.isEmpty()) {
      Text nameText = Text.new(name + " ")
      Text pronunciationText = Text.new(pronunciation)
      Text spaceText = Text.new("  ")
      nameText.getStyleClass().addAll(CONTENT_CLASS, HEAD_NAME_CLASS, SLIME_HEAD_NAME_CLASS)
      pronunciationText.getStyleClass().addAll(CONTENT_CLASS, SLIME_PRONUNCIATION_CLASS)
      spaceText.getStyleClass().addAll(CONTENT_CLASS, HEAD_NAME_CLASS, SLIME_HEAD_NAME_CLASS)
      pane.getChildren().addAll(nameText, pronunciationText, spaceText)
    } else {
      Text nameText = Text.new(name + "  ")
      nameText.getStyleClass().addAll(CONTENT_CLASS, HEAD_NAME_CLASS, SLIME_HEAD_NAME_CLASS)
      pane.getChildren().add(nameText)
    }
  }

  private void addTagNode(TextFlow pane, List<String> tags) {
    for (String tag : tags) {
      Label tagText = Label.new(tag)
      Text spaceText = Text.new(" ")
      tagText.getStyleClass().addAll(CONTENT_CLASS, SLIME_TAG_CLASS)
      spaceText.getStyleClass().addAll(CONTENT_CLASS)
      pane.getChildren().addAll(tagText, spaceText)
    }
    Text breakText = Text.new("\n")
    pane.getChildren().add(breakText)
  }

  private void addEquivalentNode(TextFlow pane, String title, List<String> equivalents) {
    Label titleText = Label.new(title)
    Text spaceText = Text.new(" ")
    Text equivalentText = Text.new(equivalents.join($dictionary.firstPunctuation()) ?: " ")
    Text breakText = Text.new("\n")
    titleText.getStyleClass().addAll(CONTENT_CLASS, SLIME_EQUIVALENT_TITLE_CLASS)
    spaceText.getStyleClass().addAll(CONTENT_CLASS, SLIME_EQUIVALENT_CLASS)
    equivalentText.getStyleClass().addAll(CONTENT_CLASS, SLIME_EQUIVALENT_CLASS)
    if (!title.isEmpty()) {
      pane.getChildren().addAll(titleText, spaceText, equivalentText, breakText)
    } else {
      pane.getChildren().addAll(equivalentText, breakText)
    }
  }

  private void addInformationNode(TextFlow pane, String title, String information) {
    Boolean modifiesPunctuation = Setting.getInstance().getModifiesPunctuation()
    String modifiedInformation = (modifiesPunctuation) ? Strings.modifyPunctuation(information) : information
    Boolean insertsBreak = !$dictionary.getPlainInformationTitles().contains(title)
    Label titleText = Label.new(title)
    Text innerBreakText = Text.new((insertsBreak) ? " \n" : " ")
    Text informationText = Text.new(modifiedInformation)
    Text breakText = Text.new("\n")
    if (insertsBreak) {
      titleText.getStyleClass().addAll(CONTENT_CLASS, SLIME_INFORMATION_TITLE_CLASS)
      innerBreakText.getStyleClass().addAll(CONTENT_CLASS, SMALL_CLASS)
    } else {
      titleText.getStyleClass().addAll(CONTENT_CLASS, SLIME_PLAIN_INFORMATION_TITLE_CLASS)
      innerBreakText.getStyleClass().addAll(CONTENT_CLASS)
    }
    informationText.getStyleClass().add(CONTENT_CLASS)
    pane.getChildren().addAll(titleText, innerBreakText, informationText, breakText)
  }

  private void addVariationNode(TextFlow pane, String title, List<String> names) {
    String marker = Setting.getInstance().getVariationMarker()
    Text markerText = Text.new(marker)
    Text markerSpaceText = Text.new(" ")
    Label titleText = Label.new(title)
    Text spaceText = Text.new(" ")
    Text breakText = Text.new("\n")
    markerText.getStyleClass().addAll(CONTENT_CLASS, SLIME_MARKER_CLASS)
    markerSpaceText.getStyleClass().addAll(CONTENT_CLASS)
    titleText.getStyleClass().addAll(CONTENT_CLASS, SLIME_RELATION_TITLE_CLASS)
    spaceText.getStyleClass().add(CONTENT_CLASS)
    if (!title.isEmpty()) {
      if (!marker.isEmpty()) {
        pane.getChildren().addAll(markerText, markerSpaceText, titleText, spaceText)
      } else {
        pane.getChildren().addAll(titleText, spaceText)
      }
    } else {
      if (!marker.isEmpty()) {
        pane.getChildren().addAll(markerText, spaceText)
      }
    }
    for (Int i = 0 ; i < names.size() ; i ++) {
      String name = names[i]
      Text nameText = Text.new(name ?: " ")
      nameText.getStyleClass().add(CONTENT_CLASS)
      pane.getChildren().add(nameText)
      if (i < names.size() - 1) {
        Text punctuationText = Text.new($dictionary.firstPunctuation())
        punctuationText.getStyleClass().add(CONTENT_CLASS)
        pane.getChildren().add(punctuationText)
      }
    }
    pane.getChildren().add(breakText)
  }

  private void addRelationNode(TextFlow pane, String title, List<IntegerClass> ids, List<String> names) {
    String marker = Setting.getInstance().getRelationMarker()
    Text markerText = Text.new(marker)
    Text markerSpaceText = Text.new(" ")
    Label titleText = Label.new(title)
    Text spaceText = Text.new(" ")
    Text breakText = Text.new("\n")
    markerText.getStyleClass().addAll(CONTENT_CLASS, SLIME_MARKER_CLASS)
    markerSpaceText.getStyleClass().addAll(CONTENT_CLASS)
    titleText.getStyleClass().addAll(CONTENT_CLASS, SLIME_RELATION_TITLE_CLASS)
    spaceText.getStyleClass().add(CONTENT_CLASS)
    if (!title.isEmpty()) {
      if (!marker.isEmpty()) {
        pane.getChildren().addAll(markerText, markerSpaceText, titleText, spaceText)
      } else {
        pane.getChildren().addAll(titleText, spaceText)
      }
    } else {
      if (!marker.isEmpty()) {
        pane.getChildren().addAll(markerText, spaceText)
      }
    }
    for (Int i = 0 ; i < names.size() ; i ++) {
      Int id = ids[i]
      String name = names[i]
      Text nameText = Text.new(name ?: " ")
      nameText.addEventHandler(MouseEvent.MOUSE_CLICKED, createLinkEventHandler(id))
      nameText.getStyleClass().addAll(CONTENT_CLASS, SLIME_LINK_CLASS)
      pane.getChildren().add(nameText)
      if (i < names.size() - 1) {
        Text punctuationText = Text.new($dictionary.firstPunctuation())
        punctuationText.getStyleClass().add(CONTENT_CLASS)
        pane.getChildren().add(punctuationText)
      }      
    }
    pane.getChildren().add(breakText)
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

  private EventHandler<MouseEvent> createLinkEventHandler(Int id) {
    EventHandler<MouseEvent> handler = { MouseEvent event ->
      if ($dictionary.getOnLinkClicked() != null) {
        if ($linkClickType != null && $linkClickType.matches(event)) {
          SearchParameter parameter = SlimeSearchParameter.new(id)
          $dictionary.getOnLinkClicked().accept(parameter)
        }
      }
    }
    return handler
  }

}