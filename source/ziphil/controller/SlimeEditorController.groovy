package ziphil.controller

import groovy.transform.CompileStatic
import javafx.application.Platform
import javafx.event.EventTarget
import javafx.fxml.FXML
import javafx.geometry.Bounds
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.stage.Modality
import javafx.scene.control.Button
import javafx.scene.control.ComboBox
import javafx.scene.control.Label
import javafx.scene.control.ScrollPane
import javafx.scene.control.TextArea
import javafx.scene.control.TextField
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyCodeCombination
import javafx.scene.input.KeyCombination
import javafx.scene.input.KeyEvent
import javafx.scene.layout.GridPane
import javafx.scene.layout.HBox
import javafx.scene.layout.Priority
import javafx.scene.layout.VBox
import javafx.stage.StageStyle
import ziphil.custom.Dialog
import ziphil.custom.Measurement
import ziphil.custom.UtilityStage
import ziphil.dictionary.SlimeDictionary
import ziphil.dictionary.SlimeEquivalent
import ziphil.dictionary.SlimeInformation
import ziphil.dictionary.SlimeRelation
import ziphil.dictionary.SlimeVariation
import ziphil.dictionary.SlimeWord
import ziphil.module.Setting


@CompileStatic @Newify
public class SlimeEditorController extends Controller<Boolean> {

  private static final String RESOURCE_PATH = "resource/fxml/slime_editor.fxml"
  private static final String TITLE = "単語編集"
  private static final Double DEFAULT_WIDTH = Measurement.rpx(640)
  private static final Double DEFAULT_HEIGHT = Measurement.rpx(640)

  @FXML private TextField $id
  @FXML private TextField $name
  @FXML private ScrollPane $scrollPane
  @FXML private GridPane $gridPane
  @FXML private VBox $tagBox
  @FXML private VBox $equivalentBox
  @FXML private VBox $informationBox
  @FXML private VBox $variationBox
  @FXML private VBox $relationBox
  @FXML private Label $idLabel
  private List<ComboBox<String>> $tags = ArrayList.new()
  private List<ComboBox<String>> $equivalentTitles = ArrayList.new()
  private List<TextField> $equivalentNames = ArrayList.new()
  private List<ComboBox<String>> $informationTitles = ArrayList.new()
  private List<TextArea> $informationTexts = ArrayList.new()
  private List<ComboBox<String>> $variationTitles = ArrayList.new()
  private List<TextField> $variationNames = ArrayList.new()
  private List<SlimeRelation> $relations = ArrayList.new()
  private List<ComboBox<String>> $relationTitles = ArrayList.new()
  private List<TextField> $relationNames = ArrayList.new()
  private SlimeWord $word
  private SlimeDictionary $dictionary

  public SlimeEditorController(UtilityStage<Boolean> stage) {
    super(stage)
    loadResource(RESOURCE_PATH, TITLE, DEFAULT_WIDTH, DEFAULT_HEIGHT)
    setupShortcuts()
  }

  @FXML
  private void initialize() {
    setupIdControl()
  }

  public void prepare(SlimeWord word, SlimeDictionary dictionary) {
    $word = word
    $dictionary = dictionary
    $id.setText(word.getId().toString())
    $name.setText(word.getName())
    word.getTags().each() { String tag ->
      addTagControl(tag, dictionary.registeredTags())
    }
    word.getRawEquivalents().each() { SlimeEquivalent equivalent ->
      String nameString = equivalent.getNames().join(", ")
      addEquivalentControl(equivalent.getTitle(), nameString, dictionary.registeredEquivalentTitles())
    }
    word.getInformations().each() { SlimeInformation information ->
      addInformationControl(information.getTitle(), information.getText(), dictionary.registeredInformationTitles())
    }
    word.getVariations().groupBy{variation -> variation.getTitle()}.each() { String title, List<SlimeVariation> variationGroup ->
      String nameString = variationGroup.collect{variation -> variation.getName()}.join(", ")
      addVariationControl(title, nameString, dictionary.registeredVariationTitles())
    }
    word.getRelations().each() { SlimeRelation relation ->
      addRelationControl(relation.getTitle(), relation.getName(), relation, dictionary.registeredRelationTitles())
    }
    if ($informationTexts.isEmpty()) {
      insertInformationControl()
    }
    Platform.runLater() {
      $informationTexts[0].requestFocus()
    }
  }

