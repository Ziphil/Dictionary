package ziphil.controller

import groovy.transform.CompileStatic
import javafx.application.Platform
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.ComboBox
import javafx.scene.control.TextArea
import javafx.scene.control.TextField
import javafx.scene.layout.HBox
import javafx.scene.layout.Priority
import javafx.scene.layout.VBox
import ziphil.custom.CustomBuilderFactory
import ziphil.custom.Measurement
import ziphil.dictionary.SlimeEquivalent
import ziphil.dictionary.SlimeInformation
import ziphil.dictionary.SlimeRelation
import ziphil.dictionary.SlimeVariation
import ziphil.dictionary.SlimeWord
import ziphil.module.Setting
import ziphil.node.UtilityStage


@CompileStatic @Newify
public class SlimeEditorController {

  private static final String RESOURCE_PATH = "resource/fxml/slime_editor.fxml"
  private static final String TITLE = "単語編集"
  private static final Double DEFAULT_WIDTH = Measurement.rpx(640)
  private static final Double DEFAULT_HEIGHT = Measurement.rpx(640)

  @FXML private TextField $id
  @FXML private TextField $name
  @FXML private TextField $tag
  @FXML private VBox $equivalentBox
  @FXML private VBox $informationBox
  @FXML private VBox $variationBox
  @FXML private VBox $relationBox
  private List<ComboBox<String>> $equivalentTitles = ArrayList.new()
  private List<TextField> $equivalentNames = ArrayList.new()
  private List<ComboBox<String>> $informationTitles = ArrayList.new()
  private List<TextArea> $informationTexts = ArrayList.new()
  private List<ComboBox<String>> $variationTitles = ArrayList.new()
  private List<TextField> $variationNames = ArrayList.new()
  private List<ComboBox<String>> $relationTitles = ArrayList.new()
  private List<TextField> $relationNames = ArrayList.new()
  private SlimeWord $word
  private UtilityStage<Boolean> $stage
  private Scene $scene

  public SlimeEditorController(UtilityStage<Boolean> stage) {
    $stage = stage
    loadResource()
    setupEditor()
  }

  public void prepare(SlimeWord word) {
    $word = word
    $id.setText(word.getId().toString())
    $name.setText(word.getName())
    $tag.setText(word.getTags().join(", "))
    word.getRawEquivalents().groupBy{equivalent -> equivalent.getTitle()}.each() { String title, List<SlimeEquivalent> eachGroup ->
      addEquivalentControl(title, eachGroup.collect{equivalent -> equivalent.getName()}.join(", "))
    }
    word.getInformations().each() { SlimeInformation information ->
      addInformationControl(information.getTitle(), information.getText())
    }
    word.getVariations().each() { SlimeVariation variation ->
      addVariationControl(variation.getTitle(), variation.getName())
    }
    word.getRelations().each() { SlimeRelation relation ->
      addRelationControl(relation.getTitle(), relation.getName())
    }
    if (!$informationTexts.isEmpty()) {
      Platform.runLater() {
        $informationTexts[0].requestFocus()
      }
    }
    setupEditor()
  }

  @FXML
  private void commitEdit() {
    Integer id = $id.getText().toInteger()
    String name = $name.getText()
    List<SlimeEquivalent> rawEquivalents = ArrayList.new()
    List<String> tags = $tag.getText().split(/\s*(,|、)\s*/).toList()
    List<SlimeInformation> informations = ArrayList.new()
    List<SlimeVariation> variations = ArrayList.new()
    List<SlimeRelation> relations = ArrayList.new()
    (0 ..< $equivalentTitles.size()).each() { Integer i ->
      String title = $equivalentTitles[i].getValue()
      List<String> equivalentNames = $equivalentNames[i].getText().split(/\s*(,|、)\s*/).toList()
      equivalentNames.each() { String equivalentName ->
        if (title != "" && equivalentName != "") {
          rawEquivalents.add(SlimeEquivalent.new(title, equivalentName))
        }
      }
    }
    (0 ..< $informationTitles.size()).each() { Integer i ->
      String title = $informationTitles[i].getValue()
      String text = $informationTexts[i].getText()
      if (title != "" && text != "") {
        informations.add(SlimeInformation.new(title, text))
      }
    }
    (0 ..< $variationTitles.size()).each() { Integer i ->
      String title = $variationTitles[i].getValue()
      String variationName = $variationNames[i].getText()
      if (title != "" && variationName != "") {
        variations.add(SlimeVariation.new(title, variationName))
      }
    }
    (0 ..< $relationTitles.size()).each() { Integer i ->
      String title = $relationTitles[i].getValue()
      String relationName = $relationNames[i].getText()
      if (title != "" && relationName != "") {
        relations.add(SlimeRelation.new(title, -1, relationName))
      }
    }
    $word.update(id, name, rawEquivalents, tags, informations, variations, relations)
    $stage.close(true)
  }

