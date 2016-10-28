package ziphil.dictionary

import groovy.transform.CompileStatic
import java.util.function.Consumer
import java.util.regex.Matcher
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Node
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


@CompileStatic @Newify
public class ShaleiaWord extends Word {

  public static final String SHALEIA_HEAD_NAME_CLASS = "shaleia-head-name"
  public static final String SHALEIA_EQUIVALENT_CLASS = "shaleia-equivalent"
  public static final String SHALEIA_WHOLE_CLASS_CLASS = "shaleia-whole-class"
  public static final String SHALEIA_LOCAL_CLASS_CLASS = "shaleia-local-class"
  public static final String SHALEIA_CREATION_DATE_CLASS = "shaleia-creation-date"
  public static final String SHALEIA_TITLE_CLASS = "shaleia-title"
  public static final String SHALEIA_NAME_CLASS = "shaleia-name"
  public static final String SHALEIA_LINK_CLASS = "shaleia-link"
  public static final String SHALEIA_ITALIC_CLASS = "shaleia-italic"

  private ShaleiaDictionary $dictionary
  private String $uniqueName = ""
  private String $data = ""
  private String $comparisonString = ""

  public ShaleiaWord(String uniqueName, String data) {
    update(uniqueName, data)
    setupContentPane()
  }

  public void update(String uniqueName, String data) {
    $name = uniqueName.replaceAll(/\+|~/, "")
    $uniqueName = uniqueName
    $data = data
    $content = uniqueName + "\n" + data
    data.eachLine() { String line ->
      Matcher matcher = line =~ /^\=(?:\:)?\s*(?:〈(.+)〉)?\s*(.+)$/
      if (matcher.matches()) {
        String equivalent = matcher.group(2)
        List<String> equivalents = equivalent.replaceAll(/(\(.+\)|\{|\}|\/|\s)/, "").split(/,/).toList()
        $equivalents.addAll(equivalents)
      }
    }
    $isChanged = true
  }

  public void createContentPane() {
    HBox headBox = HBox.new()
    VBox equivalentBox = VBox.new()
    VBox otherBox = VBox.new()
    VBox synonymBox = VBox.new()
    Boolean hasOther = false
    Boolean hasSynonym = false
    Boolean modifiesPunctuation = Setting.getInstance().getModifiesPunctuation() ?: false
    $contentPane.getChildren().clear()
    $contentPane.getChildren().addAll(headBox, equivalentBox, otherBox, synonymBox)
    $data.eachLine() { String line ->
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
        String name = $uniqueName.replaceAll(/\+|~/, "")
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
      }
      if (hiddenEquivalentMatcher.matches()) {
        String equivalent = hiddenEquivalentMatcher.group(1)
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
      $contentPane.setMargin(equivalentBox, Insets.new(0, 0, Measurement.rpx(3), 0))
    }
    if (hasSynonym) {
      $contentPane.setMargin(otherBox, Insets.new(0, 0, Measurement.rpx(3), 0))
    }
    $isChanged = false
  }

  private void addNameNode(HBox box, String name) {
    Text nameText = Text.new(name + "  ")
    nameText.getStyleClass().addAll(CONTENT_CLASS, HEAD_NAME_CLASS, SHALEIA_HEAD_NAME_CLASS)
    box.getChildren().add(nameText)
    box.setAlignment(Pos.CENTER_LEFT)
  }
 
  private void addCreationDateNode(HBox box, String wholeClass, String creationDate) {
    Label wholeClassText = Label.new(wholeClass)
    Text creationDateText = Text.new(" " + creationDate)
    wholeClassText.getStyleClass().addAll(CONTENT_CLASS, SHALEIA_WHOLE_CLASS_CLASS)
    creationDateText.getStyleClass().addAll(CONTENT_CLASS, SHALEIA_CREATION_DATE_CLASS)
    box.getChildren().addAll(wholeClassText, creationDateText)
  }

