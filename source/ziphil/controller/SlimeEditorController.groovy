package ziphil.controller

import groovy.transform.CompileStatic
import javafx.beans.value.ObservableValue
import javafx.event.EventTarget
import javafx.fxml.FXML
import javafx.geometry.Bounds
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.ComboBox
import javafx.scene.control.Label
import javafx.scene.control.ScrollPane
import javafx.scene.control.TextArea
import javafx.scene.control.TextField
import javafx.scene.control.TextFormatter
import javafx.scene.control.TitledPane
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
import ziphil.custom.UnfocusableButton
import ziphil.custom.UtilityStage
import ziphil.dictionary.WordEditResult
import ziphil.dictionary.slime.SlimeDictionary
import ziphil.dictionary.slime.SlimeEquivalent
import ziphil.dictionary.slime.SlimeInformation
import ziphil.dictionary.slime.SlimeRelation
import ziphil.dictionary.slime.SlimeRelationRequest
import ziphil.dictionary.slime.SlimeVariation
import ziphil.dictionary.slime.SlimeWord
import ziphil.module.Setting
import ziphilib.transform.InnerClass
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class SlimeEditorController extends Controller<WordEditResult> {

  private static final String RESOURCE_PATH = "resource/fxml/controller/slime_editor.fxml"
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
  @FXML private TitledPane $relationPane
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
  private Boolean $normal
  private List<RelationRequest> $relationRequests = ArrayList.new()

  public SlimeEditorController(UtilityStage<? super WordEditResult> nextStage) {
    super(nextStage)
    loadResource(RESOURCE_PATH, TITLE, DEFAULT_WIDTH, DEFAULT_HEIGHT)
    setupShortcuts()
  }

  @FXML
  private void initialize() {
    setupIdControl()
  }

  // コントローラーの準備を行います。
  // empty に true を指定すると、新規単語の編集だと判断され、単語名の編集欄にフォーカスが当たっている状態で編集ウィンドウが開きます。
  // 一方、empty に false を指定すると、既存の単語の編集だと判断され、内容の編集欄にフォーカスが当たっている状態で編集ウィンドウが開きます。
  // normal に false を指定すると、辞書に登録される単語データ以外の編集だと判断され、ID と単語名の編集欄が無効化されます。
  public void prepare(SlimeWord word, SlimeDictionary dictionary, Boolean empty, Boolean normal) {
    $word = word
    $dictionary = dictionary
    $normal = normal
    prepareBasicControls()
    prepareTagControls()
    prepareEquivalentControls()
    prepareInformationControls()
    prepareVariationControls()
    prepareRelationControls()
    prepareFocus(empty)
  }

  public void prepare(SlimeWord word, SlimeDictionary dictionary, Boolean empty) {
    prepare(word, dictionary, empty, true)
  }

  public void prepare(SlimeWord word, SlimeDictionary dictionary) {
    prepare(word, dictionary, false, true)
  }

  @FXML
  protected void commit() {
    Setting setting = Setting.getInstance()
    Boolean ignoresDuplicateId = setting.getIgnoresDuplicateSlimeId()
    Boolean asksDuplicateName = setting.getAsksDuplicateName()
    try {
      Int id = createId()
      String name = createName()
      if (ignoresDuplicateId || !$normal || !$dictionary.containsId(id, $word)) {
        Boolean committed = true
        SlimeWord removedWord = null
        if (asksDuplicateName && $normal) {
          SlimeWord otherWord = $dictionary.findName(name, $word)
          if (otherWord != null) {
            Dialog dialog = Dialog.new(StageStyle.UTILITY)
            dialog.initOwner($stage)
            dialog.setTitle("重複単語名の確認")
            dialog.setContentText("この単語名はすでに登録されています。このまま別単語として登録しますか? それとも1つの単語に統合しますか?")
            dialog.setCommitText("別単語")
            dialog.setNegateText("統合")
            dialog.setAllowsNegate(true)
            dialog.showAndWait()
            if (dialog.isNegated()) {
              removedWord = otherWord
            } else if (dialog.isCancelled()) {
              committed = false
            }
          }
        }
        if (committed) {
          $word.setId(id)
          $word.setName(name)
          $word.setRawEquivalents(createEquivalents())
          $word.setTags(createTags())
          $word.setInformations(createInformations())
          $word.setVariations(createVariations())
          $word.setRelations(createRelations())
          if (removedWord != null) {
            $word.merge(removedWord)
          }
          $word.update()
          requestRelations(id, name)
          WordEditResult result = WordEditResult.new($word, removedWord)
          $stage.commit(result)
        }
      } else {
        Dialog dialog = Dialog.new(StageStyle.UTILITY)
        dialog.initOwner($stage)
        dialog.setTitle("重複IDエラー")
        dialog.setContentText("このIDはすでに利用されています。別のIDを指定してください。")
        dialog.setAllowsCancel(false)
        dialog.showAndWait()
      }
    } catch (NumberFormatException exception) {
      Dialog dialog = Dialog.new(StageStyle.UTILITY)
      dialog.initOwner($stage)
      dialog.setTitle("フォーマットエラー")
      dialog.setContentText("IDが異常です。数値が大きすぎるか小さすぎる可能性があります。")
      dialog.setAllowsCancel(false)
      dialog.showAndWait()
    }
  }

  private void prepareBasicControls() {
    $idControl.setText($word.getId().toString())
    $nameControl.setText($word.getName())
    if (!$normal) {
      $idControl.setDisable(true)
      $nameControl.setDisable(true)
    }
  }

  private void prepareTagControls() {
    for (String tag : $word.getTags()) {
      addTagControl(tag, $dictionary.getRegisteredTags())
    }
  }

  private void prepareEquivalentControls() {
    for (SlimeEquivalent equivalent : $word.getRawEquivalents()) {
      String nameString = equivalent.getNames().join($dictionary.firstPunctuation())
      addEquivalentControl(equivalent.getTitle(), nameString, $dictionary.getRegisteredEquivalentTitles())
    }
  }

  private void prepareInformationControls() {
    for (SlimeInformation information : $word.getInformations()) {
      addInformationControl(information.getTitle(), information.getText(), $dictionary.getRegisteredInformationTitles())
    }
    if ($informationTextControls.isEmpty()) {
      insertInformationControl()
    }
  }

  private void prepareVariationControls() {
    for (Map.Entry<String, List<SlimeVariation>> entry : $word.groupedVariations()) {
      String title = entry.getKey()
      List<SlimeVariation> variationGroup = entry.getValue()
      String nameString = variationGroup.collect{it.getName()}.join(", ")
      addVariationControl(title, nameString, $dictionary.getRegisteredVariationTitles())
    }
  }

  private void prepareRelationControls() {
    for (SlimeRelation relation : $word.getRelations()) {
      addRelationControl(relation.getTitle(), relation.getName(), relation, $dictionary.getRegisteredRelationTitles())
    }
    if (!$normal) {
      VBox parent = (VBox)$relationPane.getParent()
      parent.getChildren().remove($relationPane)
    }
  }

  private void prepareFocus(Boolean empty) {
    if (empty) {
      $nameControl.sceneProperty().addListener() { ObservableValue<? extends Scene> observableValue, Scene oldValue, Scene newValue ->
        if (oldValue == null && newValue != null) {
          $nameControl.requestFocus()
        }
      }
    } else {
      $informationTextControls[0].sceneProperty().addListener() { ObservableValue<? extends Scene> observableValue, Scene oldValue, Scene newValue ->
        if (oldValue == null & newValue != null) {
          $informationTextControls[0].requestFocus()
        }
      }
    }
  }

  @FXML
  private void insertTagControl() {
    addTagControl("", $dictionary.getRegisteredTags())
    $tagControls.last().requestFocus()
  }

  @FXML
  private void insertEquivalentControl() {
    addEquivalentControl("", "", $dictionary.getRegisteredEquivalentTitles())
    $equivalentNameControls.last().requestFocus()
  }

  @FXML
  private void insertInformationControl() {
    addInformationControl("", "", $dictionary.getRegisteredInformationTitles())
    $informationTextControls.last().requestFocus()
  }

  @FXML
  private void insertVariationControl() {
    addVariationControl("", "", $dictionary.getRegisteredVariationTitles())
    $variationNameControls.last().requestFocus()
  }

  @FXML
  private void insertRelationControl() {
    addRelationControl("", "", null, $dictionary.getRegisteredRelationTitles())
    chooseRelation($relationBox.getChildren().last())
  }

  private void swapTagControl(Node box, Int amount) {
    Int index = $tagBox.getChildren().indexOf(box)
    Int otherIndex = index + amount
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

  private void swapEquivalentControl(Node box, Int amount) {
    Int index = $equivalentBox.getChildren().indexOf(box)
    Int otherIndex = index + amount
    if (index >= 0 && otherIndex >= 0 && otherIndex < $equivalentBox.getChildren().size()) {
      Node otherBox = $equivalentBox.getChildren()[otherIndex]
      $equivalentBox.getChildren()[index] = HBox.new()
      $equivalentBox.getChildren()[otherIndex] = box
      $equivalentBox.getChildren()[index] = otherBox
      ComboBox<String> otherTitle = $equivalentTitleControls[otherIndex]
      $equivalentTitleControls[otherIndex] = $equivalentTitleControls[index]
      $equivalentTitleControls[index] = otherTitle
      TextField otherName = $equivalentNameControls[otherIndex]
      $equivalentNameControls[otherIndex] = $equivalentNameControls[index]
      $equivalentNameControls[index] = otherName
    }
  }

  private void swapInformationControl(Node box, Int amount) {
    Int index = $informationBox.getChildren().indexOf(box)
    Int otherIndex = index + amount
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

  private void swapVariationControl(Node box, Int amount) {
    Int index = $variationBox.getChildren().indexOf(box)
    Int otherIndex = index + amount
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

  private void swapRelationControl(Node box, Int amount) {
    Int index = $relationBox.getChildren().indexOf(box)
    Int otherIndex = index + amount
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

  private void removeTagControl(Node box) {
    Int index = $tagBox.getChildren().indexOf(box)
    if (index >= 0) {
      $tagBox.getChildren().removeAt(index)
      $tagControls.removeAt(index)
    }
  }

  private void removeEquivalentControl(Node box) {
    Int index = $equivalentBox.getChildren().indexOf(box)
    if (index >= 0) {
      $equivalentBox.getChildren().removeAt(index)
      $equivalentTitleControls.removeAt(index)
      $equivalentNameControls.removeAt(index)
    }
  }

  private void removeInformationControl(Node box) {
    Int index = $informationBox.getChildren().indexOf(box)
    if (index >= 0) {
      $informationBox.getChildren().removeAt(index)
      $informationTitleControls.removeAt(index)
      $informationTextControls.removeAt(index)
    }
  }

  private void removeVariationControl(Node box) {
    Int index = $variationBox.getChildren().indexOf(box)
    if (index >= 0) {
      $variationBox.getChildren().removeAt(index)
      $variationTitleControls.removeAt(index)
      $variationNameControls.removeAt(index)
    }
  }

  private void removeRelationControl(Node box) {
    Int index = $relationBox.getChildren().indexOf(box)
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
    Int index = $tagControls.indexOf(target)
    if (index >= 0) {
      Int nextIndex = (index < $tagControls.size() - 1) ? index + 1 : 0
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
    Int index = $equivalentNameControls.indexOf(target)
    if (index >= 0) {
      Int nextIndex = (index < $equivalentNameControls.size() - 1) ? index + 1 : 0
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
    Int index = $informationTextControls.indexOf(target)
    if (index >= 0) {
      Int nextIndex = (index < $informationTextControls.size() - 1) ? index + 1 : 0
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
    Int index = $variationNameControls.indexOf(target)
    if (index >= 0) {
      Int nextIndex = (index < $variationNameControls.size() - 1) ? index + 1 : 0
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

  @FXML
  private void generateName() {
    UtilityStage<NameGeneratorController.Result> nextStage = UtilityStage.new(StageStyle.UTILITY)
    NameGeneratorController controller = NameGeneratorController.new(nextStage)
    nextStage.initModality(Modality.APPLICATION_MODAL)
    nextStage.initOwner($stage)
    controller.prepare(false)
    nextStage.showAndWait()
    if (nextStage.isCommitted()) {
      NameGeneratorController.Result result = nextStage.getResult()
      $nameControl.setText(result.getNames().first())
    }
  }

  private Int createId() {
    return IntegerClass.parseInt($idControl.getText())
  }

  private String createName() {
    return $nameControl.getText()
  }

  private List<SlimeEquivalent> createEquivalents() {
    List<SlimeEquivalent> equivalents = ArrayList.new()
    String punctuationPattern = /\s*(${$dictionary.getPunctuations().join("|")})\s*/
    for (Int i = 0 ; i < $equivalentTitleControls.size() ; i ++) {
      String title = $equivalentTitleControls[i].getValue()
      List<String> equivalentNames = $equivalentNameControls[i].getText().split(punctuationPattern).toList()
      if (!equivalentNames.isEmpty()) {
        equivalents.add(SlimeEquivalent.new(title, equivalentNames))
      }
    }
    return equivalents
  }

  private List<String> createTags() {
    List<String> tags = ArrayList.new()
    for (Int i = 0 ; i < $tagControls.size() ; i ++) {
      String tag = $tagControls[i].getValue()
      if (tag != "") {
        tags.add(tag)
      }
    }
    return tags
  }

  private List<SlimeInformation> createInformations() {
    List<SlimeInformation> informations = ArrayList.new()
    for (Int i = 0 ; i < $informationTitleControls.size() ; i ++) {
      String title = $informationTitleControls[i].getValue()
      String text = $informationTextControls[i].getText()
      if (text != "") {
        informations.add(SlimeInformation.new(title, text))
      }
    }
    return informations
  }

  private List<SlimeVariation> createVariations() {
    List<SlimeVariation> variations = ArrayList.new()
    String punctuationPattern = /\s*(${$dictionary.getPunctuations().join("|")})\s*/
    for (Int i = 0 ; i < $variationTitleControls.size() ; i ++) {
      String title = $variationTitleControls[i].getValue()
      List<String> variationNames = $variationNameControls[i].getText().split(punctuationPattern).toList()
      for (String variationName : variationNames) {
        if (variationName != "") {
          variations.add(SlimeVariation.new(title, variationName))
        }
      }
    }
    return variations
  }

  // 入力された情報から関連語オブジェクトのリストを生成します。
  // 同時に相互参照依頼のデータを更新します。
  private List<SlimeRelation> createRelations() {
    List<SlimeRelation> relations = ArrayList.new()
    for (Int i = 0 ; i < $relationTitleControls.size() ; i ++) {
      String title = $relationTitleControls[i].getValue()
      SlimeRelation relation = $relations[i]
      Node box = $relationBox.getChildren()[i]
      if (relation != null) {
        relations.add(SlimeRelation.new(title, relation.getId(), relation.getName()))
      }
      for (RelationRequest request : $relationRequests) {
        if (request.getBox() == box) {
          request.getRelation().setTitle(title)
        }
      }
    }
    return relations
  }

  private void requestRelations(Int id, String name) {
    for (RelationRequest request : $relationRequests) {
      if (request.getRelation().getTitle() != null) {
        request.getRelation().setId(id)
        request.getRelation().setName(name)
        $dictionary.requestRelation(request)
      }
    }
  }

  private void chooseRelation(Node box) {
    UtilityStage<SlimeWord> nextStage = UtilityStage.new(StageStyle.UTILITY)
    SlimeWordChooserController controller = SlimeWordChooserController.new(nextStage)
    Boolean asksMutualRelation = Setting.getInstance().getAsksMutualRelation()
    nextStage.initModality(Modality.APPLICATION_MODAL)
    nextStage.initOwner($stage)
    controller.prepare($dictionary.copy())
    nextStage.showAndWait()
    if (nextStage.isCommitted() && nextStage.getResult() != null) {
      SlimeWord word = nextStage.getResult()
      Int index = $relationBox.getChildren().indexOf(box)
      if (index >= 0) {
        $relations[index] = SlimeRelation.new(null, word.getId(), word.getName())
        $relationNameControls[index].setText(word.getName())
        if (asksMutualRelation) {
          Dialog dialog = Dialog.new(StageStyle.UTILITY)
          dialog.initOwner($stage)
          dialog.setTitle("関連語相互参照")
          dialog.setContentText("この関連語を相互参照にしますか? ここで関連語に設定した単語に、この単語が関連語として追加されます。")
          dialog.setCommitText("はい")
          dialog.setCancelText("いいえ")
          dialog.showAndWait()
          if (dialog.isCommitted()) {
            $relationRequests.add(RelationRequest.new(word, box))
          }
        }
      }
    } else {
      removeRelationControl(box)
    }
  }

  private void addTagControl(String tag, List<String> registeredTags) {
    HBox box = HBox.new(Measurement.rpx(5))
    HBox dammyBox = HBox.new()
    HBox swapBox = HBox.new()
    ComboBox<String> tagControl = ComboBox.new()
    Button swapUpButton = UnfocusableButton.new("↑")
    Button swapDownButton = UnfocusableButton.new("↓")
    Button removeButton = UnfocusableButton.new("－")
    tagControl.setEditable(true)
    tagControl.getItems().addAll(registeredTags)
    tagControl.setValue(tag)
    tagControl.setPrefWidth(Measurement.rpx(120))
    tagControl.setMinWidth(Measurement.rpx(120))
    swapUpButton.getStyleClass().add("left-pill")
    swapUpButton.setOnAction() {
      swapTagControl(box, -1)
    }
    swapDownButton.getStyleClass().add("right-pill")
    swapDownButton.setOnAction() {
      swapTagControl(box, 1)
    }
    removeButton.setOnAction() {
      removeTagControl(box)
    }
    swapBox.getChildren().addAll(swapUpButton, swapDownButton)
    box.getChildren().addAll(tagControl, dammyBox, swapBox, removeButton)
    box.setHgrow(dammyBox, Priority.ALWAYS)
    $tagControls.add(tagControl)
    $tagBox.getChildren().add(box)
  }

  private void addEquivalentControl(String title, String name, List<String> registeredTitles) {
    HBox box = HBox.new(Measurement.rpx(5))
    HBox swapBox = HBox.new()
    ComboBox<String> titleControl = ComboBox.new()
    TextField nameControl = TextField.new()
    Button swapUpButton = UnfocusableButton.new("↑")
    Button swapDownButton = UnfocusableButton.new("↓")
    Button removeButton = UnfocusableButton.new("－")
    titleControl.setEditable(true)
    titleControl.getItems().addAll(registeredTitles)
    titleControl.setValue(title)
    titleControl.setPrefWidth(Measurement.rpx(120))
    titleControl.setMinWidth(Measurement.rpx(120))
    nameControl.setText(name)
    swapUpButton.getStyleClass().add("left-pill")
    swapUpButton.setOnAction() {
      swapEquivalentControl(box, -1)
    }
    swapDownButton.getStyleClass().add("right-pill")
    swapDownButton.setOnAction() {
      swapEquivalentControl(box, 1)
    }
    removeButton.setOnAction() {
      removeEquivalentControl(box)
    }
    swapBox.getChildren().addAll(swapUpButton, swapDownButton)
    box.getChildren().addAll(titleControl, nameControl, swapBox, removeButton)
    box.setHgrow(nameControl, Priority.ALWAYS)
    $equivalentTitleControls.add(titleControl)
    $equivalentNameControls.add(nameControl)
    $equivalentBox.getChildren().add(box)
    $equivalentBox.setVgrow(box, Priority.ALWAYS)
  }

  private void addInformationControl(String title, String text, List<String> registeredTitles) {
    HBox box = HBox.new(Measurement.rpx(5))
    HBox swapBox = HBox.new()
    HBox removeBox = HBox.new()
    ComboBox<String> titleControl = ComboBox.new()
    TextArea textControl = TextArea.new()
    Button swapUpButton = UnfocusableButton.new("↑")
    Button swapDownButton = UnfocusableButton.new("↓")
    Button removeButton = UnfocusableButton.new("－")
    swapBox.setAlignment(Pos.BOTTOM_CENTER)
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
    swapUpButton.getStyleClass().add("left-pill")
    swapUpButton.setOnAction() {
      swapInformationControl(box, -1)
    }
    swapDownButton.getStyleClass().add("right-pill")
    swapDownButton.setOnAction() {
      swapInformationControl(box, 1)
    }
    removeButton.setOnAction() {
      removeInformationControl(box)
    }
    swapBox.getChildren().addAll(swapUpButton, swapDownButton)
    removeBox.getChildren().add(removeButton)
    box.getChildren().addAll(titleControl, textControl, swapBox, removeBox)
    box.setHgrow(textControl, Priority.ALWAYS)
    $informationTitleControls.add(titleControl)
    $informationTextControls.add(textControl)
    $informationBox.getChildren().add(box)
    $informationBox.setVgrow(box, Priority.ALWAYS)
  }

  private void addVariationControl(String title, String name, List<String> registeredTitles) {
    HBox box = HBox.new(Measurement.rpx(5))
    HBox swapBox = HBox.new()
    ComboBox<String> titleControl = ComboBox.new()
    TextField nameControl = TextField.new()
    Button swapUpButton = UnfocusableButton.new("↑")
    Button swapDownButton = UnfocusableButton.new("↓")
    Button removeButton = UnfocusableButton.new("－")
    titleControl.setEditable(true)
    titleControl.getItems().addAll(registeredTitles)
    titleControl.setValue(title)
    titleControl.setPrefWidth(Measurement.rpx(120))
    titleControl.setMinWidth(Measurement.rpx(120))
    nameControl.setText(name)
    swapUpButton.getStyleClass().add("left-pill")
    swapUpButton.setOnAction() {
      swapVariationControl(box, -1)
    }
    swapDownButton.getStyleClass().add("right-pill")
    swapDownButton.setOnAction() {
      swapVariationControl(box, 1)
    }
    removeButton.setOnAction() {
      removeVariationControl(box)
    }
    swapBox.getChildren().addAll(swapUpButton, swapDownButton)
    box.getChildren().addAll(titleControl, nameControl, swapBox, removeButton)
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
    HBox swapBox = HBox.new()
    ComboBox<String> titleControl = ComboBox.new()
    TextField nameControl = TextField.new()
    Button chooseButton = Button.new("…")
    Button swapUpButton = UnfocusableButton.new("↑")
    Button swapDownButton = UnfocusableButton.new("↓")
    Button removeButton = UnfocusableButton.new("－")
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
    swapUpButton.getStyleClass().add("left-pill")
    swapUpButton.setOnAction() {
      swapRelationControl(box, -1)
    }
    swapDownButton.getStyleClass().add("right-pill")
    swapDownButton.setOnAction() {
      swapRelationControl(box, 1)
    }
    removeButton.setOnAction() {
      removeRelationControl(box)
    }
    nameBox.getChildren().addAll(nameControl, chooseButton)
    swapBox.getChildren().addAll(swapUpButton, swapDownButton)
    box.getChildren().addAll(titleControl, nameBox, dammyBox, swapBox, removeButton)
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
      Double vvalue = nodeRelativeMinY / (contentHeight - viewportHeight)
      $scrollPane.setVvalue(vvalue)
    } else if (nodeAbsoluteMaxY > viewportHeight) {
      Double vvalue = (nodeRelativeMaxY - viewportHeight) / (contentHeight - viewportHeight)
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
    Boolean showsId = Setting.getInstance().getShowsSlimeId()
    if (!showsId) {
      $gridPane.getChildren().remove($idControl)
      $gridPane.getChildren().remove($idLabel)
      for (Node node : $gridPane.getChildren()) {
        $gridPane.setRowIndex(node, $gridPane.getRowIndex(node) - 1)
      }
    }
    $idControl.setTextFormatter(TextFormatter.new(IntegerUnaryOperator.new()))
  }


  @InnerClass @Ziphilify
  private static class RelationRequest implements SlimeRelationRequest {

    private SlimeWord $word
    private SlimeRelation $relation = SlimeRelation.new(null, -1, "")
    private Node $box

    public RelationRequest(SlimeWord word, Node box) {
      $word = word
      $box = box
    }

    public SlimeWord getWord() {
      return $word
    }

    public SlimeRelation getRelation() {
      return $relation
    }

    public Node getBox() {
      return $box
    }

  }

}