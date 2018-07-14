package ziphil.dictionary.slime

import groovy.transform.CompileStatic
import ziphil.dictionary.AlphabetOrderType
import ziphil.dictionary.PaneFactory
import ziphil.dictionary.WordBase
import ziphil.custom.ClickType
import ziphil.module.Setting
import ziphil.module.akrantiain.Akrantiain
import ziphil.module.akrantiain.AkrantiainException
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class SlimeWord extends WordBase {

  private SlimeDictionary $dictionary
  private Int $id = -1
  private List<SlimeEquivalent> $rawEquivalents = ArrayList.new()
  private List<String> $tags = ArrayList.new()
  private List<SlimeInformation> $informations = ArrayList.new()
  private List<SlimeVariation> $variations = ArrayList.new()
  private List<SlimeRelation> $relations = ArrayList.new()
  private String $comparisonString = ""

  public void update() {
    updateEquivalents()
    updateContent()
    updateComparisonString()
    changePaneFactory()
    changePlainPaneFactory()
  }

  public void change() {
    changePaneFactory()
    changePlainPaneFactory()
  }

  private void updateEquivalents() {
    $equivalents.clear()
    for (SlimeEquivalent equivalent : $rawEquivalents) {
      $equivalents.addAll(equivalent.getNames())
    }
  }

  private void updateContent() {
    StringBuilder content = StringBuilder.new()
    content.append($name)
    content.append("\n")
    for (SlimeEquivalent equivalent : $rawEquivalents) {
      for (String equivalentName : equivalent.getNames()) {
        content.append(equivalentName)
        content.append("\n")
      }
    }
    for (SlimeInformation information : $informations) {
      content.append(information.getText())
      content.append("\n")
    }
    $content = content.toString()
  }

  public void updateComparisonString() {
    String alphabetOrder = $dictionary.getAlphabetOrder()
    AlphabetOrderType alphabetOrderType = $dictionary.getAlphabetOrderType()
    if (alphabetOrderType == AlphabetOrderType.CUSTOM) {
      if (alphabetOrder != null) {
        StringBuilder comparisonString = StringBuilder.new()
        for (Int i = 0 ; i < $name.length() ; i ++) {
          Int position = alphabetOrder.indexOf($name.codePointAt(i))
          if (position >= 0) {
            comparisonString.appendCodePoint(position + 174)
          }
        }
        $comparisonString = comparisonString.toString()
      } else {
        $comparisonString = ""
      }
    } else if (alphabetOrderType == AlphabetOrderType.UNICODE) {
      $comparisonString = $name
    } else {
      $comparisonString = ""
    }
  }

  // word に渡された単語オブジェクトの ID と単語名以外のデータを、このオブジェクトに追加します。
  // このメソッドは、セッターと同様にオブジェクトの内部データの更新を行わないので、一連の更新処理が終わった後は必ず update メソッドを呼んでください。 
  public void merge(SlimeWord word) {
    $rawEquivalents.addAll(word.getRawEquivalents())
    $tags.addAll(word.getTags())
    $informations.addAll(word.getInformations())
    $variations.addAll(word.getVariations())
    $relations.addAll(word.getRelations())
  }

  public List<SlimeInformation> sortedInformations() {
    List<SlimeInformation> sortedInformations = ArrayList.new($informations)
    if ($dictionary.getPronunciationTitle() != null) {
      sortedInformations.removeAll{it.getTitle() == $dictionary.getPronunciationTitle()}
    }
    if ($dictionary.getInformationTitleOrder() != null) {
      sortedInformations.sort() { SlimeInformation firstInformation, SlimeInformation secondInformation ->
        String firstTitle = firstInformation.getTitle()
        String secondTitle = secondInformation.getTitle()
        Int firstIndex = $dictionary.getInformationTitleOrder().indexOf(firstTitle)
        Int secondIndex = $dictionary.getInformationTitleOrder().indexOf(secondTitle)
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
    }
    return sortedInformations
  }

  public Map<String, List<SlimeVariation>> groupedVariations() {
    return $variations.groupBy{it.getTitle()}
  }

  public Map<String, List<SlimeRelation>> groupedRelations() {
    return $relations.groupBy{it.getTitle()}
  }

  public String createPronunciation() {
    String pronunciation = ""
    String pronunciationTitle = $dictionary.getPronunciationTitle()
    if (pronunciationTitle != null) {
      for (SlimeInformation information : $informations) {
        if (information.getTitle() == pronunciationTitle) {
          pronunciation = information.getText().replaceAll(/(\n|\r)/, "")
          if (!pronunciation.startsWith("/") && !pronunciation.startsWith("[")) {
            pronunciation = "/" + pronunciation
          }
          if (!pronunciation.endsWith("/") && !pronunciation.endsWith("]")) {
            pronunciation = pronunciation + "/"
          }
          break
        }
      }
    }
    if (pronunciation.isEmpty()) {
      Akrantiain akrantiain = $dictionary.getAkrantiain()
      if (akrantiain != null) {
        try {
          pronunciation = akrantiain.convert(name)
          pronunciation = "/" + pronunciation + "/"
        } catch (AkrantiainException exception) {
          pronunciation = ""
        }
      }
    }
    return pronunciation
  }

  protected PaneFactory createPaneFactory() {
    Setting setting = Setting.getInstance()
    ClickType linkClickType = setting.getLinkClickType()
    Boolean persisted = setting.getPersistsPanes()
    SlimeWordPaneFactory paneFactory = SlimeWordPaneFactory.new(this, $dictionary)
    paneFactory.setLinkClickType(linkClickType)
    paneFactory.setPersisted(persisted)
    return paneFactory
  }

  protected PaneFactory createPlainPaneFactory() {
    Setting setting = Setting.getInstance()
    ClickType linkClickType = setting.getLinkClickType()
    Boolean persisted = setting.getPersistsPanes()
    SlimeWordPlainPaneFactory paneFactory = SlimeWordPlainPaneFactory.new(this, $dictionary)
    paneFactory.setLinkClickType(linkClickType)
    paneFactory.setPersisted(persisted)
    return paneFactory
  }

  public SlimeDictionary getDictionary() {
    return $dictionary
  }

  public void setDictionary(SlimeDictionary dictionary) {
    $dictionary = dictionary
  }

  public Int getId() {
    return $id
  }

  public void setId(Int id) {
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

  public String getIdentifier() {
    return IntegerClass.toString($id)
  }

  public String getComparisonString() {
    return $comparisonString
  }

}