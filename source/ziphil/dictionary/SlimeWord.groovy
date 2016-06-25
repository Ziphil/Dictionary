package ziphil.dictionary

import groovy.transform.CompileStatic
import javafx.scene.layout.Pane
import javafx.scene.layout.VBox
import net.arnx.jsonic.JSONHint
import ziphil.module.Setting


@CompileStatic @Newify
public class SlimeWord extends Word {

  private SlimeDictionary $dictionary
  private Integer $id = -1
  private String $name = ""
  private List<String> $equivalents = ArrayList.new()
  private List<String> $tags = ArrayList.new()
  private List<SlimeInformation> $informations = ArrayList.new()
  private List<SlimeVariation> $variations = ArrayList.new()
  private List<SlimeRelation> $relations = ArrayList.new()
  private String $content = ""
  private VBox $contentPane = VBox.new()
  private Boolean $isChanged = true

  public SlimeWord(Integer id, String name, List<String> equivalents, List<String> tags, List<SlimeInformation> informations, List<SlimeVariation> variations, List<SlimeRelation> relations) {
    update(id, name, equivalents, tags, informations, variations, relations)
    setupContentPane()
  }

  public SlimeWord() {
    setupContentPane()
  }

  public void update(Integer id, String name, List<String> equivalents, List<String> tags, List<SlimeInformation> informations, List<SlimeVariation> variations, List<SlimeRelation> relations) {
    $id = id
    $name = name
    $equivalents = equivalents
    $tags = tags
    $informations = informations
    $variations = variations
    $relations = relations
    $isChanged = true
  }

  public void createContentPane() {
    $isChanged = false
  }

  private void setupContentPane() {
    Setting setting = Setting.getInstance()
    String fontFamily = setting.getContentFontFamily()
    Integer fontSize = setting.getContentFontSize()
    if (fontFamily != null && fontSize != null) {
      $contentPane.setStyle("-fx-font-family: \"${fontFamily}\"; -fx-font-size: ${fontSize}")
    }
  }

  public static SlimeWord emptyWord() {
    return SlimeWord.new()
  }

  public static SlimeWord copyFrom(SlimeWord oldWord) {
    Integer id = oldWord.getId()
    String name = oldWord.getName()
    List<String> equivalents = oldWord.getEquivalents()
    List<String> tags = oldWord.getTags()
    List<SlimeInformation> informations = oldWord.getInformations()
    List<SlimeVariation> variations = oldWord.getVariations()
    List<SlimeRelation> relations = oldWord.getRelations()
    return SlimeWord.new(id, name, equivalents, tags, informations, variations, relations)
  }

  public Boolean isChanged() {
    return $isChanged
  }

  public Dictionary getDictionary() {
    return $dictionary
  }

  public void setDictionary(Dictionary dictionary) {
    $dictionary = (SlimeDictionary)dictionary
  }

  @JSONHint(ignore=true)
  public Integer getId() {
    return $id
  }

  @JSONHint(ignore=true)
  public String getName() {
    return $name
  }

  @JSONHint(name="translations")
  public List<String> getEquivalents() {
    return $equivalents
  }

  @JSONHint(name="translations")
  public void setEquivalents(List<String> equivalents) {
    $equivalents = equivalents
  }

  public List<String> getTags() {
    return $tags
  }

  public void setTags(List<String> tags) {
    $tags = tags
  }

  @JSONHint(name="getContents")
  public List<SlimeInformation> getInformations() {
    return $informations
  }

  @JSONHint(name="setContents")
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

  public Map<String, Object> getEntry() {
    return [("id"): (Object)$id, ("form"): (Object)$name]
  }

  public void setEntry(Map<String, Object> entry) {
    $id = (Integer)entry["id"]
    $name = (String)entry["form"]
  }

  public String getContent() {
    return $content
  }

  public Pane getContentPane() {
    return $contentPane
  }

}