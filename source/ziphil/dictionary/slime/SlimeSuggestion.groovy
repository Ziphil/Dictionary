package ziphil.dictionary.slime

import groovy.transform.CompileStatic
import ziphil.dictionary.PaneFactory
import ziphil.dictionary.SuggestionBase
import ziphil.module.ClickType
import ziphil.module.Setting
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class SlimeSuggestion extends SuggestionBase<SlimePossibility> {

  private SlimeDictionary $dictionary

  public void update() {
    changePaneFactory()
  }

  protected PaneFactory createPaneFactory() {
    Setting setting = Setting.getInstance()
    ClickType linkClickType = setting.getLinkClickType()
    Boolean persisted = setting.getPersistsPanes()
    SlimeSuggestionPaneFactory paneFactory = SlimeSuggestionPaneFactory.new(this, $dictionary)
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

}