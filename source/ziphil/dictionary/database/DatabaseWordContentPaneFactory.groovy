package ziphil.dictionary.database

import groovy.transform.CompileStatic
import java.sql.Connection
import java.sql.ResultSet
import java.sql.Statement
import java.util.Map.Entry
import javafx.scene.Node
import javafx.scene.control.Label
import javafx.scene.input.MouseEvent
import javafx.scene.layout.Pane
import javafx.scene.text.Text
import javafx.scene.text.TextFlow
import ziphil.dictionary.ContentPaneFactory
import ziphil.dictionary.ContentPaneFactoryBase
import ziphil.module.Strings
import ziphilib.transform.VoidClosure
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class DatabaseWordContentPaneFactory implements ContentPaneFactory {

  private static final String SLIME_HEAD_NAME_CLASS = "slime-head-name"
  private static final String SLIME_TAG_CLASS = "slime-tag"
  private static final String SLIME_EQUIVALENT_CLASS = "slime-equivalent"
  private static final String SLIME_EQUIVALENT_TITLE_CLASS = "slime-equivalent-title"
  private static final String SLIME_TITLE_CLASS = "slime-title"
  private static final String SLIME_LINK_CLASS = "slime-link"

  private TextFlow $contentPane
  private DatabaseWord $word
  private DatabaseDictionary $dictionary
  private Integer $lineSpacing = 0
  private Boolean $modifiesPunctuation = false

  public DatabaseWordContentPaneFactory(DatabaseWord word, DatabaseDictionary dictionary) {
    $word = word
    $dictionary = dictionary
  }

  public Pane create() {
    $contentPane = TextFlow.new()
    $contentPane.getStyleClass().add(CONTENT_PANE_CLASS)
    $contentPane.setLineSpacing($lineSpacing)
    addNameNode($word.getName())
    manageTags()
    manageEquivalents()
    manageInformations()
    manageRelations()
    modifyBreak()
    $dictionary.getConnection().commit()
    return $contentPane
  }

  private void manageTags() {
    Statement statement = $dictionary.getConnection().createStatement()
    try {
      List<String> tags = ArrayList.new()
      ResultSet result = statement.executeQuery("SELECT form FROM tag WHERE entry_id=${$word.getId()} ORDER BY index")
      try {
        while (result.next()) {
          tags.add(result.getString("form"))
        }
        addTagNode(tags)
      } finally {
        result.close()
      }
    } finally {
      statement.close()
    }
  }

  private void manageEquivalents() {
    Statement statement = $dictionary.getConnection().createStatement()
    try {
      List<Integer> equivalentIds = ArrayList.new()
      List<String> equivalentTitles = ArrayList.new()
      ResultSet result = statement.executeQuery("SELECT id, title FROM translation_main WHERE entry_id=${$word.getId()} ORDER BY index")
      try {
        while (result.next()) {
          equivalentIds.add(result.getInt("id"))
          equivalentTitles.add(result.getString("title"))
        }
      } finally {
        result.close()
      }
      for (Integer i : 0 ..< equivalentIds.size()) {
        StringBuilder equivalentString = StringBuilder.new()
        ResultSet nameResult = statement.executeQuery("SELECT form FROM translation_form WHERE entry_id=${$word.getId()} AND translation_id=${equivalentIds[i]} ORDER BY index")
        try {
          while (nameResult.next()) {
            equivalentString.append(nameResult.getString("form"))
            equivalentString.append(", ")
          }
          equivalentString.delete(equivalentString.length() - 2, equivalentString.length())
          addEquivalentNode(equivalentTitles[i], equivalentString.toString())
        } finally {
          nameResult.close()
        }
      }
    } finally {
      statement.close()
    }
  }

  private void manageInformations() {
    Statement statement = $dictionary.getConnection().createStatement()
    try {
      ResultSet result = statement.executeQuery("SELECT title, text FROM content WHERE entry_id=${$word.getId()} ORDER BY index")
      try {
        while (result.next()) {
          addInformationNode(result.getString("title"), result.getString("text"))
        }
      } finally {
        result.close()
      }
    } finally {
      statement.close()
    }
  }

  private void manageRelations() {
    Statement statement = $dictionary.getConnection().createStatement()
    try {
      Map<String, List<Integer>> relations = HashMap.new()
      ResultSet result = statement.executeQuery("SELECT title, referrence_id FROM relation WHERE entry_id=${$word.getId()} ORDER BY index")
      try {
        while (result.next()) {
          String title = result.getString("title")
          if (!relations.containsKey(title)) {
            relations.put(title, ArrayList.new())
          }
          relations.get(title).add(result.getInt("referrence_id"))
        }
      } finally {
        result.close()
      }
      for (Entry<String, List<Integer>> entry : relations) {
        List<Integer> ids = ArrayList.new()
        List<String> names = ArrayList.new()
        for (Integer referrenceId : entry.getValue()) {
          ResultSet referrenceResult = statement.executeQuery("SELECT form FROM entry WHERE id=${referrenceId}")
          try {
            referrenceResult.next()
            ids.add(referrenceId)
            names.add(referrenceResult.getString("form"))
          } finally {
            referrenceResult.close()
          }
        }
        addRelationNode(entry.getKey(), ids, names)
      }
    } finally {
      statement.close()
    }
  }

  private void addNameNode(String name) {
    Text nameText = Text.new(name + "  ")
    nameText.getStyleClass().addAll(CONTENT_CLASS, HEAD_NAME_CLASS, SLIME_HEAD_NAME_CLASS)
    $contentPane.getChildren().add(nameText)
  }

  private void addTagNode(List<String> tags) {
    for (String tag : tags) {
      Label tagText = Label.new(tag)
      Text spaceText = Text.new(" ")
      tagText.getStyleClass().addAll(CONTENT_CLASS, SLIME_TAG_CLASS)
      spaceText.getStyleClass().addAll(CONTENT_CLASS, SLIME_TITLE_CLASS)
      $contentPane.getChildren().addAll(tagText, spaceText)
    }
    Text breakText = Text.new("\n")
    $contentPane.getChildren().add(breakText)
  }

  private void addEquivalentNode(String title, String equivalent) {
    Label titleText = Label.new(title)
    Text equivalentText = Text.new(" " + equivalent)
    Text breakText = Text.new("\n")
    titleText.getStyleClass().addAll(CONTENT_CLASS, SLIME_EQUIVALENT_TITLE_CLASS)
    equivalentText.getStyleClass().addAll(CONTENT_CLASS, SLIME_EQUIVALENT_CLASS)
    $contentPane.getChildren().addAll(titleText, equivalentText, breakText)
  }

  private void addInformationNode(String title, String information) {
    String modifiedInformation = ($modifiesPunctuation) ? Strings.modifyPunctuation(information) : information
    Boolean insertsBreak = !$dictionary.getPlainInformationTitles().contains(title)
    Text titleText = Text.new("【${title}】")
    Text innerBreakText = Text.new((insertsBreak) ? " \n" : " ")
    Text informationText = Text.new(modifiedInformation)
    Text breakText = Text.new("\n")
    titleText.getStyleClass().addAll(CONTENT_CLASS, SLIME_TITLE_CLASS)
    innerBreakText.getStyleClass().add(CONTENT_CLASS)
    informationText.getStyleClass().add(CONTENT_CLASS)
    $contentPane.getChildren().addAll(titleText, innerBreakText, informationText, breakText)
  }

  @VoidClosure
  private void addRelationNode(String title, List<Integer> ids, List<String> names) {
    Text formerTitleText = Text.new("cf:")
    Text titleText = Text.new("〈${title}〉" + " ")
    formerTitleText.getStyleClass().addAll(CONTENT_CLASS, SLIME_TITLE_CLASS)
    titleText.getStyleClass().addAll(CONTENT_CLASS, SLIME_TITLE_CLASS)
    $contentPane.getChildren().addAll(formerTitleText, titleText)
    for (Integer i : 0 ..< names.size()) {
      Integer id = ids[i]
      String name = names[i]
      Text nameText = Text.new(name)
      nameText.addEventHandler(MouseEvent.MOUSE_CLICKED) { MouseEvent event ->
        if ($dictionary.getOnLinkClicked() != null) {
          $dictionary.getOnLinkClicked().accept(id)
        }
      }
      nameText.getStyleClass().addAll(CONTENT_CLASS, SLIME_LINK_CLASS)
      $contentPane.getChildren().add(nameText)
      if (i < names.size() - 1) {
        Text punctuationText = Text.new(", ")
        punctuationText.getStyleClass().add(CONTENT_CLASS)
        $contentPane.getChildren().add(punctuationText)
      }      
    }
    Text breakText = Text.new("\n")
    $contentPane.getChildren().add(breakText)
  }

  private void modifyBreak() {
    Node lastChild = $contentPane.getChildren().last()
    if (lastChild instanceof Text && lastChild.getText() == "\n") {
      $contentPane.getChildren().removeAt($contentPane.getChildren().size() - 1)
    }
  }

  public void setLineSpacing(Integer lineSpacing) {
    $lineSpacing = lineSpacing
  }

  public void setModifiesPunctuation(Boolean modifiesPunctuation) {
    $modifiesPunctuation = modifiesPunctuation
  }

}