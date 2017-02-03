package ziphil.dictionary.database

import groovy.transform.CompileStatic
import javafx.scene.layout.Pane
import ziphil.dictionary.ContentPaneFactory
import ziphil.dictionary.Word
import ziphil.module.Setting
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class DatabaseWord implements Word {

  private Integer $id = -1
  private String $name = ""
  private String $comparisonString = ""
  private DatabaseWordContentPaneFactory $contentPaneFactory
  private DatabaseDictionary $dictionary

  public void update() {
  }

  private void makeContentPaneFactory() {
    Setting setting = Setting.getInstance()
    Integer lineSpacing = setting.getLineSpacing()
    Boolean modifiesPunctuation = setting.getModifiesPunctuation()
    $contentPaneFactory = DatabaseWordContentPaneFactory.new(this, $dictionary)
    $contentPaneFactory.setLineSpacing(lineSpacing)
    $contentPaneFactory.setModifiesPunctuation(modifiesPunctuation)
  }

  public Boolean isDisplayed() {
    return true
  }

  public Integer getId() {
    return $id
  }

  public void setId(Integer id) {
    $id = id
  }

  public String getName() {
    return $name
  }

  public void setName(String name) {
    $name = name
  }

  public List<String> getEquivalents() {
    return ArrayList.new()
  }

  public void setEquivalents(List<String> equivalents) {
  }

  public String getContent() {
    return ""
  }

  public void setContent(String content) {
  }

  public DatabaseDictionary getDictionary() {
    return $dictionary
  }

  public void setDictionary(DatabaseDictionary dictionary) {
    $dictionary = dictionary
  }

  public String getComparisonString() {
    return $comparisonString
  }

  public ContentPaneFactory getContentPaneFactory() {
    if ($contentPaneFactory == null) {
      makeContentPaneFactory()
    }
    return $contentPaneFactory
  }

}