  @FXML
  protected void commit() {
    Boolean ignoresDuplicateSlimeId = Setting.getInstance().getIgnoresDuplicateSlimeId()
    Integer id = $id.getText().toInteger()
    if (ignoresDuplicateSlimeId || !$dictionary.containsId(id, $word)) {
      String name = $name.getText()
      List<SlimeEquivalent> rawEquivalents = ArrayList.new()
      List<String> tags = ArrayList.new()
      List<SlimeInformation> informations = ArrayList.new()
      List<SlimeVariation> variations = ArrayList.new()
      List<SlimeRelation> relations = ArrayList.new()
      (0 ..< $tags.size()).each() { Integer i ->
        String tag = $tags[i].getValue()
        if (tag != "") {
          tags.add(tag)
        }
      }
      (0 ..< $equivalentTitles.size()).each() { Integer i ->
        String title = $equivalentTitles[i].getValue()
        List<String> equivalentNames = $equivalentNames[i].getText().split(/\s*(,|、)\s*/).toList()
        if (!equivalentNames.isEmpty()) {
          rawEquivalents.add(SlimeEquivalent.new(title, equivalentNames))
        }
      }
      (0 ..< $informationTitles.size()).each() { Integer i ->
        String title = $informationTitles[i].getValue()
        String text = $informationTexts[i].getText()
        if (text != "") {
          informations.add(SlimeInformation.new(title, text))
        }
      }
      (0 ..< $variationTitles.size()).each() { Integer i ->
        String title = $variationTitles[i].getValue()
        List<String> variationNames = $variationNames[i].getText().split(/\s*(,|、)\s*/).toList()
        variationNames.each() { String variationName ->
          if (variationName != "") {
            variations.add(SlimeVariation.new(title, variationName))
          }
        }
      }
      (0 ..< $relationTitles.size()).each() { Integer i ->
        String title = $relationTitles[i].getValue()
        SlimeRelation relation = $relations[i]
        if (relation != null) {
          relations.add(SlimeRelation.new(title, relation.getId(), relation.getName()))
        }
      }
      $word.update(id, name, rawEquivalents, tags, informations, variations, relations)
      $stage.close(true)
    } else {
      Dialog dialog = Dialog.new("重複IDエラー", "このIDはすでに利用されています。別のIDを指定してください。")
      dialog.initOwner($stage)
      dialog.setAllowsCancel(false)
      dialog.showAndWait()
    }
  }

  @FXML
  private void insertTagControl() {
    addTagControl("", $dictionary.registeredTags())
    $tags[-1].requestFocus()
  }

  @FXML
  private void insertEquivalentControl() {
    addEquivalentControl("", "", $dictionary.registeredEquivalentTitles())
    $equivalentNames[-1].requestFocus()
  }

  @FXML
  private void insertInformationControl() {
    addInformationControl("", "", $dictionary.registeredInformationTitles())
    $informationTexts[-1].requestFocus()
  }

  @FXML
  private void insertVariationControl() {
    addVariationControl("", "", $dictionary.registeredVariationTitles())
    $variationNames[-1].requestFocus()
  }

  @FXML
  private void insertRelationControl() {
    addRelationControl("", "", null, $dictionary.registeredRelationTitles())
    chooseRelation((HBox)$relationBox.getChildren()[-1])
  }

  private void removeTagControl(HBox box) {
    Integer index = $tagBox.getChildren().indexOf(box)
    if (index >= 0) {
      $tagBox.getChildren().removeAt(index)
      $tags.removeAt(index)
    }
  }

  private void removeEquivalentControl(HBox box) {
    Integer index = $equivalentBox.getChildren().indexOf(box)
    if (index >= 0) {
      $equivalentBox.getChildren().removeAt(index)
      $equivalentTitles.removeAt(index)
      $equivalentNames.removeAt(index)
    }
  }

