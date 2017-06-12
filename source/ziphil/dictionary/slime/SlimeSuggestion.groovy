package ziphil.dictionary.slime

import groovy.transform.CompileStatic
import ziphil.dictionary.ContentPaneFactory
import ziphil.dictionary.SuggestionBase
import ziphil.module.ClickType
import ziphil.module.Setting
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class SlimeSuggestion extends SuggestionBase<SlimePossibility> {

  private SlimeDictionary $dictionary

  public void update() {
    changeContentPaneFactory()
  }

  protected ContentPaneFactory createContentPaneFactory() {
    Setting setting = Setting.getInstance()
    ClickType linkClickType = setting.getLinkClickType()
    Boolean persisted = setting.getPersistsContentPanes()
    SlimeSuggestionContentPaneFactory contentPaneFactory = SlimeSuggestionContentPaneFactory.new(this, $dictionary)
    contentPaneFactory.setLinkClickType(linkClickType)
    contentPaneFactory.setPersisted(persisted)
    return contentPaneFactory
  }

  public SlimeDictionary getDictionary() {
    return $dictionary
  }

  public void setDictionary(SlimeDictionary dictionary) {
    $dictionary = dictionary
  }

}