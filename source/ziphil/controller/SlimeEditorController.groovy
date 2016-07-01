package ziphil.controller

import groovy.transform.CompileStatic
import javafx.application.Platform
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.stage.Modality
import javafx.scene.control.Button
import javafx.scene.control.ComboBox
import javafx.scene.control.TextArea
import javafx.scene.control.TextField
import javafx.scene.layout.HBox
import javafx.scene.layout.Priority
import javafx.scene.layout.VBox
import javafx.stage.StageStyle
import ziphil.custom.CustomBuilderFactory
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
public class SlimeEditorController {

  private static final String RESOURCE_PATH = "resource/fxml/slime_editor.fxml"
  private static final String TITLE = "単語編集"
  private static final Double DEFAULT_WIDTH = Measurement.rpx(640)
  private static final Double DEFAULT_HEIGHT = Measurement.rpx(640)

  @FXML private TextField $id
  @FXML private TextField $name
  @FXML private TextField $tag
  @FXML private VBox $tagBox
  @FXML private VBox $equivalentBox
  @FXML private VBox $informationBox
  @FXML private VBox $variationBox
  @FXML private VBox $relationBox
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
  private UtilityStage<Boolean> $stage
  private Scene $scene

  public SlimeEditorController(UtilityStage<Boolean> stage) {
    $stage = stage
    loadResource()
    setupEditor()
  }

  public void prepare(SlimeWord word, SlimeDictionary dictionary) {
    $word = word
    $dictionary = dictionary
    $id.setText(word.getId().toString())
    $name.setText(word.getName())
    word.getTags().each() { String tag ->
      addTagControl(tag, dictionary.registeredTags())
    }
    word.getRawEquivalents().groupBy{equivalent -> equivalent.getTitle()}.each() { String title, List<SlimeEquivalent> equivalentGroup ->
      String nameString = equivalentGroup.collect{equivalent -> equivalent.getName()}.join(", ")
      addEquivalentControl(title, nameString, dictionary.registeredEquivalentTitles())
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
    if (!$dictionary.containsId(id, $word)) {
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
        equivalentNames.each() { String equivalentName ->
          if (equivalentName != "") {
            rawEquivalents.add(SlimeEquivalent.new(title, equivalentName))
          }
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
      Dialog dialog = Dialog.new()
      dialog.initOwner($stage)
      dialog.setTitle("重複IDエラー")
      dialog.setContentString("このIDはすでに利用されています。別のIDを指定してください。")
      dialog.setAllowsCancel(false)
      dialog.showAndWait()
    }
  }

  @FXML
  private void cancelEdit() {
    $stage.close(false)
  }

  @FXML
  private void insertTagControl() {
    addTagControl("", $dictionary.registeredTags())
    setupEditor()
  }

  @FXML
  private void insertEquivalentControl() {
    addEquivalentControl("", "", $dictionary.registeredEquivalentTitles())
    setupEditor()
  }

  @FXML
  private void insertInformationControl() {
    addInformationControl("", "", $dictionary.registeredInformationTitles())
    setupEditor()
  }

  @FXML
  private void insertVariationControl() {
    addVariationControl("", "", $dictionary.registeredVariationTitles())
    setupEditor()
  }

  @FXML
  private void insertRelationControl() {
    addRelationControl("", "", null, $dictionary.registeredRelationTitles())
    setupEditor()
  }

  private void removeTagControl(HBox box) {
    Integer index
    $tagBox.getChildren().eachWithIndex() { Node node, Integer i ->
      if (node == box) {
        index = i
      }
    }
    if (index != null) {
      $tagBox.getChildren().removeAt(index)
      $tags.removeAt(index)
    }
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

  private void chooseRelation(HBox box) {
    UtilityStage<SlimeWord> stage = UtilityStage.new(StageStyle.UTILITY)
    SlimeWordChooserController controller = SlimeWordChooserController.new(stage)
    stage.initModality(Modality.WINDOW_MODAL)
    stage.initOwner($stage)
    controller.prepare($dictionary.copy())
    SlimeWord word = stage.showAndWaitResult()
    if (word != null) {
      Integer index
      $relationBox.getChildren().eachWithIndex() { Node node, Integer i ->
        if (node == box) {
          index = i
        }
      }
      if (index != null) {
        $relations[index] = SlimeRelation.new(null, word.getId(), word.getName())
        $relationNames[index].setText(word.getName())
      }
    }
  }

  private void addTagControl(String tagString, List<String> registeredTags) {
    HBox box = HBox.new(Measurement.rpx(5))
    ComboBox<String> tag = ComboBox.new()
    Button remove = Button.new("削除")
    tag.setEditable(true)
    tag.getItems().addAll(registeredTags)
    tag.setValue(tagString)
    tag.setPrefWidth(Measurement.rpx(120))
    tag.setMinWidth(Measurement.rpx(120))
    remove.setPrefWidth(Measurement.rpx(70))
    remove.setMinWidth(Measurement.rpx(70))
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
    Button remove = Button.new("削除")
    title.setEditable(true)
    title.getItems().addAll(registeredTitles)
    title.setValue(titleString)
    title.setPrefWidth(Measurement.rpx(120))
    title.setMinWidth(Measurement.rpx(120))
    name.setText(nameString)
    remove.setPrefWidth(Measurement.rpx(70))
    remove.setMinWidth(Measurement.rpx(70))
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
    Button remove = Button.new("削除")
    removeBox.setAlignment(Pos.BOTTOM_CENTER)
    title.setEditable(true)
    title.getItems().addAll(registeredTitles)
    title.setValue(titleString)
    title.setPrefWidth(Measurement.rpx(120))
    title.setMinWidth(Measurement.rpx(120))
    text.setWrapText(true)
    text.setText(textString)
    text.setPrefHeight(Measurement.rpx(120))
    text.setMinHeight(Measurement.rpx(120))
    remove.setPrefWidth(Measurement.rpx(70))
    remove.setMinWidth(Measurement.rpx(70))
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
    Button remove = Button.new("削除")
    title.setEditable(true)
    title.getItems().addAll(registeredTitles)
    title.setValue(titleString)
    title.setPrefWidth(Measurement.rpx(120))
    title.setMinWidth(Measurement.rpx(120))
    name.setText(nameString)
    remove.setPrefWidth(Measurement.rpx(70))
    remove.setMinWidth(Measurement.rpx(70))
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
    Button remove = Button.new("削除")
    title.setEditable(true)
    title.getItems().addAll(registeredTitles)
    title.setValue(titleString)
    title.setPrefWidth(Measurement.rpx(120))
    title.setMinWidth(Measurement.rpx(120))
    name.setEditable(false)
    name.setText(nameString)
    name.getStyleClass().add("left-pill")
    choose.getStyleClass().add("right-pill")
    remove.setPrefWidth(Measurement.rpx(70))
    remove.setMinWidth(Measurement.rpx(70))
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