  private void removeInformationControl(HBox box) {
    Integer index = $informationBox.getChildren().indexOf(box)
    if (index >= 0) {
      $informationBox.getChildren().removeAt(index)
      $informationTitles.removeAt(index)
      $informationTexts.removeAt(index)
    }
  }

  private void removeVariationControl(HBox box) {
    Integer index = $variationBox.getChildren().indexOf(box)
    if (index >= 0) {
      $variationBox.getChildren().removeAt(index)
      $variationTitles.removeAt(index)
      $variationNames.removeAt(index)
    }
  }

  private void removeRelationControl(HBox box) {
    Integer index = $relationBox.getChildren().indexOf(box)
    if (index >= 0) {
      $relationBox.getChildren().removeAt(index)
      $relationTitles.removeAt(index)
      $relationNames.removeAt(index)
    }
  }

  private void focusName() {
    $name.requestFocus()
    scrollToNode($name)
  }

  private void focusTagControl(EventTarget target) {
    Integer index = $tags.indexOf(target)
    if (index >= 0) {
      Integer nextIndex = (index < $tags.size() - 1) ? index + 1 : 0
      $tags[nextIndex].requestFocus()
      scrollToNode($tags[nextIndex])
    } else {
      if ($tags.isEmpty()) {
        insertTagControl()
      }
      $tags[0].requestFocus()
      scrollToNode($tags[0])
    }
  }

  private void focusEquivalentControl(EventTarget target) {
    Integer index = $equivalentNames.indexOf(target)
    if (index >= 0) {
      Integer nextIndex = (index < $equivalentNames.size() - 1) ? index + 1 : 0
      $equivalentNames[nextIndex].requestFocus()
      scrollToNode($equivalentNames[nextIndex])
    } else {
      if ($equivalentNames.isEmpty()) {
        insertEquivalentControl()
      }
      $equivalentNames[0].requestFocus()
      scrollToNode($equivalentNames[0])
    }
  }

  private void focusInformationControl(EventTarget target) {
    Integer index = $informationTexts.indexOf(target)
    if (index >= 0) {
      Integer nextIndex = (index < $informationTexts.size() - 1) ? index + 1 : 0
      $informationTexts[nextIndex].requestFocus()
      scrollToNode($informationTexts[nextIndex])
    } else {
      if ($informationTexts.isEmpty()) {
        insertInformationControl()
      }
      $informationTexts[0].requestFocus()
      scrollToNode($informationTexts[0])
    }
  }

  private void focusVariationControl(EventTarget target) {
    Integer index = $variationNames.indexOf(target)
    if (index >= 0) {
      Integer nextIndex = (index < $variationNames.size() - 1) ? index + 1 : 0
      $variationNames[nextIndex].requestFocus()
      scrollToNode($variationNames[nextIndex])
    } else {
      if ($variationNames.isEmpty()) {
        insertVariationControl()
      }
      $variationNames[0].requestFocus()
      scrollToNode($variationNames[0])
    }
  }

  private void chooseRelation(HBox box) {
    UtilityStage<SlimeWord> stage = UtilityStage.new(StageStyle.UTILITY)
    SlimeWordChooserController controller = SlimeWordChooserController.new(stage)
    stage.initModality(Modality.WINDOW_MODAL)
    stage.initOwner($stage)
    controller.prepare($dictionary.copy())
    SlimeWord word = stage.showAndWaitResult()
    if (word != null) {
      Integer index = $relationBox.getChildren().indexOf(box)
      if (index >= 0) {
        $relations[index] = SlimeRelation.new(null, word.getId(), word.getName())
        $relationNames[index].setText(word.getName())
      }
    }
  }

  private void addTagControl(String tagString, List<String> registeredTags) {
    HBox box = HBox.new(Measurement.rpx(5))
    ComboBox<String> tag = ComboBox.new()
    Button remove = Button.new("－")
    tag.setEditable(true)
    tag.getItems().addAll(registeredTags)
    tag.setValue(tagString)
    tag.setPrefWidth(Measurement.rpx(120))
    tag.setMinWidth(Measurement.rpx(120))
    remove.setOnAction() {
      removeTagControl(box)
    }
    box.getChildren().addAll(tag, remove)
    $tags.add(tag)
    $tagBox.getChildren().add(box)
  }

