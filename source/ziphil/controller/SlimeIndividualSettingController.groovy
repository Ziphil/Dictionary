package ziphil.controller

import groovy.transform.CompileStatic
import java.util.concurrent.Callable
import javafx.beans.binding.Bindings
import javafx.beans.binding.BooleanBinding
import javafx.beans.value.ObservableValue
import javafx.fxml.FXML
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.scene.control.Button
import javafx.scene.control.CheckBox
import javafx.scene.control.ComboBox
import javafx.scene.control.Label
import javafx.scene.control.TextField
import javafx.scene.layout.GridPane
import javafx.scene.layout.HBox
import javafx.scene.layout.Priority
import javafx.scene.text.Font
import ziphil.custom.ExtensionFilter
import ziphil.custom.ListSelectionView
import ziphil.custom.Measurement
import ziphil.custom.PermutableListView
import ziphil.custom.RichTextLanguage
import ziphil.custom.UtilityStage
import ziphil.dictionary.WordEditResult
import ziphil.dictionary.WordOrderType
import ziphil.dictionary.slime.SlimeDictionary
import ziphil.dictionary.slime.SlimeIndividualSetting
import ziphil.dictionary.slime.SlimeSearchParameter
import ziphil.dictionary.slime.SlimeWord
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class SlimeIndividualSettingController extends Controller<BooleanClass> {

  private static final String RESOURCE_PATH = "resource/fxml/controller/slime_individual_setting.fxml"
  private static final String TITLE = "個別設定"
  private static final Double DEFAULT_WIDTH = Measurement.rpx(520)
  private static final Double DEFAULT_HEIGHT = -1

  @FXML private ListSelectionView<String> $plainInformationTitleView
  @FXML private PermutableListView<String> $informationTitleOrderView
  @FXML private CheckBox $usesIndividualTitleOrderControl
  @FXML private ComboBox<String> $nameFontFamilyControl
  @FXML private CheckBox $usesDefaultNameFontFamilyControl
  @FXML private TextField $alphabetOrderControl
  @FXML private CheckBox $usesUnicodeWordOrderControl
  @FXML private CheckBox $usesNumberWordOrderControl
  @FXML private TextField $punctuationsControl
  @FXML private TextField $akrantiainSourceControl
  @FXML private ComboBox<String> $pronunciationTitleControl
  @FXML private GridPane $registeredParameterPane
  @FXML private List<TextField> $registeredParameterStringControls = ArrayList.new(10)
  @FXML private List<TextField> $registeredParameterNameControls = ArrayList.new(10)
  private List<SlimeSearchParameter> $registeredParameters
  private String $akrantiainSource
  private SlimeWord $defaultWord
  private SlimeDictionary $dictionary

  public SlimeIndividualSettingController(UtilityStage<? super BooleanClass> stage) {
    super(stage)
    loadResource(RESOURCE_PATH, TITLE, DEFAULT_WIDTH, DEFAULT_HEIGHT, false)
  }

  @FXML
  private void initialize() {
    setupNameFontFamilyControl()
    setupSearchParameterPane()
    setupAlphabetOrderControl()
    setupUsesWordOrderControls()
    setupInformationTitleOrderView()
    bindNameFontControlProperty()
  }

  public void prepare(SlimeDictionary dictionary) {
    $dictionary = dictionary
    SlimeIndividualSetting individualSetting = (SlimeIndividualSetting)dictionary.getIndividualSetting()
    ObservableList<String> plainInformationTitles = FXCollections.observableArrayList(dictionary.getPlainInformationTitles())
    ObservableList<String> normalInformationTitles = FXCollections.observableArrayList(dictionary.getRegisteredInformationTitles() - dictionary.getPlainInformationTitles())
    List<String> rawInformationTitleOrder = dictionary.getInformationTitleOrder()
    ObservableList<String> informationTitleOrder = FXCollections.observableArrayList(dictionary.getInformationTitleOrder() ?: dictionary.getRegisteredInformationTitles())
    SlimeWord defaultWord = (dictionary.getDefaultWord() != null) ? dictionary.copyWord(dictionary.getDefaultWord()) : null
    List<SlimeSearchParameter> registeredParameters = ArrayList.new(individualSetting.getRegisteredParameters())
    List<String> registeredParameterStrings = registeredParameters.collect{(it != null) ? it.toString() : ""}
    List<String> registeredParameterNames = ArrayList.new(individualSetting.getRegisteredParameterNames())
    $plainInformationTitleView.setSources(normalInformationTitles)
    $plainInformationTitleView.setTargets(plainInformationTitles)
    $informationTitleOrderView.setItems(informationTitleOrder)
    if (dictionary.getInformationTitleOrder() == null) {
      $usesIndividualTitleOrderControl.setSelected(true)
    }
    $nameFontFamilyControl.getSelectionModel().select(dictionary.getNameFontFamily())
    if (dictionary.getNameFontFamily() == null) {
      $usesDefaultNameFontFamilyControl.setSelected(true)
    }
    $alphabetOrderControl.setText(dictionary.getAlphabetOrder())
    $usesUnicodeWordOrderControl.setSelected(dictionary.getWordOrderType() == WordOrderType.UNICODE)
    $usesNumberWordOrderControl.setSelected(dictionary.getWordOrderType() == WordOrderType.IDENTIFIER)
    $punctuationsControl.setText(dictionary.getPunctuations().join(""))
    $akrantiainSourceControl.setText(dictionary.getAkrantiainSource())
    $pronunciationTitleControl.setValue(dictionary.getPronunciationTitle())
    $pronunciationTitleControl.getItems().addAll(dictionary.getRegisteredInformationTitles())
    for (Int i = 0 ; i < 10 ; i ++) {
      $registeredParameterStringControls[i].setText(registeredParameterStrings[i])
      $registeredParameterNameControls[i].setText(registeredParameterNames[i])
    }
    $registeredParameters = registeredParameters
    $akrantiainSource = dictionary.getAkrantiainSource()
    $defaultWord = defaultWord
  }

  @FXML
  protected void commit() {
    SlimeIndividualSetting individualSetting = (SlimeIndividualSetting)$dictionary.getIndividualSetting()
    List<String> plainInformationTitles = ArrayList.new($plainInformationTitleView.getTargets())
    Boolean usesIndividualOrder = $usesIndividualTitleOrderControl.isSelected()
    List<String> informationTitleOrder = (usesIndividualOrder) ? null : ArrayList.new($informationTitleOrderView.getItems())
    Boolean usesDefaultNameFontFamily = $usesDefaultNameFontFamilyControl.isSelected()
    String nameFontFamily = (usesDefaultNameFontFamily) ? null : $nameFontFamilyControl.getValue()
    String alphabetOrder = $alphabetOrderControl.getText() ?: ""
    WordOrderType wordOrderType = WordOrderType.CUSTOM
    if ($usesUnicodeWordOrderControl.isSelected()) {
      wordOrderType = WordOrderType.UNICODE
    } else if ($usesNumberWordOrderControl.isSelected()) {
      wordOrderType = WordOrderType.IDENTIFIER
    }
    List<String> punctuations = $punctuationsControl.getText().split("").toList()
    String akrantiainSource = $akrantiainSource
    String pronunciationTitle = $pronunciationTitleControl.getValue()
    SlimeWord defaultWord = $defaultWord
    List<SlimeSearchParameter> registeredParameters = $registeredParameters
    List<String> registeredParameterNames = $registeredParameterNameControls.collect{it.getText()}
    $dictionary.setPlainInformationTitles(plainInformationTitles)
    $dictionary.setInformationTitleOrder(informationTitleOrder)
    $dictionary.setNameFontFamily(nameFontFamily)
    $dictionary.setAlphabetOrder(alphabetOrder)
    $dictionary.setWordOrderType(wordOrderType)
    $dictionary.setPunctuations(punctuations)
    $dictionary.setAkrantiainSource(akrantiainSource)
    $dictionary.setPronunciationTitle(pronunciationTitle)
    $dictionary.setDefaultWord(defaultWord)
    individualSetting.setRegisteredParameters(registeredParameters)
    individualSetting.setRegisteredParameterNames(registeredParameterNames)
    $stage.commit(true)
  }

  @FXML
  private void editSnoj() {
    UtilityStage<FileStringChooserController.Result> nextStage = createStage()
    FileStringChooserController controller = FileStringChooserController.new(nextStage)
    ExtensionFilter filter = ExtensionFilter.new("snojファイル", "snoj")
    FileStringChooserController.Result previousResult = FileStringChooserController.Result.ofString($akrantiainSource)  
    controller.prepare(filter, RichTextLanguage.AKRANTIAIN, previousResult)
    nextStage.showAndWait()
    if (nextStage.isCommitted()) {
      FileStringChooserController.Result result = nextStage.getResult()
      if (result.isFileSelected()) {
        File file = result.getFile()
        if (file != null) {
          String source = file.getText()
          $akrantiainSource = source
          $akrantiainSourceControl.setText(source)
        }
      } else {
        String source = result.getString()
        $akrantiainSource = source
        $akrantiainSourceControl.setText(source)
      }
    }
  }

  @FXML
  private void removeSnoj() {
    $akrantiainSource = null
    $akrantiainSourceControl.setText("")
  }

  @FXML
  private void removePronunciationTitle() {
    $pronunciationTitleControl.setValue(null)
  }

  @FXML
  private void editDefaultWord() {
    SlimeWord defaultWord = $defaultWord ?: $dictionary.createWord(null)
    UtilityStage<WordEditResult> nextStage = createStage()
    SlimeEditorController controller = SlimeEditorController.new(nextStage)
    controller.prepare(defaultWord, $dictionary, false, false)
    nextStage.showAndWait()
  }

  private void editSearchParameter(Int index) {
    UtilityStage<SlimeSearchParameter> nextStage = createStage(null)
    SlimeSearcherController controller = SlimeSearcherController.new(nextStage)
    controller.prepare($dictionary, $registeredParameters[index])
    nextStage.showAndWait()
    if (nextStage.isCommitted()) {
      SlimeSearchParameter parameter = nextStage.getResult()
      $registeredParameters[index] = parameter
      $registeredParameterStringControls[index].setText(parameter.toString())
    }
  }

  private void deregisterSearchParameter(Int index) {
    $registeredParameters[index] = (SlimeSearchParameter)null
    $registeredParameterStringControls[index].setText("")
    $registeredParameterNameControls[index].setText("")
  }

  private void setupNameFontFamilyControl() {
    List<String> fontFamilies = Font.getFamilies()
    $nameFontFamilyControl.getItems().addAll(fontFamilies)
  }

  private void setupSearchParameterPane() {
    for (Int i = 0 ; i < 10 ; i ++) {
      Int j = i
      Label numberLabel = Label.new("検索条件${(i + 1) % 10}:")
      HBox box = HBox.new(Measurement.rpx(5))
      HBox innerBox = HBox.new()
      TextField registeredParameterStringControl = TextField.new()
      TextField registeredParameterNameControl = TextField.new()
      Button editButton = Button.new("…")
      Button deregisterButton = Button.new("解除")
      registeredParameterStringControl.setEditable(false)
      registeredParameterStringControl.getStyleClass().add("left-pill")
      registeredParameterNameControl.setPrefWidth(Measurement.rpx(120))
      registeredParameterNameControl.setMinWidth(Measurement.rpx(120))
      editButton.setMinWidth(Button.USE_PREF_SIZE)
      editButton.getStyleClass().add("right-pill")
      deregisterButton.setPrefWidth(Measurement.rpx(70))
      deregisterButton.setMinWidth(Measurement.rpx(70))
      editButton.setOnAction() {
        editSearchParameter(j)
      }
      deregisterButton.setOnAction() {
        deregisterSearchParameter(j)
      }
      innerBox.getChildren().addAll(registeredParameterStringControl, editButton)
      innerBox.setHgrow(registeredParameterStringControl, Priority.ALWAYS)
      box.getChildren().addAll(registeredParameterNameControl, innerBox, deregisterButton)
      box.setHgrow(innerBox, Priority.ALWAYS)
      $registeredParameterStringControls[i] = registeredParameterStringControl
      $registeredParameterNameControls[i] = registeredParameterNameControl
      $registeredParameterPane.add(numberLabel, 0, i)
      $registeredParameterPane.add(box, 1, i)
    }
  }

  private void setupAlphabetOrderControl() {
    Callable<BooleanClass> function = (Callable){
      return $usesUnicodeWordOrderControl.isSelected() || $usesNumberWordOrderControl.isSelected()
    }
    BooleanBinding binding = Bindings.createBooleanBinding(function, $usesUnicodeWordOrderControl.selectedProperty(), $usesNumberWordOrderControl.selectedProperty()) 
    $alphabetOrderControl.disableProperty().bind(binding)
  }

  private void setupUsesWordOrderControls() {
    $usesUnicodeWordOrderControl.selectedProperty().addListener() { ObservableValue<? extends BooleanClass> observableValue, BooleanClass oldValue, BooleanClass newValue ->
      if (newValue == true) {
        $usesNumberWordOrderControl.setSelected(false)
      }
    }
    $usesNumberWordOrderControl.selectedProperty().addListener() { ObservableValue<? extends BooleanClass> observableValue, BooleanClass oldValue, BooleanClass newValue ->
      if (newValue == true) {
        $usesUnicodeWordOrderControl.setSelected(false)
      }
    }
  }

  private void setupInformationTitleOrderView() {
    $informationTitleOrderView.disableProperty().bind($usesIndividualTitleOrderControl.selectedProperty())
  }

  private void bindNameFontControlProperty() {
    $nameFontFamilyControl.disableProperty().bind($usesDefaultNameFontFamilyControl.selectedProperty())
  }

}