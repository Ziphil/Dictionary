package ziphil.dictionary.shaleia

import groovy.transform.CompileStatic
import java.util.regex.Matcher
import ziphil.dictionary.PaneFactory
import ziphil.dictionary.WordBase
import ziphil.custom.ClickType
import ziphil.module.Setting
import ziphil.module.akrantiain.Akrantiain
import ziphil.module.akrantiain.AkrantiainException
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class ShaleiaWord extends WordBase {

  private String $uniqueName = ""
  private String $description = ""
  private String $comparisonString = ""
  private ShaleiaDictionary $dictionary

  public void update() {
    updateName()
    updateEquivalents()
    updateContent()
    updateComparisonString()
    changePaneFactory()
  }

  private void updateName() {
    $name = ($uniqueName.startsWith("\$")) ? "\$" : $uniqueName.replaceAll(/\+|~/, "")
  }

  private void updateEquivalents() {
    BufferedReader reader = BufferedReader.new(StringReader.new($description))
    try {
      $equivalents.clear()
      for (String line ; (line = reader.readLine()) != null ;) {
        Matcher matcher = line =~ /^\=(?:\:)?\s*(?:〈(.+)〉)?\s*(.+)$/
        if (matcher.matches()) {
          String equivalent = matcher.group(2)
          List<String> equivalents = equivalent.replaceAll(/(\(.+\)|\{|\}|\/|～|\s)/, "").split(/,/).toList()
          $equivalents.addAll(equivalents)
        }
      }
    } finally {
      reader.close()
    }
  }

  private void updateContent() {
    $content = $uniqueName + "\n" + $description
  }

  public void updateComparisonString() {
    String alphabetOrder = $dictionary.getAlphabetOrder()
    Boolean apostropheCharacter = alphabetOrder.contains("'")
    StringBuilder comparisonString = StringBuilder.new()
    for (Int i = 0 ; i < $uniqueName.length() ; i ++) {
      String character = $uniqueName[i]
      if ((apostropheCharacter || character != "'") && character != "+" && character != "~" && character != "-") {
        if (character != "\$") {
          Int position = alphabetOrder.indexOf($uniqueName.codePointAt(i))
          if (position >= 0) {
            comparisonString.appendCodePoint(position + 174)
          } else {
            comparisonString.appendCodePoint(10000)
          }
        } else {
          comparisonString.appendCodePoint(11000)
        }
      }
    }
    $comparisonString = comparisonString.toString()
  }

  public String createPronunciation() {
    String pronunciation = ""
    Akrantiain akrantiain = $dictionary.getAkrantiain()
    if (akrantiain != null) {
      try {
        pronunciation = akrantiain.convert(name)
        pronunciation = "/" + pronunciation + "/"
      } catch (AkrantiainException exception) {
        pronunciation = ""
      }
    }
    return pronunciation
  }

  protected PaneFactory createPaneFactory() {
    Setting setting = Setting.getInstance()
    ClickType linkClickType = setting.getLinkClickType()
    Boolean persisted = setting.getPersistsPanes()
    ShaleiaWordPaneFactory paneFactory = ShaleiaWordPaneFactory.new(this, $dictionary)
    paneFactory.setLinkClickType(linkClickType)
    paneFactory.setPersisted(persisted)
    return paneFactory
  }

  protected PaneFactory createPlainPaneFactory() {
    Setting setting = Setting.getInstance()
    ClickType linkClickType = setting.getLinkClickType()
    Boolean persisted = setting.getPersistsPanes()
    ShaleiaWordPlainPaneFactory paneFactory = ShaleiaWordPlainPaneFactory.new(this, $dictionary)
    paneFactory.setLinkClickType(linkClickType)
    paneFactory.setPersisted(persisted)
    return paneFactory
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

  public void setUniqueName(String uniqueName) {
    $uniqueName = uniqueName
  }

  public String getDescription() {
    return $description
  }

  public void setDescription(String description) {
    $description = description
  }

  public String getIdentifier() {
    return $uniqueName
  }

  public String getComparisonString() {
    return $comparisonString
  }

}