  @FXML
  private void cancelEdit() {
    $stage.close(false)
  }

  @FXML
  private void insertEquivalentControl() {
    addEquivalentControl(null, null)
    setupEditor()
  }

  @FXML
  private void insertInformationControl() {
    addInformationControl(null, null)
    setupEditor()
  }

  @FXML
  private void insertVariationControl() {
    addVariationControl(null, null)
    setupEditor()
  }

  @FXML
  private void insertRelationControl() {
    addRelationControl(null, null)
    setupEditor()
  }

  private void removeEquivalentControl(HBox box) {
    Integer index
    $equivalentBox.getChildren().eachWithIndex() { Node node, Integer i ->
      if (node == box) {
        index = i
      }
    }
    if (index != null) {
      $equivalentBox.getChildren().removeAt(index)
      $equivalentTitles.removeAt(index)
      $equivalentNames.removeAt(index)
    }
  }

  private void removeInformationControl(HBox box) {
    Integer index
    $informationBox.getChildren().eachWithIndex() { Node node, Integer i ->
      if (node == box) {
        index = i
      }
    }
    if (index != null) {
      $informationBox.getChildren().removeAt(index)
      $informationTitles.removeAt(index)
      $informationTexts.removeAt(index)
    }
  }

  private void removeVariationControl(HBox box) {
    Integer index
    $variationBox.getChildren().eachWithIndex() { Node node, Integer i ->
      if (node == box) {
        index = i
      }
    }
    if (index != null) {
      $variationBox.getChildren().removeAt(index)
      $variationTitles.removeAt(index)
      $variationNames.removeAt(index)
    }
  }

  private void removeRelationControl(HBox box) {
    Integer index
    $relationBox.getChildren().eachWithIndex() { Node node, Integer i ->
      if (node == box) {
        index = i
      }
    }
    if (index != null) {
      $relationBox.getChildren().removeAt(index)
      $relationTitles.removeAt(index)
      $relationNames.removeAt(index)
    }
  }

  private void addEquivalentControl(String titleString, String nameString) {
    HBox box = HBox.new(Measurement.rpx(5))
    ComboBox<String> title = ComboBox.new()
    TextField name = TextField.new()
    Button remove = Button.new("削除")
    title.setEditable(true)
    title.setPrefWidth(Measurement.rpx(120))
    title.setMinWidth(Measurement.rpx(120))
    remove.setPrefWidth(Measurement.rpx(70))
    remove.setMinWidth(Measurement.rpx(70))
    remove.setOnAction() {
      removeEquivalentControl(box)
    }
    if (titleString != null) {
      title.setValue(titleString)
    }
    if (nameString != null) {
      name.setText(nameString)
    }
    box.getChildren().addAll(title, name, remove)
    box.setHgrow(name, Priority.ALWAYS)
    $equivalentTitles.add(title)
    $equivalentNames.add(name)
    $equivalentBox.getChildren().add(box)
    $equivalentBox.setVgrow(box, Priority.ALWAYS)
  }

