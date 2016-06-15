package ziphil.dictionary

import groovy.transform.CompileStatic
import java.util.regex.Matcher
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.control.Label
import javafx.scene.layout.HBox
import javafx.scene.layout.Pane
import javafx.scene.layout.VBox
import javafx.scene.text.Text
import javafx.scene.text.TextFlow
import ziphil.module.Setting
import ziphil.module.Strings


@CompileStatic @Newify
public class ShaleiaWord extends Word {

  private static final Map<String, Integer> ALPHABET_ORDER = [("s"): 1, ("z"): 2, ("t"): 3, ("d"): 4, ("k"): 5, ("g"): 6, ("f"): 7, ("v"): 8, ("p"): 9, ("b"): 10, ("c"): 11, ("q"): 12,
                                                              ("x"): 13, ("j"): 14, ("r"): 15, ("l"): 16, ("m"): 17, ("n"): 18, ("h"): 19, ("y"): 20, ("a"): 21, ("â"): 22, ("á"): 23, ("à"): 24,
                                                              ("e"): 25, ("ê"): 26, ("é"): 27, ("è"): 28, ("i"): 29, ("î"): 30, ("í"): 31, ("ì"): 32, ("o"): 31, ("ô"): 32, ("ò"): 33, ("u"): 34,
                                                              ("û"): 35, ("ù"): 36]

  private String $name = ""
  private String $uniqueName = ""
  private List<String> $equivalents = ArrayList.new()
  private String $data = ""
  private String $content = ""
  private VBox $contentPane = VBox.new()

  public ShaleiaWord(String uniqueName, String data) {
    update(uniqueName, data)
    setupContentPane()
  }

  public void update(String uniqueName, String data) {
    HBox headBox = HBox.new()
    VBox equivalentBox = VBox.new()
    VBox otherBox = VBox.new()
    VBox synonymBox = VBox.new()
    Boolean hasOther = false
    Boolean hasSynonym = false
    Boolean modifiesPunctuation = Setting.getInstance().modifiesPunctuation() ?: false
    $name = uniqueName.replaceAll(/\+|~/, "")
    $uniqueName = uniqueName
    $data = data
    $content = uniqueName + "\n" + data
    $contentPane.getChildren().clear()
    $contentPane.getChildren().addAll(headBox, equivalentBox, otherBox, synonymBox)
    data.eachLine() { String line ->
      Matcher creationDateMatcher = line =~ /^\+\s*(\d+)\s*〈(.+)〉\s*$/
      Matcher hiddenEquivalentMatcher = line =~ /^\=:\s*(.+)$/
      Matcher equivalentMatcher = line =~ /^\=\s*〈(.+)〉\s*(.+)$/
      Matcher meaningMatcher = line =~ /^M>\s*(.+)$/
      Matcher ethymologyMatcher = line =~ /^E>\s*(.+)$/
      Matcher usageMatcher = line =~ /^U>\s*(.+)$/
      Matcher phraseMatcher = line =~ /^P>\s*(.+)$/
      Matcher noteMatcher = line =~ /^N>\s*(.+)$/
      Matcher exampleMatcher = line =~ /^S>\s*(.+)$/
      Matcher synonymMatcher = line =~ /^\-\s*(.+)$/
      if (headBox.getChildren().isEmpty()) {
        String name = uniqueName.replaceAll(/\+|~/, "")
        addNameNode(headBox, name)
      }
      if (creationDateMatcher.matches()) {
        String creationDate = creationDateMatcher.group(1)
        String wholeClass = creationDateMatcher.group(2)
        addCreationDateNode(headBox, wholeClass, creationDate)
      }
      if (equivalentMatcher.matches()) {
        String localClass = equivalentMatcher.group(1)
        String equivalent = equivalentMatcher.group(2)
        addEquivalentNode(equivalentBox, localClass, equivalent)
        List<String> equivalents = equivalent.replaceAll(/(\(.+\)|\{|\}|\/|\s)/, "").split(/,/).toList()
        $equivalents.addAll(equivalents)
      }
      if (hiddenEquivalentMatcher.matches()) {
        String equivalent = hiddenEquivalentMatcher.group(1)
        List<String> equivalents = equivalent.replaceAll(/(\(.+\)|\{|\}|\/|\s)/, "").split(/,/).toList()
        $equivalents.addAll(equivalents)
      }
      if (meaningMatcher.matches()) {
        String meaning = meaningMatcher.group(1)
        addOtherNode(otherBox, "語義", meaning, modifiesPunctuation)
        hasOther = true
      }
      if (ethymologyMatcher.matches()) {
        String ethymology = ethymologyMatcher.group(1)
        addOtherNode(otherBox, "語源", ethymology, modifiesPunctuation)
        hasOther = true
      }
      if (usageMatcher.matches()) {
        String usage = usageMatcher.group(1)
        addOtherNode(otherBox, "語法", usage, modifiesPunctuation)
        hasOther = true
      }
      if (phraseMatcher.matches()) {
        String phrase = phraseMatcher.group(1)
        addOtherNode(otherBox, "成句", phrase, modifiesPunctuation)
        hasOther = true
      }
      if (noteMatcher.matches()) {
        String note = noteMatcher.group(1)
        addOtherNode(otherBox, "備考", note, modifiesPunctuation)
        hasOther = true
      }
      if (exampleMatcher.matches()) {
        String example = exampleMatcher.group(1)
        addOtherNode(otherBox, "例文", example, modifiesPunctuation)
        hasOther = true
      }
      if (synonymMatcher.matches()) {
        String synonym = synonymMatcher.group(1)
        addSynonymNode(synonymBox, synonym)
        hasSynonym = true
      }
    }
    if (hasOther) {
      $contentPane.setMargin(equivalentBox, Insets.new(0, 0, 5, 0))
    }
    if (hasSynonym) {
      $contentPane.setMargin(otherBox, Insets.new(0, 0, 5, 0))
    }
  }

