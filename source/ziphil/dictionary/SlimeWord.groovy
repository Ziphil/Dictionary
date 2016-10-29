package ziphil.dictionary

import groovy.transform.CompileStatic
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.control.Label
import javafx.scene.input.MouseEvent
import javafx.scene.layout.HBox
import javafx.scene.layout.Pane
import javafx.scene.layout.VBox
import javafx.scene.text.Text
import javafx.scene.text.TextFlow
import ziphil.custom.Measurement
import ziphil.module.Setting
import ziphil.module.Strings
import ziphilib.transform.ReturnVoidClosure


@CompileStatic @Newify
public class SlimeWord extends Word {

  public static final String SLIME_HEAD_NAME_CLASS = "slime-head-name"
  public static final String SLIME_TAG_CLASS = "slime-tag"
  public static final String SLIME_EQUIVALENT_CLASS = "slime-equivalent"
  public static final String SLIME_EQUIVALENT_TITLE_CLASS = "slime-equivalent-title"
  public static final String SLIME_TITLE_CLASS = "slime-title"
  public static final String SLIME_LINK_CLASS = "slime-link"
  public static final String SLIME_ID_CLASS = "slime-id"

  private SlimeDictionary $dictionary
  private Integer $id = -1
  private List<SlimeEquivalent> $rawEquivalents = ArrayList.new()
  private List<String> $tags = ArrayList.new()
  private List<SlimeInformation> $informations = ArrayList.new()
  private List<SlimeVariation> $variations = ArrayList.new()
  private List<SlimeRelation> $relations = ArrayList.new()
  private String $comparisonString = ""
  private VBox $simpleContentPane = VBox.new()
  private Boolean $isSimpleChanged = true

  public SlimeWord(Integer id, String name, List<SlimeEquivalent> rawEquivalents, List<String> tags, List<SlimeInformation> informations, List<SlimeVariation> variations,
                   List<SlimeRelation> relations) {
    update(id, name, rawEquivalents, tags, informations, variations, relations)
    setupContentPanes()
  }

  public SlimeWord() {
    setupContentPanes()
  }

  public void update(Integer id, String name, List<SlimeEquivalent> rawEquivalents, List<String> tags, List<SlimeInformation> informations, List<SlimeVariation> variations,
                     List<SlimeRelation> relations) {
    $id = id
    $name = name
    $rawEquivalents = rawEquivalents
    $equivalents = (List)rawEquivalents.inject([]) { List<String> result, SlimeEquivalent equivalent ->
      result.addAll(equivalent.getNames())
      return result
    }
    $tags = tags
    $informations = informations
    $variations = variations
    $relations = relations
    $isChanged = true
    $isSimpleChanged = true
  }