  private void addEquivalentControl(String titleString, String nameString, List<String> registeredTitles) {
    HBox box = HBox.new(Measurement.rpx(5))
    ComboBox<String> title = ComboBox.new()
    TextField name = TextField.new()
    Button remove = Button.new("－")
    title.setEditable(true)
    title.getItems().addAll(registeredTitles)
    title.setValue(titleString)
    title.setPrefWidth(Measurement.rpx(120))
    title.setMinWidth(Measurement.rpx(120))
    name.setText(nameString)
    remove.setOnAction() {
      removeEquivalentControl(box)
    }
    box.getChildren().addAll(title, name, remove)
    box.setHgrow(name, Priority.ALWAYS)
    $equivalentTitles.add(title)
    $equivalentNames.add(name)
    $equivalentBox.getChildren().add(box)
    $equivalentBox.setVgrow(box, Priority.ALWAYS)
  }

  private void addInformationControl(String titleString, String textString, List<String> registeredTitles) {
    HBox box = HBox.new(Measurement.rpx(5))
    HBox removeBox = HBox.new()
    ComboBox<String> title = ComboBox.new()
    TextArea text = TextArea.new()
    Button remove = Button.new("－")
    removeBox.setAlignment(Pos.BOTTOM_CENTER)
    title.setEditable(true)
    title.getItems().addAll(registeredTitles)
    title.setValue(titleString)
    title.setPrefWidth(Measurement.rpx(120))
    title.setMinWidth(Measurement.rpx(120))
    text.setWrapText(true)
    text.getStyleClass().add("editor")
    text.setText(textString)
    text.setPrefHeight(Measurement.rpx(120))
    text.setMinHeight(Measurement.rpx(120))
    remove.setOnAction() {
      removeInformationControl(box)
    }
    removeBox.getChildren().add(remove)
    box.getChildren().addAll(title, text, removeBox)
    box.setHgrow(text, Priority.ALWAYS)
    $informationTitles.add(title)
    $informationTexts.add(text)
    $informationBox.getChildren().add(box)
    $informationBox.setVgrow(box, Priority.ALWAYS)
  }

  private void addVariationControl(String titleString, String nameString, List<String> registeredTitles) {
    HBox box = HBox.new(Measurement.rpx(5))
    ComboBox<String> title = ComboBox.new()
    TextField name = TextField.new()
    Button remove = Button.new("－")
    title.setEditable(true)
    title.getItems().addAll(registeredTitles)
    title.setValue(titleString)
    title.setPrefWidth(Measurement.rpx(120))
    title.setMinWidth(Measurement.rpx(120))
    name.setText(nameString)
    remove.setOnAction() {
      removeVariationControl(box)
    }
    box.getChildren().addAll(title, name, remove)
    box.setHgrow(name, Priority.ALWAYS)
    $variationTitles.add(title)
    $variationNames.add(name)
    $variationBox.getChildren().add(box)
    $variationBox.setVgrow(box, Priority.ALWAYS)
  }

  private void addRelationControl(String titleString, String nameString, SlimeRelation relation, List<String> registeredTitles) {
    HBox box = HBox.new(Measurement.rpx(5))
    HBox nameBox = HBox.new()
    ComboBox<String> title = ComboBox.new()
    TextField name = TextField.new()
    Button choose = Button.new("…")
    Button remove = Button.new("－")
    title.setEditable(true)
    title.getItems().addAll(registeredTitles)
    title.setValue(titleString)
    title.setPrefWidth(Measurement.rpx(120))
    title.setMinWidth(Measurement.rpx(120))
    name.setEditable(false)
    name.setText(nameString)
    name.setPrefWidth(Measurement.rpx(150))
    name.setMinWidth(Measurement.rpx(150))
    name.getStyleClass().add("left-pill")
    choose.getStyleClass().add("right-pill")
    choose.setOnAction() {
      chooseRelation(box)
    }
    remove.setOnAction() {
      removeRelationControl(box)
    }
    nameBox.getChildren().addAll(name, choose)
    box.getChildren().addAll(title, nameBox, remove)
    box.setHgrow(name, Priority.ALWAYS)
    $relations.add(relation)
    $relationTitles.add(title)
    $relationNames.add(name)
    $relationBox.getChildren().add(box)
    $relationBox.setVgrow(box, Priority.ALWAYS)
  }