  private void addNameNode(HBox box, String name) {
    Label nameText = Label.new(name)
    nameText.getStyleClass().addAll("content-text", "head-name", "shaleia-head-name")
    box.getChildren().add(nameText)
    box.setMargin(nameText, Insets.new(0, 10, 0, 0))
    box.setAlignment(Pos.CENTER_LEFT)
  }
 
  private void addCreationDateNode(HBox box, String wholeClass, String creationDate) {
    Label wholeClassText = Label.new(wholeClass)
    Label creationDateText = Label.new(" " + creationDate)
    wholeClassText.getStyleClass().addAll("content-text", "shaleia-whole-class")
    creationDateText.getStyleClass().addAll("content-text", "shaleia-creation-date")
    box.getChildren().addAll(wholeClassText, creationDateText)
  }

  private void addEquivalentNode(VBox box, String localClass, String equivalent) {
    TextFlow textFlow = TextFlow.new()
    Label localClassText = Label.new(localClass)
    List<Text> equivalentTexts = createRichTexts(" " + equivalent)
    localClassText.getStyleClass().addAll("content-text", "shaleia-local-class")
    equivalentTexts.each() { Text equivalentText ->
      equivalentText.getStyleClass().addAll("content-text", "shaleia-equivalent")
    }
    textFlow.getChildren().add(localClassText)
    textFlow.getChildren().addAll(equivalentTexts)
    box.getChildren().add(textFlow)
  }

  private void addOtherNode(VBox box, String item, String other, Boolean modifiesPunctuation) {
    String newOther = (modifiesPunctuation) ? Strings.modifyPunctuation(other) : other
    TextFlow textFlow = TextFlow.new()
    Label itemText = Label.new("【${item}】")
    List<Text> otherTexts = createRichTexts(newOther)
    itemText.getStyleClass().addAll("content-text", "shaleia-item")
    otherTexts.each() { Text otherText ->
      otherText.getStyleClass().addAll("content-text")
    }
    textFlow.getChildren().addAll(otherTexts)
    box.getChildren().addAll(itemText, textFlow)
  }

  private void addSynonymNode(VBox box, String synonym) {
    TextFlow textFlow = TextFlow.new()
    Label itemText = Label.new("cf: ")
    List<Text> synonymTexts = createRichTexts(synonym)
    itemText.getStyleClass().addAll("content-text", "shaleia-item")
    synonymTexts.each() { Text synonymText ->
      synonymText.getStyleClass().addAll("content-text")
    }
    textFlow.getChildren().add(itemText)
    textFlow.getChildren().addAll(synonymTexts)
    box.getChildren().add(textFlow)
  }

  private List<Text> createRichTexts(String string) {
    List<Text> texts = ArrayList.new()
    Text text = Text.new(string.replaceAll(/\{|\}|\[|\]|\//, ""))
    texts.add(text)
    return texts
  }

  public List<Integer> listForComparison() {
    List<String> splittedString = $uniqueName.split("").toList()
    List<Integer> convertedString = splittedString.collect{character -> ALPHABET_ORDER.get(character, -1)}
    if (convertedString[0] == -1) {
      convertedString.remove(0)
      convertedString.add(-2)
    }
    return convertedString
  }

  private void setupContentPane() {
    Setting setting = Setting.getInstance()
    String fontFamily = setting.getContentFontFamily()
    Integer fontSize = setting.getContentFontSize()
    if (fontFamily != null && fontSize != null) {
      $contentPane.setStyle("-fx-font-family: \"${fontFamily}\"; -fx-font-size: ${fontSize}")
    }
  }

  public static ShaleiaWord emptyWord() {
    return ShaleiaWord.new("", "")
  }

  public static ShaleiaWord copyFrom(ShaleiaWord oldWord) {
    String name = oldWord.getName()
    String data = oldWord.getData()
    return ShaleiaWord.new(name, data)
  }

  public String getName() {
    return $name
  }

  public String getUniqueName() {
    return $uniqueName
  }

  public List<String> getEquivalents() {
    return $equivalents
  }

  public String getData() {
    return $data
  }

  public String getContent() {
    return $content
  }

  public Pane getContentPane() {
    return $contentPane
  }

}