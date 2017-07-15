package ziphil.controller

import groovy.transform.CompileStatic
import javafx.beans.value.ObservableValue
import javafx.fxml.FXML
import javafx.scene.Node
import javafx.scene.control.ComboBox
import javafx.scene.control.Label
import javafx.scene.control.Spinner
import javafx.scene.control.TextFormatter
import javafx.scene.layout.GridPane
import javafx.scene.layout.HBox
import javafx.stage.Modality
import javafx.stage.StageStyle
import ziphil.custom.IntegerUnaryOperator
import ziphil.custom.Measurement
import ziphil.custom.StringListEditor
import ziphil.custom.UtilityStage
import ziphil.dictionary.EditableDictionary
import ziphil.dictionary.EquivalentCollection
import ziphil.dictionary.EquivalentCollectionType
import ziphil.dictionary.PseudoWord
import ziphil.dictionary.Word
import ziphil.module.NameGenerator
import ziphilib.transform.InnerClass
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class NameGeneratorController extends Controller<NameGeneratorController.Result> {

  private static final String RESOURCE_PATH = "resource/fxml/controller/name_generator.fxml"
  private static final String TITLE = "単語自動生成"
  private static final Double DEFAULT_WIDTH = Measurement.rpx(480)
  private static final Double DEFAULT_HEIGHT = -1

  @FXML private GridPane $gridPane
  @FXML private StringListEditor $vowelsControl
  @FXML private StringListEditor $consonantsControl
  @FXML private StringListEditor $syllablePatternsControl
  @FXML private Spinner<IntegerClass> $minSyllableSizeControl
  @FXML private Spinner<IntegerClass> $maxSyllableSizeControl
  @FXML private Label $collectionTypeLabel
  @FXML private HBox $collectionTypeBox
  @FXML private ComboBox $collectionTypeControl
  private Boolean $usesCollection

  public NameGeneratorController(UtilityStage<NameGeneratorController.Result> stage) {
    super(stage)
    loadResource(RESOURCE_PATH, TITLE, DEFAULT_WIDTH, DEFAULT_HEIGHT, false)
  }

  @FXML
  private void initialize() {
    setupSyllableSizeControls()
    setupCollectionTypeControl()
    setupIntegerControls()
  }

  public void prepare(Boolean usesCollection) {
    $usesCollection = usesCollection
    if (!usesCollection) {
      List<Node> removedChildren = ArrayList.new()
      for (Node node : $gridPane.getChildren()) {
        List<String> styleClass = node.getStyleClass()
        if (styleClass.contains("option") && styleClass.contains("collection")) {
          removedChildren.add(node)
        }
      }
      $gridPane.getChildren().removeAll(removedChildren)
    }
  }

  public void prepare() {
    prepare(true)
  }

  @FXML
  private void showEquivalents() {
    EquivalentCollectionType collectionType = $collectionTypeControl.getValue()
    if (collectionType != null) {
      EquivalentCollection collection = EquivalentCollection.load(collectionType)
      UtilityStage<Void> nextStage = UtilityStage.new(StageStyle.UTILITY)
      EquivalentCollectionController controller = EquivalentCollectionController.new(nextStage)
      nextStage.initModality(Modality.APPLICATION_MODAL)
      nextStage.initOwner($stage)
      controller.prepare(collection)
      nextStage.showAndWait()
    }
  }

  protected void commit() {
    List<Word> words = ArrayList.new()
    EquivalentCollectionType collectionType = $collectionTypeControl.getValue()
    NameGenerator generator = NameGenerator.new()
    Result result = null
    generator.setVowels($vowelsControl.getStrings())
    generator.setConsonants($consonantsControl.getStrings())
    generator.setSyllablePatterns($syllablePatternsControl.getStrings())
    generator.setMinSyllableSize($minSyllableSizeControl.getValue())
    generator.setMaxSyllableSize($maxSyllableSizeControl.getValue())
    if ($usesCollection) {
      if (collectionType != null) {
        EquivalentCollection collection = EquivalentCollection.load(collectionType)
        List<String> names = ArrayList.new()
        for (PseudoWord pseudoWord : collection.getPseudoWords()) {
          String name = generator.generate()
          names.add(name)
        }
        result = Result.new(collection.getPseudoWords(), names)
      } else {
        result = Result.new(ArrayList.new(), ArrayList.new())
      }
    } else {
      List<String> names = ArrayList.new()
      String name = generator.generate()
      names.add(name)
      result = Result.new(null, names)
    }
    $stage.commit(result)
  }

  private void setupSyllableSizeControls() {
    $minSyllableSizeControl.valueProperty().addListener() { ObservableValue<? extends IntegerClass> observableValue, IntegerClass oldValue, IntegerClass newValue ->
      if (newValue > $maxSyllableSizeControl.getValue()) {
        $maxSyllableSizeControl.getValueFactory().setValue(newValue)
      }
    }
    $maxSyllableSizeControl.valueProperty().addListener() { ObservableValue<? extends IntegerClass> observableValue, IntegerClass oldValue, IntegerClass newValue ->
      if (newValue < $minSyllableSizeControl.getValue()) {
        $minSyllableSizeControl.getValueFactory().setValue(newValue)
      }
    }
  }

  private void setupCollectionTypeControl() {
    List<EquivalentCollectionType> collectionTypes = EquivalentCollectionType.getCollectionTypes()
    $collectionTypeControl.getItems().addAll(collectionTypes)
    if (!collectionTypes.isEmpty()) {
      $collectionTypeControl.setValue(collectionTypes.first())
    }
  }

  private void setupIntegerControls() {
    $minSyllableSizeControl.getEditor().setTextFormatter(TextFormatter.new(IntegerUnaryOperator.new()))
    $maxSyllableSizeControl.getEditor().setTextFormatter(TextFormatter.new(IntegerUnaryOperator.new()))
  }


  @InnerClass @Ziphilify
  public static class Result {

    private List<PseudoWord> $pseudoWords
    private List<String> $names

    public Result(List<PseudoWord> pseudoWords, List<String> names) {
      $pseudoWords = pseudoWords
      $names = names
    }

    public List<PseudoWord> getPseudoWords() {
      return $pseudoWords
    }

    public List<String> getNames() {
      return $names
    }

  }

}