  public void createContentPane() {
    HBox headBox = HBox.new()
    VBox equivalentBox = VBox.new()
    VBox informationBox = VBox.new()
    VBox relationBox = VBox.new()
    Boolean hasInformation = false
    Boolean hasRelation = false
    Boolean modifiesPunctuation = Setting.getInstance().getModifiesPunctuation() ?: false
    $contentPane.getChildren().clear()
    $contentPane.getChildren().addAll(headBox, equivalentBox, informationBox, relationBox)
    addNameNode(headBox, $name)
    addTagNode(headBox, $tags)
    $rawEquivalents.each() { SlimeEquivalent equivalent ->
      String equivalentString = equivalent.getNames().join(", ")
      addEquivalentNode(equivalentBox, equivalent.getTitle(), equivalentString)
    }
    $informations.each() { SlimeInformation information ->
      addInformationNode(informationBox, information.getTitle(), information.getText(), modifiesPunctuation)
      hasInformation = true
    }
    $relations.groupBy{relation -> relation.getTitle()}.each() { String title, List<SlimeRelation> relationGroup ->
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
    $isChanged = false
  }

  public void createSimpleContentPane() {
    HBox headBox = HBox.new()
    VBox equivalentBox = VBox.new()
    $simpleContentPane.getChildren().clear()
    $simpleContentPane.getChildren().addAll(headBox, equivalentBox)
    addSimpleNameNode(headBox, $name, $id)
    addSimpleEquivalentNode(equivalentBox, $equivalents.join(", "))
    $isSimpleChanged = false
  }

  private void addNameNode(HBox box, String name) {
    Text nameText = Text.new(name + "  ")
    nameText.getStyleClass().addAll(CONTENT_CLASS, HEAD_NAME_CLASS, SLIME_HEAD_NAME_CLASS)
    box.getChildren().add(nameText)
    box.setAlignment(Pos.CENTER_LEFT)
  }

  private void addSimpleNameNode(HBox box, String name, Integer id) {
    Text nameText = Text.new($name + " ")
    Text idText = Text.new("#${$id}")
    nameText.getStyleClass().addAll(CONTENT_CLASS, HEAD_NAME_CLASS, SLIME_HEAD_NAME_CLASS)
    idText.getStyleClass().addAll(CONTENT_CLASS, SLIME_ID_CLASS)
    box.getChildren().addAll(nameText, idText)
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

  private void addSimpleEquivalentNode(VBox box, String equivalent) {
    TextFlow textFlow = TextFlow.new()
    Text equivalentText = Text.new(equivalent)
    equivalentText.getStyleClass().addAll(CONTENT_CLASS, SLIME_EQUIVALENT_CLASS)
    textFlow.getChildren().add(equivalentText)
    box.getChildren().add(textFlow)
  }

  private void addInformationNode(VBox box, String title, String information, Boolean modifiesPunctuation) {
    String newInformation = (modifiesPunctuation) ? Strings.modifyPunctuation(information) : information
    TextFlow titleTextFlow = TextFlow.new()
    TextFlow textFlow = TextFlow.new()
    Text titleText = Text.new("【${title}】")
    Text dammyText = Text.new(" ")
    Text informationText = Text.new(newInformation)
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

  public void createComparisonString(String order) {
    StringBuilder comparisonString = StringBuilder.new()
    (0 ..< $name.length()).each() { Integer i ->
      Integer position = order.indexOf($name.codePointAt(i))
      if (position > -1) {
        comparisonString.appendCodePoint(position + 174)
      } else {
        comparisonString.appendCodePoint(10000)
      }
    }
    $comparisonString = comparisonString.toString()
  }

  private void setupContentPanes() {
    $contentPane.getStyleClass().add(CONTENT_PANE_CLASS)
    $simpleContentPane.getStyleClass().add(CONTENT_PANE_CLASS)
  }

  public Boolean isSimpleChanged() {
    return $isSimpleChanged
  }

  public SlimeDictionary getDictionary() {
    return $dictionary
  }

  public void setDictionary(SlimeDictionary dictionary) {
    $dictionary = dictionary
  }

  public Integer getId() {
    return $id
  }

  public void setId(Integer id) {
    $id = id
  }

  public void setName(String name) {
    $name = name
  }

  public List<SlimeEquivalent> getRawEquivalents() {
    return $rawEquivalents
  }

  public void setRawEquivalents(List<SlimeEquivalent> rawEquivalents) {
    $rawEquivalents = rawEquivalents
    $equivalents = (List)rawEquivalents.inject([]) { List<String> result, SlimeEquivalent equivalent ->
      result.addAll(equivalent.getNames())
      return result
    }
  }

  public List<String> getTags() {
    return $tags
  }

  public void setTags(List<String> tags) {
    $tags = tags
  }

  public List<SlimeInformation> getInformations() {
    return $informations
  }

  public void setInformations(List<SlimeInformation> informations) {
    $informations = informations
  }

  public List<SlimeVariation> getVariations() {
    return $variations
  }

  public void setVariations(List<SlimeVariation> variations) {
    $variations = variations
  }

  public List<SlimeRelation> getRelations() {
    return $relations
  }

  public void setRelations(List<SlimeRelation> relations) {
    $relations = relations
  }

  public String getComparisonString() {
    return $comparisonString
  }

  public Pane getSimpleContentPane() {
    return $simpleContentPane
  }

}