  private void addInformationControl(String titleString, String textString) {
    HBox box = HBox.new(Measurement.rpx(5))
    HBox removeBox = HBox.new()
    ComboBox<String> title = ComboBox.new()
    TextArea text = TextArea.new()
    Button remove = Button.new("削除")
    removeBox.setAlignment(Pos.BOTTOM_CENTER)
    title.setEditable(true)
    title.setPrefWidth(Measurement.rpx(120))
    title.setMinWidth(Measurement.rpx(120))
    text.setWrapText(true)
    text.setPrefHeight(Measurement.rpx(120))
    text.setMinHeight(Measurement.rpx(120))
    remove.setPrefWidth(Measurement.rpx(70))
    remove.setMinWidth(Measurement.rpx(70))
    remove.setOnAction() {
      removeInformationControl(box)
    }
    if (title != null) {
      title.setValue(titleString)
    }
    if (textString != null) {
      text.setText(textString)
    }
    removeBox.getChildren().add(remove)
    box.getChildren().addAll(title, text, removeBox)
    box.setHgrow(text, Priority.ALWAYS)
    $informationTitles.add(title)
    $informationTexts.add(text)
    $informationBox.getChildren().add(box)
    $informationBox.setVgrow(box, Priority.ALWAYS)
  }

  private void addVariationControl(String titleString, String nameString) {
    HBox box = HBox.new(Measurement.rpx(5))
    ComboBox<String> title = ComboBox.new()
    TextField name = TextField.new()
    Button remove = Button.new("削除")
    title.setEditable(true)
    title.setPrefWidth(Measurement.rpx(120))
    title.setMinWidth(Measurement.rpx(120))
    remove.setPrefWidth(Measurement.rpx(70))
    remove.setMinWidth(Measurement.rpx(70))
    remove.setOnAction() {
      removeVariationControl(box)
    }
    if (titleString != null) {
      title.setValue(titleString)
    }
    if (nameString != null) {
      name.setText(nameString)
    }
    box.getChildren().addAll(title, name, remove)
    box.setHgrow(name, Priority.ALWAYS)
    $variationTitles.add(title)
    $variationNames.add(name)
    $variationBox.getChildren().add(box)
    $variationBox.setVgrow(box, Priority.ALWAYS)
  }

  private void addRelationControl(String titleString, String nameString) {
    HBox box = HBox.new(Measurement.rpx(5))
    ComboBox<String> title = ComboBox.new()
    TextField name = TextField.new()
    Button remove = Button.new("削除")
    title.setEditable(true)
    title.setPrefWidth(Measurement.rpx(120))
    title.setMinWidth(Measurement.rpx(120))
    remove.setPrefWidth(Measurement.rpx(70))
    remove.setMinWidth(Measurement.rpx(70))
    remove.setOnAction() {
      removeRelationControl(box)
    }
    if (titleString != null) {
      title.setValue(titleString)
    }
    if (nameString != null) {
      name.setText(nameString)
    }
    box.getChildren().addAll(title, name, remove)
    box.setHgrow(name, Priority.ALWAYS)
    $relationTitles.add(title)
    $relationNames.add(name)
    $relationBox.getChildren().add(box)
    $relationBox.setVgrow(box, Priority.ALWAYS)
  }


  private void setupEditor() {
    Setting setting = Setting.getInstance()
    String fontFamily = setting.getEditorFontFamily()
    Integer fontSize = setting.getEditorFontSize()
    if (fontFamily != null && fontSize != null) {
      $informationTexts.each() { TextArea informationText ->
        informationText.setStyle("-fx-font-family: \"${fontFamily}\"; -fx-font-size: ${fontSize}")
      }
    }
  }

  private void loadResource() {
    FXMLLoader loader = FXMLLoader.new(getClass().getClassLoader().getResource(RESOURCE_PATH), null, CustomBuilderFactory.new())
    loader.setController(this)
    Parent root = (Parent)loader.load()
    $scene = Scene.new(root, DEFAULT_WIDTH, DEFAULT_HEIGHT)
    $stage.setScene($scene)
    $stage.setTitle(TITLE)
    $stage.sizeToScene()
  }

}