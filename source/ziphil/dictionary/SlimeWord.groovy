package ziphil.dictionary

import groovy.transform.CompileStatic
import javafx.scene.layout.Pane
import javafx.scene.layout.VBox
import ziphil.module.Setting


@CompileStatic @Newify
public class SlimeWord extends Word {

  private SlimeDictionary $dictionary
  private Integer $id = -1
  private List<SlimeEquivalent> $rawEquivalents = ArrayList.new()
  private List<String> $tags = ArrayList.new()
  private List<SlimeInformation> $informations = ArrayList.new()
  private List<SlimeVariation> $variations = ArrayList.new()
  private List<SlimeRelation> $relations = ArrayList.new()
  private String $comparisonString = ""
  private VBox $simpleContentPane = VBox.new()
  private Boolean $isSimpleChanged = true

  public SlimeWord(Integer id, String name, List<SlimeEquivalent> rawEquivalents, List<String> tags, List<SlimeInformation> informations, List<SlimeVariation> variations,
                   List<SlimeRelation> relations) {
    update(id, name, rawEquivalents, tags, informations, variations, relations)
  }

  public SlimeWord() {
  }

  public void update(Integer id, String name, List<SlimeEquivalent> rawEquivalents, List<String> tags, List<SlimeInformation> informations, List<SlimeVariation> variations,
                     List<SlimeRelation> relations) {
    $id = id
    $name = name
    $rawEquivalents = rawEquivalents
    $tags = tags
    $informations = informations
    $variations = variations
    $relations = relations
    rawEquivalents.each() { SlimeEquivalent equivalent ->
      $equivalents.addAll(equivalent.getNames())
    }
    $isChanged = true
    $isSimpleChanged = true
  }

  public void createContentPane() {
    Setting setting = Setting.getInstance()
    Boolean modifiesPunctuation = setting.getModifiesPunctuation()
    SlimeWordContentPaneCreator creator = SlimeWordContentPaneCreator.new($contentPane, this, $dictionary)
    creator.setModifiesPunctuation(modifiesPunctuation)
    creator.create()
    $isChanged = false
  }

  public void createSimpleContentPane() {
    Setting setting = Setting.getInstance()
    Boolean modifiesPunctuation = setting.getModifiesPunctuation()
    SlimeWordSimpleContentPaneCreator creator = SlimeWordSimpleContentPaneCreator.new($simpleContentPane, this, $dictionary)
    creator.setModifiesPunctuation(modifiesPunctuation)
    creator.create()
    $isSimpleChanged = false
  }

  public void createComparisonString(String order) {
    StringBuilder comparisonString = StringBuilder.new()
    (0 ..< $name.length()).each() { Integer i ->
      Integer position = order.indexOf($name.codePointAt(i))
      if (position > -1) {
        comparisonString.appendCodePoint(position + 174)
      } else {
        comparisonString.appendCodePoint(10000)
      }
    }
    $comparisonString = comparisonString.toString()
  }

  public Boolean isSimpleChanged() {
    return $isSimpleChanged
  }

  public SlimeDictionary getDictionary() {
    return $dictionary
  }

  public void setDictionary(SlimeDictionary dictionary) {
    $dictionary = dictionary
  }

  public Integer getId() {
    return $id
  }

  public void setId(Integer id) {
    $id = id
  }

  public void setName(String name) {
    $name = name
  }

  public List<SlimeEquivalent> getRawEquivalents() {
    return $rawEquivalents
  }

  public void setRawEquivalents(List<SlimeEquivalent> rawEquivalents) {
    $rawEquivalents = rawEquivalents
    rawEquivalents.each() { SlimeEquivalent equivalent ->
      $equivalents.addAll(equivalent.getNames())
    }
  }

  public List<String> getTags() {
    return $tags
  }

  public void setTags(List<String> tags) {
    $tags = tags
  }

  public List<SlimeInformation> getInformations() {
    return $informations
  }

  public void setInformations(List<SlimeInformation> informations) {
    $informations = informations
  }

  public List<SlimeVariation> getVariations() {
    return $variations
  }

  public void setVariations(List<SlimeVariation> variations) {
    $variations = variations
  }

  public List<SlimeRelation> getRelations() {
    return $relations
  }

  public void setRelations(List<SlimeRelation> relations) {
    $relations = relations
  }

  public String getComparisonString() {
    return $comparisonString
  }

  public Pane getSimpleContentPane() {
    return $simpleContentPane
  }

}