  private void scrollToNode(Node node) {
    Node content = $scrollPane.getContent()
    Double viewportHeight = $scrollPane.getViewportBounds().getHeight()
    Double paneMinY = $scrollPane.localToScene($scrollPane.getBoundsInLocal()).getMinY()
    Double contentMinY = content.localToScene(content.getBoundsInLocal()).getMinY()
    Double contentHeight = content.getBoundsInLocal().getHeight()
    Double nodeMinY = node.localToScene(node.getBoundsInLocal()).getMinY()
    Double nodeMaxY = node.localToScene(node.getBoundsInLocal()).getMaxY()
    Double nodeHeight = node.getBoundsInLocal().getHeight()
    Double nodeRelativeMinY = nodeMinY - contentMinY
    Double nodeRelativeMaxY = nodeMaxY - contentMinY
    Double nodeAbsoluteMinY = nodeMinY - paneMinY
    Double nodeAbsoluteMaxY = nodeMaxY - paneMinY
    if (nodeAbsoluteMinY < 0) {
      Double vvalue = (Double)(nodeRelativeMinY / (contentHeight - viewportHeight))
      $scrollPane.setVvalue(vvalue)
    } else if (nodeAbsoluteMaxY > viewportHeight) {
      Double vvalue = (Double)((nodeRelativeMaxY - viewportHeight) / (contentHeight - viewportHeight))
      $scrollPane.setVvalue(vvalue)
    }
  }

  private void setupShortcuts() {
    $scene.setOnKeyPressed() { KeyEvent event ->
      if (KeyCodeCombination.new(KeyCode.W, KeyCombination.SHORTCUT_DOWN).match(event)) {
        focusName()
      } else if (KeyCodeCombination.new(KeyCode.T, KeyCombination.SHORTCUT_DOWN).match(event)) {
        focusTagControl(event.getTarget())
      } else if (KeyCodeCombination.new(KeyCode.E, KeyCombination.SHORTCUT_DOWN).match(event)) {
        focusEquivalentControl(event.getTarget())
      } else if (KeyCodeCombination.new(KeyCode.I, KeyCombination.SHORTCUT_DOWN).match(event)) {
        focusInformationControl(event.getTarget())
      } else if (KeyCodeCombination.new(KeyCode.D, KeyCombination.SHORTCUT_DOWN).match(event)) {
        focusVariationControl(event.getTarget())
      } else if (KeyCodeCombination.new(KeyCode.T, KeyCombination.SHORTCUT_DOWN, KeyCombination.SHIFT_DOWN).match(event)) {
        insertTagControl()
      } else if (KeyCodeCombination.new(KeyCode.E, KeyCombination.SHORTCUT_DOWN, KeyCombination.SHIFT_DOWN).match(event)) {
        insertEquivalentControl()
      } else if (KeyCodeCombination.new(KeyCode.I, KeyCombination.SHORTCUT_DOWN, KeyCombination.SHIFT_DOWN).match(event)) {
        insertInformationControl()
      } else if (KeyCodeCombination.new(KeyCode.D, KeyCombination.SHORTCUT_DOWN, KeyCombination.SHIFT_DOWN).match(event)) {
        insertVariationControl()
      } else if (KeyCodeCombination.new(KeyCode.R, KeyCombination.SHORTCUT_DOWN, KeyCombination.SHIFT_DOWN).match(event)) {
        insertRelationControl()
      } else if (KeyCodeCombination.new(KeyCode.ENTER, KeyCombination.SHORTCUT_DOWN).match(event)) {
        commit()
      }
    }
  }

  private void setupIdControl() {
    Boolean showsSlimeId = Setting.getInstance().getShowsSlimeId()
    if (!showsSlimeId) {
      $gridPane.getChildren().remove($id)
      $gridPane.getChildren().remove($idLabel)
      $gridPane.getChildren().each() { Node node ->
        $gridPane.setRowIndex(node, $gridPane.getRowIndex(node) - 1)
      }
    }
  }

}