  private void addEquivalentNode(VBox box, String localClass, String equivalent) {
    TextFlow textFlow = TextFlow.new()
    Label localClassText = Label.new(localClass)
    List<Text> equivalentTexts = createRichTexts(" " + equivalent)
    localClassText.getStyleClass().addAll(CONTENT_CLASS, SHALEIA_LOCAL_CLASS_CLASS)
    equivalentTexts.each() { Text equivalentText ->
      equivalentText.getStyleClass().addAll(CONTENT_CLASS, SHALEIA_EQUIVALENT_CLASS)
    }
    textFlow.getChildren().add(localClassText)
    textFlow.getChildren().addAll(equivalentTexts)
    box.getChildren().add(textFlow)
  }

  private void addOtherNode(VBox box, String title, String other, Boolean modifiesPunctuation) {
    String newOther = (modifiesPunctuation) ? Strings.modifyPunctuation(other) : other
    TextFlow titleTextFlow = TextFlow.new()
    TextFlow textFlow = TextFlow.new()
    Text titleText = Text.new("【${title}】")
    Text dammyText = Text.new(" ")
    List<Text> otherTexts = createRichTexts(newOther)
    titleText.getStyleClass().addAll(CONTENT_CLASS, SHALEIA_TITLE_CLASS)
    otherTexts.each() { Text otherText ->
      otherText.getStyleClass().add(CONTENT_CLASS)
    }
    titleTextFlow.getChildren().addAll(titleText, dammyText)
    textFlow.getChildren().addAll(otherTexts)
    box.getChildren().addAll(titleTextFlow, textFlow)
  }

  private void addSynonymNode(VBox box, String synonym) {
    TextFlow textFlow = TextFlow.new()
    Text titleText = Text.new("cf:")
    List<Text> synonymTexts = createRichTexts(" " + synonym)
    titleText.getStyleClass().addAll(CONTENT_CLASS, SHALEIA_TITLE_CLASS)
    synonymTexts.each() { Text synonymText ->
      synonymText.getStyleClass().add(CONTENT_CLASS)
    }
    textFlow.getChildren().add(titleText)
    textFlow.getChildren().addAll(synonymTexts)
    box.getChildren().add(textFlow)
  }

