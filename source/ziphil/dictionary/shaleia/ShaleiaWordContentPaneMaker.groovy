package ziphil.dictionary.shaleia

import groovy.transform.CompileStatic
import java.util.regex.Matcher
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.control.Label
import javafx.scene.input.MouseEvent
import javafx.scene.text.Text
import javafx.scene.text.TextFlow
import ziphil.custom.Measurement
import ziphil.dictionary.ContentPaneMaker
import ziphil.module.Strings
import ziphilib.transform.VoidClosure
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class ShaleiaWordContentPaneMaker extends ContentPaneMaker<ShaleiaWord, ShaleiaDictionary> {

  private static final String SHALEIA_HEAD_NAME_CLASS = "shaleia-head-name"
  private static final String SHALEIA_EQUIVALENT_CLASS = "shaleia-equivalent"
  private static final String SHALEIA_TOTAL_PART_CLASS = "shaleia-total-part"
  private static final String SHALEIA_PART_CLASS = "shaleia-part"
  private static final String SHALEIA_CREATION_DATE_CLASS = "shaleia-creation-date"
  private static final String SHALEIA_TITLE_CLASS = "shaleia-title"
  private static final String SHALEIA_NAME_CLASS = "shaleia-name"
  private static final String SHALEIA_LINK_CLASS = "shaleia-link"
  private static final String SHALEIA_ITALIC_CLASS = "shaleia-italic"
  private static final String START_NAME_CHARACTER = "["
  private static final String END_NAME_CHARACTER = "]"
  private static final String START_LINK_CHARACTER = "{"
  private static final String END_LINK_CHARACTER = "}"
  private static final String START_ITALIC_CHARACTER = "/"
  private static final String END_ITALIC_CHARACTER = "/"
  private static final String START_ESCAPE_CHARACTER = "&"
  private static final String END_ESCAPE_CHARACTER = ";"
  private static final String PUNCTUATIONS = " .,?!-"

  public ShaleiaWordContentPaneMaker(TextFlow contentPane, ShaleiaWord word, ShaleiaDictionary dictionary) {
    super(contentPane, word, dictionary)
  }

  public void make() {
    Boolean hasOther = false
    Boolean hasSynonym = false
    $contentPane.getStyleClass().clear()
    $contentPane.getStyleClass().add(CONTENT_PANE_CLASS)
    $contentPane.getChildren().clear()
    $contentPane.setLineSpacing($lineSpacing)
    BufferedReader reader = BufferedReader.new(StringReader.new($word.getData()))
    String line
    while ((line = reader.readLine()) != null) {
      Matcher creationDateMatcher = line =~ /^\+\s*(\d+)\s*〈(.+)〉\s*$/
      Matcher hiddenEquivalentMatcher = line =~ /^\=:\s*(.+)$/
      Matcher equivalentMatcher = line =~ /^\=\s*〈(.+)〉\s*(.+)$/
      Matcher meaningMatcher = line =~ /^M>\s*(.+)$/
      Matcher etymologyMatcher = line =~ /^E>\s*(.+)$/
      Matcher usageMatcher = line =~ /^U>\s*(.+)$/
      Matcher phraseMatcher = line =~ /^P>\s*(.+)$/
      Matcher noteMatcher = line =~ /^N>\s*(.+)$/
      Matcher taskMatcher = line =~ /^O>\s*(.+)$/
      Matcher exampleMatcher = line =~ /^S>\s*(.+)$/
      Matcher synonymMatcher = line =~ /^\-\s*(.+)$/
      if ($contentPane.getChildren().isEmpty()) {
        String name = $word.getName()
        addNameNode(name)
      }
      if (creationDateMatcher.matches()) {
        String creationDate = creationDateMatcher.group(1)
        String totalPart = creationDateMatcher.group(2)
        addCreationDateNode(totalPart, creationDate)
      }
      if (equivalentMatcher.matches()) {
        String part = equivalentMatcher.group(1)
        String equivalent = equivalentMatcher.group(2)
        addEquivalentNode(part, equivalent)
      }
      if (hiddenEquivalentMatcher.matches()) {
        String equivalent = hiddenEquivalentMatcher.group(1)
      }
      if (meaningMatcher.matches()) {
        String meaning = meaningMatcher.group(1)
        addOtherNode("語義", meaning)
        hasOther = true
      }
      if (etymologyMatcher.matches()) {
        String etymology = etymologyMatcher.group(1)
        addOtherNode("語源", etymology)
        hasOther = true
      }
      if (usageMatcher.matches()) {
        String usage = usageMatcher.group(1)
        addOtherNode("語法", usage)
        hasOther = true
      }
      if (phraseMatcher.matches()) {
        String phrase = phraseMatcher.group(1)
        addOtherNode("成句", phrase)
        hasOther = true
      }
      if (noteMatcher.matches()) {
        String note = noteMatcher.group(1)
        addOtherNode("備考", note)
        hasOther = true
      }
      if (taskMatcher.matches()) {
        String task = taskMatcher.group(1)
        addOtherNode("タスク", task)
        hasOther = true
      }
      if (exampleMatcher.matches()) {
        String example = exampleMatcher.group(1)
        addOtherNode("例文", example)
        hasOther = true
      }
      if (synonymMatcher.matches()) {
        String synonym = synonymMatcher.group(1)
        addSynonymNode(synonym)
        hasSynonym = true
      }
    }
    modifyBreak()
    reader.close()
  }

  private void addNameNode(String name) {
    Text nameText = Text.new(name + "  ")
    nameText.getStyleClass().addAll(CONTENT_CLASS, HEAD_NAME_CLASS, SHALEIA_HEAD_NAME_CLASS)
    $contentPane.getChildren().add(nameText)
  }
 
  private void addCreationDateNode(String totalPart, String creationDate) {
    Label totalPartText = Label.new(totalPart)
    Text creationDateText = Text.new(" " + creationDate)
    Text breakText = Text.new("\n")
    totalPartText.getStyleClass().addAll(CONTENT_CLASS, SHALEIA_TOTAL_PART_CLASS)
    creationDateText.getStyleClass().addAll(CONTENT_CLASS, SHALEIA_CREATION_DATE_CLASS)
    $contentPane.getChildren().addAll(totalPartText, creationDateText, breakText)
  }

  private void addEquivalentNode(String part, String equivalent) {
    Label partText = Label.new(part)
    Text breakText = Text.new("\n")
    List<Text> equivalentTexts = createRichTexts(" " + equivalent)
    partText.getStyleClass().addAll(CONTENT_CLASS, SHALEIA_PART_CLASS)
    for (Text equivalentText : equivalentTexts) {
      equivalentText.getStyleClass().addAll(CONTENT_CLASS, SHALEIA_EQUIVALENT_CLASS)
    }
    $contentPane.getChildren().add(partText)
    $contentPane.getChildren().addAll(equivalentTexts)
    $contentPane.getChildren().add(breakText)
  }

  private void addOtherNode(String title, String other) {
    String modifiedOther = ($modifiesPunctuation) ? Strings.modifyPunctuation(other) : other
    Text titleText = Text.new("【${title}】")
    Text dammyText = Text.new(" \n")
    Text breakText = Text.new("\n")
    List<Text> otherTexts = createRichTexts(modifiedOther)
    titleText.getStyleClass().addAll(CONTENT_CLASS, SHALEIA_TITLE_CLASS)
    dammyText.getStyleClass().add(CONTENT_CLASS)
    for (Text otherText : otherTexts) {
      otherText.getStyleClass().add(CONTENT_CLASS)
    }
    $contentPane.getChildren().addAll(titleText, dammyText)
    $contentPane.getChildren().addAll(otherTexts)
    $contentPane.getChildren().add(breakText)
  }

  private void addSynonymNode(String synonym) {
    TextFlow textFlow = TextFlow.new()
    Text titleText = Text.new("cf:")
    Text breakText = Text.new("\n")
    List<Text> synonymTexts = createRichTexts(" " + synonym, true)
    titleText.getStyleClass().addAll(CONTENT_CLASS, SHALEIA_TITLE_CLASS)
    for (Text synonymText : synonymTexts) {
      synonymText.getStyleClass().add(CONTENT_CLASS)
    }
    $contentPane.getChildren().add(titleText)
    $contentPane.getChildren().addAll(synonymTexts)
    $contentPane.getChildren().add(breakText)
  }

  @VoidClosure
  private List<Text> createRichTexts(String string, Boolean decoratesLink) {
    List<Text> texts = ArrayList.new()
    List<Text> unnamedTexts = ArrayList.new()
    StringBuilder currentString = StringBuilder.new()
    StringBuilder currentEscapeString = StringBuilder.new()
    StringBuilder currentName = StringBuilder.new()
    TextMode currentMode = TextMode.NORMAL
    for (String character : string) {
      if (currentMode == TextMode.NORMAL && character == START_LINK_CHARACTER) {
        if (currentString.length() > 0) {
          Text text = Text.new(currentString.toString())
          texts.add(text)
          currentString.setLength(0)
          currentName.setLength(0)
        }
        currentMode = TextMode.LINK
      } else if ((currentMode == TextMode.LINK || currentMode == TextMode.LINK_ITALIC) && character == END_LINK_CHARACTER) {
        if (currentString.length() > 0) {
          String partName = currentString.toString()
          Text text = Text.new(partName)
          text.getStyleClass().add(SHALEIA_NAME_CLASS)
          if (decoratesLink) {
            text.getStyleClass().add(SHALEIA_LINK_CLASS)
          }
          unnamedTexts.add(text)
          texts.add(text)
          currentString.setLength(0)
        }
        if (currentName.length() > 0) {
          String name = currentName.toString()
          for (Text unnamedText : unnamedTexts) {
            unnamedText.addEventHandler(MouseEvent.MOUSE_CLICKED) { MouseEvent event ->
              if ($dictionary.getOnLinkClicked() != null) {
                $dictionary.getOnLinkClicked().accept(name)
              }
            }
          }
          currentName.setLength(0)
          unnamedTexts.clear()
        }
        currentMode = TextMode.NORMAL
      } else if ((currentMode == TextMode.LINK || currentMode == TextMode.LINK_ITALIC) && PUNCTUATIONS.indexOf(character) >= 0) {
        if (currentString.length() > 0) {
          String partName = currentString.toString()
          Text text = Text.new(partName)
          text.getStyleClass().add(SHALEIA_NAME_CLASS)
          if (decoratesLink) {
            text.getStyleClass().add(SHALEIA_LINK_CLASS)
          }
          unnamedTexts.add(text)
          texts.add(text)
          currentString.setLength(0)
        }
        if (currentName.length() > 0) {
          String name = currentName.toString()
          for (Text unnamedText : unnamedTexts) {
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
      } else if (currentMode == TextMode.LINK && character == START_ITALIC_CHARACTER) {
        if (currentString.length() > 0) {
          String partName = currentString.toString()
          Text text = Text.new(partName)
          text.getStyleClass().add(SHALEIA_NAME_CLASS)
          if (decoratesLink) {
            text.getStyleClass().add(SHALEIA_LINK_CLASS)
          }
          unnamedTexts.add(text)
          texts.add(text)
          currentString.setLength(0)
        }
        currentMode = TextMode.LINK_ITALIC
      } else if (currentMode == TextMode.LINK_ITALIC && character == END_ITALIC_CHARACTER) {
        if (currentString.length() > 0) {
          String partName = currentString.toString()
          Text text = Text.new(partName)
          text.getStyleClass().addAll(SHALEIA_NAME_CLASS, SHALEIA_ITALIC_CLASS)
          if (decoratesLink) {
            text.getStyleClass().add(SHALEIA_LINK_CLASS)
          }
          unnamedTexts.add(text)
          texts.add(text)
          currentString.setLength(0)
        }
        currentMode = TextMode.LINK
      } else if (currentMode == TextMode.NORMAL && character == START_NAME_CHARACTER) {
        if (currentString.length() > 0) {
          Text text = Text.new(currentString.toString())
          texts.add(text)
          currentString.setLength(0)
          currentName.setLength(0)
        }      
        currentMode = TextMode.NAME
      } else if ((currentMode == TextMode.NAME || currentName == TextMode.NAME_ITALIC) && character == END_NAME_CHARACTER) {
        if (currentString.length() > 0) {
          Text text = Text.new(currentString.toString())
          text.getStyleClass().add(SHALEIA_NAME_CLASS)
          texts.add(text)
          currentString.setLength(0)
          currentName.setLength(0)
        }
        currentMode = TextMode.NORMAL
      } else if (currentMode == TextMode.NAME && character == START_ITALIC_CHARACTER) {
        if (currentString.length() > 0) {
          String partName = currentString.toString()
          Text text = Text.new(partName)
          text.getStyleClass().add(SHALEIA_NAME_CLASS)
          texts.add(text)
          currentString.setLength(0)
          currentName.setLength(0)
        }
        currentMode = TextMode.NAME_ITALIC
      } else if (currentMode == TextMode.NAME_ITALIC && character == END_ITALIC_CHARACTER) {
        if (currentString.length() > 0) {
          String partName = currentString.toString()
          Text text = Text.new(partName)
          text.getStyleClass().addAll(SHALEIA_NAME_CLASS, SHALEIA_ITALIC_CLASS)
          texts.add(text)
          currentString.setLength(0)
          currentName.setLength(0)
        }      
        currentMode = TextMode.NAME
      } else if (currentMode == TextMode.NORMAL && character == START_ITALIC_CHARACTER) {
        if (currentString.length() > 0) {
          Text text = Text.new(currentString.toString())
          texts.add(text)
          currentString.setLength(0)
          currentName.setLength(0)
        }
        currentMode = TextMode.NORMAL_ITALIC
      } else if (currentMode == TextMode.NORMAL_ITALIC && character == END_ITALIC_CHARACTER) {
        if (currentString.length() > 0) {
          Text text = Text.new(currentString.toString())
          text.getStyleClass().add(SHALEIA_ITALIC_CLASS)
          texts.add(text)
          currentString.setLength(0)
          currentName.setLength(0)
        }
        currentMode = TextMode.NORMAL
      } else {
        if (character == START_ESCAPE_CHARACTER || currentEscapeString.length() > 0) {
          currentEscapeString.append(character)
          if (character == END_ESCAPE_CHARACTER && currentEscapeString.length() > 0) {
            Matcher matcher = currentEscapeString.toString() =~ /^&#x([0-9A-Fa-f]+);$/
            if (matcher.matches()) {
              String convertedEscapeString = Character.toChars(Integer.parseInt(matcher.group(1), 16))[0]
              currentString.append(convertedEscapeString)
              currentName.append(convertedEscapeString)
              currentEscapeString.setLength(0)
            }
          }
        } else {
          currentString.append(character)
          currentName.append(character)
        }
      }
    }
    Text text = Text.new(currentString.toString())
    texts.add(text)
    return texts
  }

  private List<Text> createRichTexts(String string) {
    return createRichTexts(string, false)
  }

  public void setModifiesPunctuation(Boolean modifiesPunctuation) {
    $modifiesPunctuation = modifiesPunctuation
  }

}


@InnerClass(ShaleiaWordContentPaneMaker)
private static enum TextMode {

  NORMAL, NORMAL_ITALIC, NAME, NAME_ITALIC, LINK, LINK_ITALIC

}