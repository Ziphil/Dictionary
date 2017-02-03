package ziphil.dictionary.slime

import groovy.transform.CompileStatic
import ziphil.dictionary.ContentPaneFactory
import ziphil.dictionary.ContentPaneFactoryBase
import ziphil.dictionary.WordBase
import ziphil.module.Setting
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class SlimeWord extends WordBase {

  private Integer $id = -1
  private List<SlimeEquivalent> $rawEquivalents = ArrayList.new()
  private List<String> $tags = ArrayList.new()
  private List<SlimeInformation> $informations = ArrayList.new()
  private List<SlimeVariation> $variations = ArrayList.new()
  private List<SlimeRelation> $relations = ArrayList.new()
  private String $comparisonString = ""
  private ContentPaneFactoryBase $plainContentPaneFactory
  private SlimeDictionary $dictionary

  public void update() {
    updateEquivalents()
    updateContent()
    changeContentPaneFactory()
    changePlainContentPaneFactory()
  }

  public void change() {
    changeContentPaneFactory()
    changePlainContentPaneFactory()
  }

  private void updateEquivalents() {
    $equivalents.clear()
    for (SlimeEquivalent equivalent : $rawEquivalents) {
      $equivalents.addAll(equivalent.getNames())
    }
  }

  private void updateContent() {
    StringBuilder content = StringBuilder.new()
    for (SlimeEquivalent equivalent : $rawEquivalents) {
      for (String equivalentName : equivalent.getNames()) {
        content.append(equivalentName).append("\n")
      }
    }
    for (SlimeInformation information : $informations) {
      content.append(information.getText()).append("\n")
    }
    $content = content.toString()
  }

  public void updateComparisonString(String order) {
    if (order != null) {
      StringBuilder comparisonString = StringBuilder.new()
      for (Integer i : 0 ..< $name.length()) {
        Integer position = order.indexOf($name.codePointAt(i))
        if (position > -1) {
          comparisonString.appendCodePoint(position + 174)
        } else {
          comparisonString.appendCodePoint(10000)
        }
      }
      $comparisonString = comparisonString.toString()
    } else {
      $comparisonString = $name
    }
  }

  private void changePlainContentPaneFactory() {
    if ($plainContentPaneFactory != null) {
      $plainContentPaneFactory.change()
    }
  }

  protected void makeContentPaneFactory() {
    Setting setting = Setting.getInstance()
    Integer lineSpacing = setting.getLineSpacing()
    Boolean modifiesPunctuation = setting.getModifiesPunctuation()
    $contentPaneFactory = SlimeWordContentPaneFactory.new(this, $dictionary)
    $contentPaneFactory.setLineSpacing(lineSpacing)
    $contentPaneFactory.setModifiesPunctuation(modifiesPunctuation)
  }

  protected void makePlainContentPaneFactory() {
    Setting setting = Setting.getInstance()
    Boolean modifiesPunctuation = setting.getModifiesPunctuation()
    $plainContentPaneFactory = SlimeWordPlainContentPaneFactory.new(this, $dictionary)
    $plainContentPaneFactory.setModifiesPunctuation(modifiesPunctuation)
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

  public SlimeDictionary getDictionary() {
    return $dictionary
  }

  public void setDictionary(SlimeDictionary dictionary) {
    $dictionary = dictionary
  }

  public String getComparisonString() {
    return $comparisonString
  }

  public ContentPaneFactory getPlainContentPaneFactory() {
    if ($plainContentPaneFactory == null) {
      makePlainContentPaneFactory()
    }
    return $plainContentPaneFactory
  }

}