  private List<Text> createRichTexts(String string) {
    List<Text> texts = ArrayList.new()
    List<Text> unnamedTexts = ArrayList.new()
    StringBuilder currentString = StringBuilder.new()
    StringBuilder currentName = StringBuilder.new()
    Integer currentMode = 0
    string.each() { String character ->
      if (currentMode == 0 && character == "{") {
        if (currentString.length() > 0) {
          Text text = Text.new(currentString.toString())
          texts.add(text)
          currentString.setLength(0)
          currentName.setLength(0)
        }
        currentMode = 1
      } else if ((currentMode == 1 || currentMode == 11) && character == "}") {
        if (currentString.length() > 0) {
          String partName = currentString.toString()
          Text text = Text.new(partName)
          text.getStyleClass().addAll(SHALEIA_NAME_CLASS, SHALEIA_LINK_CLASS)
          unnamedTexts.add(text)
          texts.add(text)
          currentString.setLength(0)
        }
        if (currentName.length() > 0) {
          String name = currentName.toString()
          unnamedTexts.each() { Text unnamedText ->
            unnamedText.addEventHandler(MouseEvent.MOUSE_CLICKED) { MouseEvent event ->
              if ($dictionary.getOnLinkClicked() != null) {
                $dictionary.getOnLinkClicked().accept(name)
              }
            }
          }
          currentName.setLength(0)
          unnamedTexts.clear()
        }
        currentMode = 0
      } else if ((currentMode == 1 || currentMode == 11) && (character == " " || character == "." || character == "," || character == "?" || character == "-")) {
        if (currentString.length() > 0) {
          String partName = currentString.toString()
          Text text = Text.new(partName)
          text.getStyleClass().addAll(SHALEIA_NAME_CLASS, SHALEIA_LINK_CLASS)
          unnamedTexts.add(text)
          texts.add(text)
          currentString.setLength(0)
        }
        if (currentName.length() > 0) {
          String name = currentName.toString()
          unnamedTexts.each() { Text unnamedText ->
            unnamedText.addEventHandler(MouseEvent.MOUSE_CLICKED) { MouseEvent event ->
              if ($dictionary.getOnLinkClicked() != null) {
                $dictionary.getOnLinkClicked().accept(name)
              }
            }
          }
          currentName.setLength(0)
          unnamedTexts.clear()
        }
        Text characterText = Text.new(character)
        characterText.getStyleClass().add(SHALEIA_NAME_CLASS)
        texts.add(characterText)    
      } else if (currentMode == 1 && character == "/") {
        if (currentString.length() > 0) {
          String partName = currentString.toString()
          Text text = Text.new(partName)
          text.getStyleClass().addAll(SHALEIA_NAME_CLASS, SHALEIA_LINK_CLASS)
          unnamedTexts.add(text)
          texts.add(text)
          currentString.setLength(0)
        }
        currentMode = 11
      } else if (currentMode == 11 && character == "/") {
        if (currentString.length() > 0) {
          String partName = currentString.toString()
          Text text = Text.new(partName)
          text.getStyleClass().addAll(SHALEIA_NAME_CLASS, SHALEIA_LINK_CLASS, SHALEIA_ITALIC_CLASS)
          unnamedTexts.add(text)
          texts.add(text)
          currentString.setLength(0)
        }
        currentMode = 1
      } else if (currentMode == 0 && character == "[") {
        if (currentString.length() > 0) {
          Text text = Text.new(currentString.toString())
          texts.add(text)
          currentString.setLength(0)
          currentName.setLength(0)
        }      
        currentMode = 2
      } else if ((currentMode == 2 || currentName == 12) && character == "]") {
        if (currentString.length() > 0) {
          Text text = Text.new(currentString.toString())
          text.getStyleClass().add(SHALEIA_NAME_CLASS)
          texts.add(text)
          currentString.setLength(0)
          currentName.setLength(0)
        }
        currentMode = 0
      } else if (currentMode == 2 && character == "/") {
        if (currentString.length() > 0) {
          String partName = currentString.toString()
          Text text = Text.new(partName)
          text.getStyleClass().add(SHALEIA_NAME_CLASS)
          texts.add(text)
          currentString.setLength(0)
          currentName.setLength(0)
        }
        currentMode = 12
      } else if (currentMode == 12 && character == "/") {
        if (currentString.length() > 0) {
          String partName = currentString.toString()
          Text text = Text.new(partName)
          text.getStyleClass().addAll(SHALEIA_NAME_CLASS, SHALEIA_ITALIC_CLASS)
          texts.add(text)
          currentString.setLength(0)
          currentName.setLength(0)
        }      
        currentMode = 2
      } else if (currentMode == 0 && character == "/") {
        if (currentString.length() > 0) {
          Text text = Text.new(currentString.toString())
          texts.add(text)
          currentString.setLength(0)
          currentName.setLength(0)
        }
        currentMode = 3
      } else if (currentMode == 3 && character == "/") {
        if (currentString.length() > 0) {
          Text text = Text.new(currentString.toString())
          text.getStyleClass().add(SHALEIA_ITALIC_CLASS)
          texts.add(text)
          currentString.setLength(0)
          currentName.setLength(0)
        }
        currentMode = 0
      } else {
        currentString.append(character)
        currentName.append(character)
      }
    }
    Text text = Text.new(currentString.toString())
    texts.add(text)
    return texts
  }

  public void createComparisonString(String order) {
    StringBuilder comparisonString = StringBuilder.new()
    (0 ..< $name.length()).each() { Integer i ->
      if ($name[i] != "'") {
        Integer position = order.indexOf($name.codePointAt(i))
        if (position > -1) {
          comparisonString.appendCodePoint(position + 174)
        } else {
          comparisonString.appendCodePoint(10000)
        }
      }
    }
    $comparisonString = comparisonString.toString()
  }

  private void setupContentPane() {
    $contentPane.getStyleClass().add(CONTENT_PANE_CLASS)
  }

  public ShaleiaDictionary getDictionary() {
    return $dictionary
  }

  public void setDictionary(ShaleiaDictionary dictionary) {
    $dictionary = dictionary
  }

  public String getUniqueName() {
    return $uniqueName
  }

  public String getData() {
    return $data
  }

  public String getComparisonString() {
    return $comparisonString
  }

}