package ziphil.dictionary.slime

import groovy.transform.CompileStatic
import ziphil.dictionary.ContentPaneFactory
import ziphil.dictionary.ContentPaneFactoryBase
import ziphil.dictionary.WordBase
import ziphil.module.Setting
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class SlimeWord extends WordBase {

  private SlimeDictionary $dictionary
  private Integer $id = -1
  private List<SlimeEquivalent> $rawEquivalents = ArrayList.new()
  private List<String> $tags = ArrayList.new()
  private List<SlimeInformation> $informations = ArrayList.new()
  private List<SlimeVariation> $variations = ArrayList.new()
  private List<SlimeRelation> $relations = ArrayList.new()
  private String $comparisonString = ""
  private ContentPaneFactoryBase $plainContentPaneFactory

  public void update() {
    updateEquivalents()
    updateContent()
    updateComparisonString()
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

  public void updateComparisonString() {
    String alphabetOrder = $dictionary.getAlphabetOrder()
    if (alphabetOrder != null) {
      StringBuilder comparisonString = StringBuilder.new()
      for (Integer i : 0 ..< $name.length()) {
        Integer position = alphabetOrder.indexOf($name.codePointAt(i))
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

  public List<SlimeInformation> sortedInformations() {
    if ($dictionary.getInformationTitleOrder() != null) {
      List<SlimeInformation> sortedInformations = $informations.toSorted() { SlimeInformation firstInformation, SlimeInformation secondInformation ->
        String firstTitle = firstInformation.getTitle()
        String secondTitle = secondInformation.getTitle()
        Integer firstIndex = $dictionary.getInformationTitleOrder().indexOf(firstTitle)
        Integer secondIndex = $dictionary.getInformationTitleOrder().indexOf(secondTitle)
        if (firstIndex == -1) {
          if (secondIndex == -1) {
            return 0
          } else {
            return -1
          }
        } else {
          if (secondIndex == -1) {
            return 1
          } else {
            return firstIndex <=> secondIndex
          }
        }
      }
      return sortedInformations
    } else {
      return $informations
    }
  }

  public Map<String, List<SlimeRelation>> groupedRelations() {
    return $relations.groupBy{it.getTitle()}
  }

  protected void makeContentPaneFactory() {
    $contentPaneFactory = SlimeWordContentPaneFactory.new(this, $dictionary)
  }

  protected void makePlainContentPaneFactory() {
    $plainContentPaneFactory = SlimeWordPlainContentPaneFactory.new(this, $dictionary)
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

  public ContentPaneFactory getPlainContentPaneFactory() {
    if ($plainContentPaneFactory == null) {
      makePlainContentPaneFactory()
    }
    return $plainContentPaneFactory
  }

}