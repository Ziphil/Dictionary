package ziphil.controller

import groovy.transform.CompileStatic
import javafx.application.Platform
import javafx.event.EventTarget
import javafx.fxml.FXML
import javafx.geometry.Bounds
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.control.Button
import javafx.scene.control.ComboBox
import javafx.scene.control.Label
import javafx.scene.control.ScrollPane
import javafx.scene.control.TextArea
import javafx.scene.control.TextField
import javafx.scene.control.TextFormatter
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyCodeCombination
import javafx.scene.input.KeyCombination
import javafx.scene.input.KeyEvent
import javafx.scene.layout.GridPane
import javafx.scene.layout.HBox
import javafx.scene.layout.Priority
import javafx.scene.layout.VBox
import javafx.stage.Modality
import javafx.stage.StageStyle
import ziphil.custom.Dialog
import ziphil.custom.IntegerUnaryOperator
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

  @FXML private TextField $idControl
  @FXML private TextField $nameControl
  @FXML private ScrollPane $scrollPane
  @FXML private GridPane $gridPane
  @FXML private VBox $tagBox
  @FXML private VBox $equivalentBox
  @FXML private VBox $informationBox
  @FXML private VBox $variationBox
  @FXML private VBox $relationBox
  @FXML private Label $idLabel
  private List<ComboBox<String>> $tagControls = ArrayList.new()
  private List<ComboBox<String>> $equivalentTitleControls = ArrayList.new()
  private List<TextField> $equivalentNameControls = ArrayList.new()
  private List<ComboBox<String>> $informationTitleControls = ArrayList.new()
  private List<TextArea> $informationTextControls = ArrayList.new()
  private List<ComboBox<String>> $variationTitleControls = ArrayList.new()
  private List<TextField> $variationNameControls = ArrayList.new()
  private List<SlimeRelation> $relations = ArrayList.new()
  private List<ComboBox<String>> $relationTitleControls = ArrayList.new()
  private List<TextField> $relationNameControls = ArrayList.new()
  private SlimeWord $word
  private SlimeDictionary $dictionary

  public SlimeEditorController(UtilityStage<Boolean> nextStage) {
    super(nextStage)
    loadResource(RESOURCE_PATH, TITLE, DEFAULT_WIDTH, DEFAULT_HEIGHT)
    setupShortcuts()
  }

  @FXML
  private void initialize() {
    setupIdControl()
    setupTextFormatter()
  }

  public void prepare(SlimeWord word, SlimeDictionary dictionary, String defaultName) {
    $word = word
    $dictionary = dictionary
    $idControl.setText(word.getId().toString())
    $nameControl.setText(word.getName())
    word.getTags().each() { String tag ->
      addTagControl(tag, dictionary.getRegisteredTags())
    }
    word.getRawEquivalents().each() { SlimeEquivalent equivalent ->
      String nameString = equivalent.getNames().join(", ")
      addEquivalentControl(equivalent.getTitle(), nameString, dictionary.getRegisteredEquivalentTitles())
    }
    word.getInformations().each() { SlimeInformation information ->
      addInformationControl(information.getTitle(), information.getText(), dictionary.getRegisteredInformationTitles())
    }
    word.getVariations().groupBy{variation -> variation.getTitle()}.each() { String title, List<SlimeVariation> variationGroup ->
      String nameString = variationGroup.collect{variation -> variation.getName()}.join(", ")
      addVariationControl(title, nameString, dictionary.getRegisteredVariationTitles())
    }
    word.getRelations().each() { SlimeRelation relation ->
      addRelationControl(relation.getTitle(), relation.getName(), relation, dictionary.getRegisteredRelationTitles())
    }
    if ($informationTextControls.isEmpty()) {
      insertInformationControl()
    }
    if (defaultName != null) {
      $nameControl.setText(defaultName)
      Platform.runLater() {
        $nameControl.requestFocus()
      }
    } else {
      Platform.runLater() {
        $informationTextControls[0].requestFocus()
      }
    }
  }

  public void prepare(SlimeWord word, SlimeDictionary dictionary) {
    prepare(word, dictionary, null)
  }

  @FXML
  protected void commit() {
    Boolean ignoresDuplicateSlimeId = Setting.getInstance().getIgnoresDuplicateSlimeId()
    try {
      Integer id = $idControl.getText().toInteger()
      if (ignoresDuplicateSlimeId || !$dictionary.containsId(id, $word)) {
        String name = $nameControl.getText()
        List<SlimeEquivalent> rawEquivalents = ArrayList.new()
        List<String> tags = ArrayList.new()
        List<SlimeInformation> informations = ArrayList.new()
        List<SlimeVariation> variations = ArrayList.new()
        List<SlimeRelation> relations = ArrayList.new()
        (0 ..< $tagControls.size()).each() { Integer i ->
          String tag = $tagControls[i].getValue()
          if (tag != "") {
            tags.add(tag)
          }
        }
        (0 ..< $equivalentTitleControls.size()).each() { Integer i ->
          String title = $equivalentTitleControls[i].getValue()
          List<String> equivalentNames = $equivalentNameControls[i].getText().split(/\s*(,|、)\s*/).toList()
          if (!equivalentNames.isEmpty()) {
            rawEquivalents.add(SlimeEquivalent.new(title, equivalentNames))
          }
        }
        (0 ..< $informationTitleControls.size()).each() { Integer i ->
          String title = $informationTitleControls[i].getValue()
          String text = $informationTextControls[i].getText()
          if (text != "") {
            informations.add(SlimeInformation.new(title, text))
          }
        }
        (0 ..< $variationTitleControls.size()).each() { Integer i ->
          String title = $variationTitleControls[i].getValue()
          List<String> variationNames = $variationNameControls[i].getText().split(/\s*(,|、)\s*/).toList()
          variationNames.each() { String variationName ->
            if (variationName != "") {
              variations.add(SlimeVariation.new(title, variationName))
            }
          }
        }
        (0 ..< $relationTitleControls.size()).each() { Integer i ->
          String title = $relationTitleControls[i].getValue()
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
    } catch (NumberFormatException exception) {
      Dialog dialog = Dialog.new("フォーマットエラー", "IDが異常です。数値が大きすぎるか小さすぎる可能性があります。")
      dialog.initOwner($stage)
      dialog.setAllowsCancel(false)
      dialog.showAndWait()
    }
  }

  @FXML
  private void insertTagControl() {
    addTagControl("", $dictionary.getRegisteredTags())
    $tagControls[-1].requestFocus()
  }

  @FXML
  private void insertEquivalentControl() {
    addEquivalentControl("", "", $dictionary.getRegisteredEquivalentTitles())
    $equivalentNameControls[-1].requestFocus()
  }

  @FXML
  private void insertInformationControl() {
    addInformationControl("", "", $dictionary.getRegisteredInformationTitles())
    $informationTextControls[-1].requestFocus()
  }

  @FXML
  private void insertVariationControl() {
    addVariationControl("", "", $dictionary.getRegisteredVariationTitles())
    $variationNameControls[-1].requestFocus()
  }

  @FXML
  private void insertRelationControl() {
    addRelationControl("", "", null, $dictionary.getRegisteredRelationTitles())
    chooseRelation((HBox)$relationBox.getChildren()[-1])
  }

  private void exchangeTagControl(HBox box, Integer amount) {
    Integer index = $tagBox.getChildren().indexOf(box)
    Integer otherIndex = index + amount
    if (index >= 0 && otherIndex >= 0 && otherIndex < $tagBox.getChildren().size()) {
      Node otherBox = $tagBox.getChildren()[otherIndex]
      $tagBox.getChildren()[index] = HBox.new()
      $tagBox.getChildren()[otherIndex] = box
      $tagBox.getChildren()[index] = otherBox
      ComboBox<String> other = $tagControls[otherIndex]
      $tagControls[otherIndex] = $tagControls[index]
      $tagControls[index] = other
    }
  }

  private void exchangeEquivalentControl(HBox box, Integer amount) {
    Integer index = $equivalentBox.getChildren().indexOf(box)
    Integer otherIndex = index + amount
    if (index >= 0 && otherIndex >= 0 && otherIndex < $equivalentBox.getChildren().size()) {
      Node otherBox = $equivalentBox.getChildren()[otherIndex]
      $equivalentBox.getChildren()[index] = HBox.new()
      $equivalentBox.getChildren()[otherIndex] = box
      $equivalentBox.getChildren()[index] = otherBox
      ComboBox<String> otherTitle = $equivalentTitleControls[otherIndex]
      $equivalentTitleControls[otherIndex] = $equivalentTitleControls[index]
      $equivalentTitleControls[index] = otherTitle
      TextField otherText = $equivalentNameControls[otherIndex]
      $equivalentNameControls[otherIndex] = $equivalentNameControls[index]
      $equivalentNameControls[index] = otherText
    }
  }

  private void exchangeInformationControl(HBox box, Integer amount) {
    Integer index = $informationBox.getChildren().indexOf(box)
    Integer otherIndex = index + amount
    if (index >= 0 && otherIndex >= 0 && otherIndex < $informationBox.getChildren().size()) {
      Node otherBox = $informationBox.getChildren()[otherIndex]
      $informationBox.getChildren()[index] = HBox.new()
      $informationBox.getChildren()[otherIndex] = box
      $informationBox.getChildren()[index] = otherBox
      ComboBox<String> otherTitle = $informationTitleControls[otherIndex]
      $informationTitleControls[otherIndex] = $informationTitleControls[index]
      $informationTitleControls[index] = otherTitle
      TextArea otherText = $informationTextControls[otherIndex]
      $informationTextControls[otherIndex] = $informationTextControls[index]
      $informationTextControls[index] = otherText
    }
  }

  private void exchangeVariationControl(HBox box, Integer amount) {
    Integer index = $variationBox.getChildren().indexOf(box)
    Integer otherIndex = index + amount
    if (index >= 0 && otherIndex >= 0 && otherIndex < $variationBox.getChildren().size()) {
      Node otherBox = $variationBox.getChildren()[otherIndex]
      $variationBox.getChildren()[index] = HBox.new()
      $variationBox.getChildren()[otherIndex] = box
      $variationBox.getChildren()[index] = otherBox
      ComboBox<String> otherTitle = $variationTitleControls[otherIndex]
      $variationTitleControls[otherIndex] = $variationTitleControls[index]
      $variationTitleControls[index] = otherTitle
      TextField otherName = $variationNameControls[otherIndex]
      $variationNameControls[otherIndex] = $variationNameControls[index]
      $variationNameControls[index] = otherName
    }
  }

  private void exchangeRelationControl(HBox box, Integer amount) {
    Integer index = $relationBox.getChildren().indexOf(box)
    Integer otherIndex = index + amount
    if (index >= 0 && otherIndex >= 0 && otherIndex < $relationBox.getChildren().size()) {
      Node otherBox = $relationBox.getChildren()[otherIndex]
      $relationBox.getChildren()[index] = HBox.new()
      $relationBox.getChildren()[otherIndex] = box
      $relationBox.getChildren()[index] = otherBox
      SlimeRelation other = $relations[otherIndex]
      $relations[otherIndex] = $relations[index]
      $relations[index] = other
      ComboBox<String> otherTitle = $relationTitleControls[otherIndex]
      $relationTitleControls[otherIndex] = $relationTitleControls[index]
      $relationTitleControls[index] = otherTitle
      TextField otherName = $relationNameControls[otherIndex]
      $relationNameControls[otherIndex] = $relationNameControls[index]
      $relationNameControls[index] = otherName
    }
  }

  private void removeTagControl(HBox box) {
    Integer index = $tagBox.getChildren().indexOf(box)
    if (index >= 0) {
      $tagBox.getChildren().removeAt(index)
      $tagControls.removeAt(index)
    }
  }

  private void removeEquivalentControl(HBox box) {
    Integer index = $equivalentBox.getChildren().indexOf(box)
    if (index >= 0) {
      $equivalentBox.getChildren().removeAt(index)
      $equivalentTitleControls.removeAt(index)
      $equivalentNameControls.removeAt(index)
    }
  }

  private void removeInformationControl(HBox box) {
    Integer index = $informationBox.getChildren().indexOf(box)
    if (index >= 0) {
      $informationBox.getChildren().removeAt(index)
      $informationTitleControls.removeAt(index)
      $informationTextControls.removeAt(index)
    }
  }

  private void removeVariationControl(HBox box) {
    Integer index = $variationBox.getChildren().indexOf(box)
    if (index >= 0) {
      $variationBox.getChildren().removeAt(index)
      $variationTitleControls.removeAt(index)
      $variationNameControls.removeAt(index)
    }
  }

  private void removeRelationControl(HBox box) {
    Integer index = $relationBox.getChildren().indexOf(box)
    if (index >= 0) {
      $relationBox.getChildren().removeAt(index)
      $relations.removeAt(index)
      $relationTitleControls.removeAt(index)
      $relationNameControls.removeAt(index)
    }
  }

  private void focusName() {
    $nameControl.requestFocus()
    scrollPaneTo($nameControl)
  }

  private void focusTagControl(EventTarget target) {
    Integer index = $tagControls.indexOf(target)
    if (index >= 0) {
      Integer nextIndex = (index < $tagControls.size() - 1) ? index + 1 : 0
      $tagControls[nextIndex].requestFocus()
      scrollPaneTo($tagControls[nextIndex])
    } else {
      if ($tagControls.isEmpty()) {
        insertTagControl()
      }
      $tagControls[0].requestFocus()
      scrollPaneTo($tagControls[0])
    }
  }

  private void focusEquivalentControl(EventTarget target) {
    Integer index = $equivalentNameControls.indexOf(target)
    if (index >= 0) {
      Integer nextIndex = (index < $equivalentNameControls.size() - 1) ? index + 1 : 0
      $equivalentNameControls[nextIndex].requestFocus()
      scrollPaneTo($equivalentNameControls[nextIndex])
    } else {
      if ($equivalentNameControls.isEmpty()) {
        insertEquivalentControl()
      }
      $equivalentNameControls[0].requestFocus()
      scrollPaneTo($equivalentNameControls[0])
    }
  }

  private void focusInformationControl(EventTarget target) {
    Integer index = $informationTextControls.indexOf(target)
    if (index >= 0) {
      Integer nextIndex = (index < $informationTextControls.size() - 1) ? index + 1 : 0
      $informationTextControls[nextIndex].requestFocus()
      scrollPaneTo($informationTextControls[nextIndex])
    } else {
      if ($informationTextControls.isEmpty()) {
        insertInformationControl()
      }
      $informationTextControls[0].requestFocus()
      scrollPaneTo($informationTextControls[0])
    }
  }

  private void focusVariationControl(EventTarget target) {
    Integer index = $variationNameControls.indexOf(target)
    if (index >= 0) {
      Integer nextIndex = (index < $variationNameControls.size() - 1) ? index + 1 : 0
      $variationNameControls[nextIndex].requestFocus()
      scrollPaneTo($variationNameControls[nextIndex])
    } else {
      if ($variationNameControls.isEmpty()) {
        insertVariationControl()
      }
      $variationNameControls[0].requestFocus()
      scrollPaneTo($variationNameControls[0])
    }
  }

  private void chooseRelation(HBox box) {
    UtilityStage<SlimeWord> nextStage = UtilityStage.new(StageStyle.UTILITY)
    SlimeWordChooserController controller = SlimeWordChooserController.new(nextStage)
    nextStage.initModality(Modality.WINDOW_MODAL)
    nextStage.initOwner($stage)
    controller.prepare($dictionary.copy())
    SlimeWord word = nextStage.showAndWaitResult()
    if (word != null) {
      Integer index = $relationBox.getChildren().indexOf(box)
      if (index >= 0) {
        $relations[index] = SlimeRelation.new(null, word.getId(), word.getName())
        $relationNameControls[index].setText(word.getName())
      }
    }
  }

  private void addTagControl(String tag, List<String> registeredTags) {
    HBox box = HBox.new(Measurement.rpx(5))
    HBox dammyBox = HBox.new()
    HBox exchangeBox = HBox.new()
    ComboBox<String> tagControl = ComboBox.new()
    Button exchangeUpButton = Button.new("↑")
    Button exchangeDownButton = Button.new("↓")
    Button removeButton = Button.new("－")
    tagControl.setEditable(true)
    tagControl.getItems().addAll(registeredTags)
    tagControl.setValue(tag)
    tagControl.setPrefWidth(Measurement.rpx(120))
    tagControl.setMinWidth(Measurement.rpx(120))
    exchangeUpButton.getStyleClass().add("left-pill")
    exchangeUpButton.setOnAction() {
      exchangeTagControl(box, -1)
    }
    exchangeDownButton.getStyleClass().add("right-pill")
    exchangeDownButton.setOnAction() {
      exchangeTagControl(box, 1)
    }
    removeButton.setOnAction() {
      removeTagControl(box)
    }
    exchangeBox.getChildren().addAll(exchangeUpButton, exchangeDownButton)
    box.getChildren().addAll(tagControl, dammyBox, exchangeBox, removeButton)
    box.setHgrow(dammyBox, Priority.ALWAYS)
    $tagControls.add(tagControl)
    $tagBox.getChildren().add(box)
  }

  private void addEquivalentControl(String title, String name, List<String> registeredTitles) {
    HBox box = HBox.new(Measurement.rpx(5))
    HBox exchangeBox = HBox.new()
    ComboBox<String> titleControl = ComboBox.new()
    TextField nameControl = TextField.new()
    Button exchangeUpButton = Button.new("↑")
    Button exchangeDownButton = Button.new("↓")
    Button removeButton = Button.new("－")
    titleControl.setEditable(true)
    titleControl.getItems().addAll(registeredTitles)
    titleControl.setValue(title)
    titleControl.setPrefWidth(Measurement.rpx(120))
    titleControl.setMinWidth(Measurement.rpx(120))
    nameControl.setText(name)
    exchangeUpButton.getStyleClass().add("left-pill")
    exchangeUpButton.setOnAction() {
      exchangeEquivalentControl(box, -1)
    }
    exchangeDownButton.getStyleClass().add("right-pill")
    exchangeDownButton.setOnAction() {
      exchangeEquivalentControl(box, 1)
    }
    removeButton.setOnAction() {
      removeEquivalentControl(box)
    }
    exchangeBox.getChildren().addAll(exchangeUpButton, exchangeDownButton)
    box.getChildren().addAll(titleControl, nameControl, exchangeBox, removeButton)
    box.setHgrow(nameControl, Priority.ALWAYS)
    $equivalentTitleControls.add(titleControl)
    $equivalentNameControls.add(nameControl)
    $equivalentBox.getChildren().add(box)
    $equivalentBox.setVgrow(box, Priority.ALWAYS)
  }

  private void addInformationControl(String title, String text, List<String> registeredTitles) {
    HBox box = HBox.new(Measurement.rpx(5))
    HBox exchangeBox = HBox.new()
    HBox removeBox = HBox.new()
    ComboBox<String> titleControl = ComboBox.new()
    TextArea textControl = TextArea.new()
    Button exchangeUpButton = Button.new("↑")
    Button exchangeDownButton = Button.new("↓")
    Button removeButton = Button.new("－")
    exchangeBox.setAlignment(Pos.BOTTOM_CENTER)
    removeBox.setAlignment(Pos.BOTTOM_CENTER)
    titleControl.setEditable(true)
    titleControl.getItems().addAll(registeredTitles)
    titleControl.setValue(title)
    titleControl.setPrefWidth(Measurement.rpx(120))
    titleControl.setMinWidth(Measurement.rpx(120))
    textControl.setWrapText(true)
    textControl.getStyleClass().add("editor")
    textControl.setText(text)
    textControl.setPrefHeight(Measurement.rpx(120))
    textControl.setMinHeight(Measurement.rpx(120))
    exchangeUpButton.getStyleClass().add("left-pill")
    exchangeUpButton.setOnAction() {
      exchangeInformationControl(box, -1)
    }
    exchangeDownButton.getStyleClass().add("right-pill")
    exchangeDownButton.setOnAction() {
      exchangeInformationControl(box, 1)
    }
    removeButton.setOnAction() {
      removeInformationControl(box)
    }
    exchangeBox.getChildren().addAll(exchangeUpButton, exchangeDownButton)
    removeBox.getChildren().add(removeButton)
    box.getChildren().addAll(titleControl, textControl, exchangeBox, removeBox)
    box.setHgrow(textControl, Priority.ALWAYS)
    $informationTitleControls.add(titleControl)
    $informationTextControls.add(textControl)
    $informationBox.getChildren().add(box)
    $informationBox.setVgrow(box, Priority.ALWAYS)
  }

  private void addVariationControl(String title, String name, List<String> registeredTitles) {
    HBox box = HBox.new(Measurement.rpx(5))
    HBox exchangeBox = HBox.new()
    ComboBox<String> titleControl = ComboBox.new()
    TextField nameControl = TextField.new()
    Button exchangeUpButton = Button.new("↑")
    Button exchangeDownButton = Button.new("↓")
    Button removeButton = Button.new("－")
    titleControl.setEditable(true)
    titleControl.getItems().addAll(registeredTitles)
    titleControl.setValue(title)
    titleControl.setPrefWidth(Measurement.rpx(120))
    titleControl.setMinWidth(Measurement.rpx(120))
    nameControl.setText(name)
    exchangeUpButton.getStyleClass().add("left-pill")
    exchangeUpButton.setOnAction() {
      exchangeVariationControl(box, -1)
    }
    exchangeDownButton.getStyleClass().add("right-pill")
    exchangeDownButton.setOnAction() {
      exchangeVariationControl(box, 1)
    }
    removeButton.setOnAction() {
      removeVariationControl(box)
    }
    exchangeBox.getChildren().addAll(exchangeUpButton, exchangeDownButton)
    box.getChildren().addAll(titleControl, nameControl, exchangeBox, removeButton)
    box.setHgrow(nameControl, Priority.ALWAYS)
    $variationTitleControls.add(titleControl)
    $variationNameControls.add(nameControl)
    $variationBox.getChildren().add(box)
    $variationBox.setVgrow(box, Priority.ALWAYS)
  }

  private void addRelationControl(String title, String name, SlimeRelation relation, List<String> registeredTitles) {
    HBox box = HBox.new(Measurement.rpx(5))
    HBox nameBox = HBox.new()
    HBox dammyBox = HBox.new()
    HBox exchangeBox = HBox.new()
    ComboBox<String> titleControl = ComboBox.new()
    TextField nameControl = TextField.new()
    Button chooseButton = Button.new("…")
    Button exchangeUpButton = Button.new("↑")
    Button exchangeDownButton = Button.new("↓")
    Button removeButton = Button.new("－")
    titleControl.setEditable(true)
    titleControl.getItems().addAll(registeredTitles)
    titleControl.setValue(title)
    titleControl.setPrefWidth(Measurement.rpx(120))
    titleControl.setMinWidth(Measurement.rpx(120))
    nameControl.setEditable(false)
    nameControl.setText(name)
    nameControl.setPrefWidth(Measurement.rpx(150))
    nameControl.setMinWidth(Measurement.rpx(150))
    nameControl.getStyleClass().add("left-pill")
    chooseButton.getStyleClass().add("right-pill")
    chooseButton.setOnAction() {
      chooseRelation(box)
    }
    exchangeUpButton.getStyleClass().add("left-pill")
    exchangeUpButton.setOnAction() {
      exchangeRelationControl(box, -1)
    }
    exchangeDownButton.getStyleClass().add("right-pill")
    exchangeDownButton.setOnAction() {
      exchangeRelationControl(box, 1)
    }
    removeButton.setOnAction() {
      removeRelationControl(box)
    }
    nameBox.getChildren().addAll(nameControl, chooseButton)
    exchangeBox.getChildren().addAll(exchangeUpButton, exchangeDownButton)
    box.getChildren().addAll(titleControl, nameBox, dammyBox, exchangeBox, removeButton)
    box.setHgrow(dammyBox, Priority.ALWAYS)
    $relations.add(relation)
    $relationTitleControls.add(titleControl)
    $relationNameControls.add(nameControl)
    $relationBox.getChildren().add(box)
    $relationBox.setVgrow(box, Priority.ALWAYS)
  }

  private void scrollPaneTo(Node node) {
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
    $scene.addEventHandler(KeyEvent.KEY_PRESSED) { KeyEvent event ->
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
      $gridPane.getChildren().remove($idControl)
      $gridPane.getChildren().remove($idLabel)
      $gridPane.getChildren().each() { Node node ->
        $gridPane.setRowIndex(node, $gridPane.getRowIndex(node) - 1)
      }
    }
  }

  private void setupTextFormatter() {
    $idControl.setTextFormatter(TextFormatter.new(IntegerUnaryOperator.new()))
  }

}