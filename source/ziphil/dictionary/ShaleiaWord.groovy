package ziphil.dictionary

import groovy.transform.CompileStatic
import java.util.regex.Matcher
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.control.Label
import javafx.scene.layout.HBox
import javafx.scene.layout.Pane
import javafx.scene.layout.Region
import javafx.scene.layout.VBox
import javafx.scene.text.Text
import javafx.scene.text.TextFlow


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

  public ShaleiaWord(String name, String data) {
    update(name, data)
  }

  public void update(String name, String data) {
    HBox headBox = HBox.new()
    VBox equivalentBox = VBox.new()
    $name = name.replaceAll(/\+|~/, "")
    $uniqueName = name
    $data = data
    $content = name + "\n" + data
    $contentPane.getChildren().clear()
    $contentPane.getChildren().add(headBox)
    $contentPane.getChildren().add(equivalentBox)
    $contentPane.setMargin(equivalentBox, Insets.new(0, 0, 5, 0))
    data.eachLine() { String line ->
      Matcher creationDateMatcher = line =~ /^\+\s*(\d+)\s*〈(.+)〉\s*$/
      Matcher hiddenEquivalentMatcher = line =~ /^\=:\s*(.+)$/
      Matcher equivalentMatcher = line =~ /^\=\s*〈(.+)〉\s*(.+)$/
      Matcher meaningMatcher = line =~ /^M>\s*(.+)$/
      Matcher ethymologyMatcher = line =~ /^E>\s*(.+)$/
      Matcher usageMatcher = line =~ /^U>\s*(.+)$/
      Matcher phraseMatcher = line =~ /^P>\s*(.+)$/
      Matcher noteMatcher = line =~ /^N>\s*(.+)$/
      Matcher synonymMatcher = line =~ /^\-\s*(.+)$/
      if (headBox.getChildren().isEmpty()) {
        Label nameText = Label.new(name.replaceAll(/\+|~/, ""))
        nameText.getStyleClass().addAll("content-text", "head-name", "shaleia-head-name")
        headBox.getChildren().add(nameText)
        headBox.setMargin(nameText, Insets.new(0, 10, 0, 0))
        headBox.setAlignment(Pos.CENTER_LEFT)
      }
      if (creationDateMatcher.matches()) {
        Label wholeClassText = Label.new(creationDateMatcher.group(2))
        Label creationDateText = Label.new(" " + creationDateMatcher.group(1))
        wholeClassText.getStyleClass().addAll("content-text", "shaleia-whole-class")
        creationDateText.getStyleClass().addAll("content-text", "shaleia-creation-date")
        headBox.getChildren().addAll(wholeClassText, creationDateText)
      }
      if (hiddenEquivalentMatcher.matches()) {
        List<String> equivalents = hiddenEquivalentMatcher.group(1).replaceAll(/(\(.+\)|\{|\}|\/|\s)/, "").split(/,/).toList()
        $equivalents.addAll(equivalents)
      } else if (equivalentMatcher.matches()) {
        TextFlow textFlow = TextFlow.new()
        Label localClassText = Label.new(equivalentMatcher.group(1))
        Text equivalentText = Text.new(" " + equivalentMatcher.group(2))
        localClassText.getStyleClass().addAll("content-text", "shaleia-local-class")
        equivalentText.getStyleClass().addAll("content-text", "shaleia-equivalent")
        textFlow.getChildren().addAll(localClassText, equivalentText)
        equivalentBox.getChildren().add(textFlow)
        List<String> equivalents = equivalentMatcher.group(2).replaceAll(/(\(.+\)|\{|\}|\/|\s)/, "").split(/,/).toList()
        $equivalents.addAll(equivalents)
      }
      if (meaningMatcher.matches()) {
        TextFlow textFlow = TextFlow.new()
        Label meaningItemText = Label.new("【語義】")
        Text meaningText = Text.new(meaningMatcher.group(1))
        meaningItemText.getStyleClass().addAll("content-text", "shaleia-item")
        meaningText.getStyleClass().addAll("content-text")
        textFlow.getChildren().add(meaningText)
        $contentPane.getChildren().addAll(meaningItemText, textFlow)
      }
      if (ethymologyMatcher.matches()) {
        TextFlow textFlow = TextFlow.new()
        Label ethymologyItemText = Label.new("【語源】")
        Text ethymologyText = Text.new(ethymologyMatcher.group(1))
        ethymologyItemText.getStyleClass().addAll("content-text", "shaleia-item")
        ethymologyText.getStyleClass().addAll("content-text")
        textFlow.getChildren().add(ethymologyText)
        $contentPane.getChildren().addAll(ethymologyItemText, textFlow)
      }
      if (usageMatcher.matches()) {
        TextFlow textFlow = TextFlow.new()
        Label usageItemText = Label.new("【語法】")
        Text usageText = Text.new(usageMatcher.group(1))
        usageItemText.getStyleClass().addAll("content-text", "shaleia-item")
        usageText.getStyleClass().addAll("content-text")
        textFlow.getChildren().add(usageText)
        $contentPane.getChildren().addAll(usageItemText, textFlow)
      }
      if (phraseMatcher.matches()) {
        TextFlow textFlow = TextFlow.new()
        Label phraseItemText = Label.new("【成句】")
        Text phraseText = Text.new(phraseMatcher.group(1))
        phraseItemText.getStyleClass().addAll("content-text", "shaleia-item")
        phraseText.getStyleClass().addAll("content-text")
        textFlow.getChildren().add(phraseText)
        $contentPane.getChildren().addAll(phraseItemText, textFlow)
      }
      if (noteMatcher.matches()) {
        TextFlow textFlow = TextFlow.new()
        Label noteItemText = Label.new("【備考】")
        Text noteText = Text.new(noteMatcher.group(1))
        noteItemText.getStyleClass().addAll("content-text", "shaleia-item")
        noteText.getStyleClass().addAll("content-text")
        textFlow.getChildren().add(noteText)
        $contentPane.getChildren().addAll(noteItemText, textFlow)
      }
      if (synonymMatcher.matches()) {
      }
    }
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