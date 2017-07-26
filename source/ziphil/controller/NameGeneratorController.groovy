package ziphil.controller

import groovy.transform.CompileStatic
import javafx.beans.value.ObservableValue
import javafx.fxml.FXML
import javafx.scene.Node
import javafx.scene.control.ComboBox
import javafx.scene.control.Label
import javafx.scene.control.Spinner
import javafx.scene.control.Tab
import javafx.scene.control.TabPane
import javafx.scene.control.TextField
import javafx.scene.control.TextFormatter
import javafx.scene.layout.GridPane
import javafx.scene.layout.HBox
import javafx.stage.Modality
import javafx.stage.StageStyle
import ziphil.custom.ExtensionFilter
import ziphil.custom.IntegerUnaryOperator
import ziphil.custom.Measurement
import ziphil.custom.StringListEditor
import ziphil.custom.UtilityStage
import ziphil.dictionary.EditableDictionary
import ziphil.dictionary.EquivalentCollection
import ziphil.dictionary.EquivalentCollectionType
import ziphil.dictionary.PseudoWord
import ziphil.dictionary.Word
import ziphil.module.EasyNameGenerator
import ziphil.module.NameGenerator
import ziphil.module.zatlin.Zatlin
import ziphil.module.zatlin.ZatlinException
import ziphil.module.zatlin.ZatlinParseException
import ziphilib.transform.InnerClass
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class NameGeneratorController extends Controller<NameGeneratorController.Result> {

  private static final String RESOURCE_PATH = "resource/fxml/controller/name_generator.fxml"
  private static final String TITLE = "単語自動生成"
  private static final Double DEFAULT_WIDTH = Measurement.rpx(480)
  private static final Double DEFAULT_HEIGHT = -1

  @FXML private TabPane $tabPane
  @FXML private StringListEditor $vowelsControl
  @FXML private StringListEditor $consonantsControl
  @FXML private StringListEditor $syllablePatternsControl
  @FXML private Spinner<IntegerClass> $minSyllableSizeControl
  @FXML private Spinner<IntegerClass> $maxSyllableSizeControl
  @FXML private TextField $zatlinSourceControl
  private String $zatlinSource = ""
  private Boolean $usesCollection

  public NameGeneratorController(UtilityStage<? super NameGeneratorController.Result> stage) {
    super(stage)
    loadResource(RESOURCE_PATH, TITLE, DEFAULT_WIDTH, DEFAULT_HEIGHT, false)
  }

  @FXML
  private void initialize() {
    setupSyllableSizeControls()
    setupCollectionTypeControls()
    setupIntegerControls()
  }

  public void prepare(Boolean usesCollection, Config previousConfig) {
    $usesCollection = usesCollection
    prepareEasyControls(previousConfig)
    prepareZatlinControls(previousConfig)
    prepareTabPane(previousConfig)
  }

  public void prepare(Boolean usesCollection) {
    prepare(usesCollection, null)
  }

  public void prepare() {
    prepare(true, null)
  }

  private void prepareEasyControls(Config previousConfig) {
    if (previousConfig != null) {
      EasyNameGenerator.Config easyConfig = previousConfig.getEasyConfig()
      $vowelsControl.getStrings().addAll(easyConfig.getVowels())
      $consonantsControl.getStrings().addAll(easyConfig.getConsonants())
      $syllablePatternsControl.getStrings().addAll(easyConfig.getSyllablePatterns())
      $minSyllableSizeControl.getValueFactory().setValue(easyConfig.getMinSyllableSize())
      $maxSyllableSizeControl.getValueFactory().setValue(easyConfig.getMaxSyllableSize())
    }
  }

  private void prepareZatlinControls(Config previousConfig) {
    if (previousConfig != null) {
      String zatlinSource = previousConfig.getZatlinSource()
      $zatlinSourceControl.setText(zatlinSource)
      $zatlinSource = zatlinSource
    }
  }

  private void prepareTabPane(Config previousConfig) {
    if (previousConfig != null) {
      for (Tab tab : $tabPane.getTabs()) {
        if (tab.getText() == previousConfig.getSelectedTabText()) {
          $tabPane.getSelectionModel().select(tab)
          break
        }
      }
    }
    if (!$usesCollection) {
      for (Tab tab : $tabPane.getTabs()) {
        Node node = tab.getContent()
        if (node instanceof GridPane) {
          List<Node> removedChildren = ArrayList.new()
          for (Node childNode : node.getChildren()) {
            List<String> styleClass = childNode.getStyleClass()
            if (styleClass.contains("option-collection")) {
              removedChildren.add(childNode)
            }
          }
          node.getChildren().removeAll(removedChildren)
        }
      }
    }
  }

  @FXML
  private void editZatlin() {
    UtilityStage<FileStringChooserController.Result> nextStage = UtilityStage.new(StageStyle.UTILITY)
    FileStringChooserController controller = FileStringChooserController.new(nextStage)
    ExtensionFilter filter = ExtensionFilter.new("生成規則ファイル", "ztl")
    FileStringChooserController.Result previousResult = FileStringChooserController.Result.ofString($zatlinSource)
    nextStage.initModality(Modality.APPLICATION_MODAL)
    nextStage.initOwner($stage)     
    controller.prepare(filter, previousResult)
    nextStage.showAndWait()
    if (nextStage.isCommitted()) {
      FileStringChooserController.Result result = nextStage.getResult()
      if (result.isFileSelected()) {
        File file = result.getFile()
        if (file != null) {
          String source = file.getText()
          $zatlinSource = source
          $zatlinSourceControl.setText(source)
        }
      } else {
        String source = result.getString()
        $zatlinSource = source
        $zatlinSourceControl.setText(source)
      }
    }
  }

  @FXML
  private void showEquivalents() {
    EquivalentCollectionType collectionType = selectedCollectionType()
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
    EquivalentCollectionType collectionType = selectedCollectionType()
    NameGenerator generator = createGenerator()
    Result result = null
    if ($usesCollection) {
      if (collectionType != null) {
        EquivalentCollection collection = EquivalentCollection.load(collectionType)
        List<String> names = ArrayList.new()
        for (PseudoWord pseudoWord : collection.getPseudoWords()) {
          try {
            String name = (generator != null) ? generator.generate() : ""
            names.add(name)
          } catch (ZatlinException exception) {
            names.add("")
          }
        }
        result = Result.new(collection.getPseudoWords(), names)
      } else {
        result = Result.new(ArrayList.new(), ArrayList.new())
      }
    } else {
      List<String> names = ArrayList.new()
      try {
        String name = (generator != null) ? generator.generate() : ""
        names.add(name)
      } catch (ZatlinException exception) {
        names.add("")
      }
      result = Result.new(null, names)
    }
    $stage.commit(result)
  }

  private NameGenerator createGenerator() {
    if ($tabPane.getSelectionModel().getSelectedIndex() == 0) {
      EasyNameGenerator generator = EasyNameGenerator.new()
      generator.load(createEasyConfig())
      return generator
    } else {
      Zatlin zatlin = Zatlin.new()
      try {
        zatlin.load($zatlinSource)
        return zatlin
      } catch (ZatlinParseException exception) {
        return null
      } 
    }
  }

  public Config createConfig() {
    Config config = Config.new()
    config.setEasyConfig(createEasyConfig())
    config.setZatlinSource($zatlinSource)
    config.setSelectedTabText($tabPane.getSelectionModel().getSelectedItem().getText())
    return config
  }

  private EasyNameGenerator.Config createEasyConfig() {
    EasyNameGenerator.Config easyConfig = EasyNameGenerator.Config.new()
    easyConfig.setVowels($vowelsControl.getStrings())
    easyConfig.setConsonants($consonantsControl.getStrings())
    easyConfig.setSyllablePatterns($syllablePatternsControl.getStrings())
    easyConfig.setMinSyllableSize($minSyllableSizeControl.getValue())
    easyConfig.setMaxSyllableSize($maxSyllableSizeControl.getValue())
    return easyConfig
  }

  private EquivalentCollectionType selectedCollectionType() {
    Tab selectedTab = $tabPane.getSelectionModel().getSelectedItem()
    Node node = selectedTab.getContent().lookup(".option-type")
    if (node instanceof ComboBox) {
      return node.getValue()
    } else {
      return null
    }
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

  private void setupCollectionTypeControls() {
    List<EquivalentCollectionType> collectionTypes = EquivalentCollectionType.getCollectionTypes()
    for (Node node : $tabPane.lookupAll(".option-type")) {
      if (node instanceof ComboBox) {
        ComboBox collectionTypeControl = (ComboBox)node
        collectionTypeControl.getItems().addAll(collectionTypes)
        if (!collectionTypes.isEmpty()) {
          collectionTypeControl.setValue(collectionTypes.first())
        }
      }
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


  @InnerClass @Ziphilify
  public static class Config {

    private EasyNameGenerator.Config $easyConfig
    private String $zatlinSource
    private String $selectedTabText

    public EasyNameGenerator.Config getEasyConfig() {
      return $easyConfig
    }

    public void setEasyConfig(EasyNameGenerator.Config easyConfig) {
      $easyConfig = easyConfig
    }

    public String getZatlinSource() {
      return $zatlinSource
    }

    public void setZatlinSource(String zatlinSource) {
      $zatlinSource = zatlinSource
    }

    public String getSelectedTabText() {
      return $selectedTabText
    }

    public void setSelectedTabText(String selectedTabText) {
      $selectedTabText = selectedTabText
    }

  }

}