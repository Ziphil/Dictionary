package ziphil.dictionary.shaleia

import groovy.transform.CompileStatic
import ziphil.dictionary.ContentPaneFactory
import ziphil.dictionary.SuggestionBase
import ziphil.module.ClickType
import ziphil.module.Setting
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class ShaleiaSuggestion extends SuggestionBase<ShaleiaPossibility> {

  private ShaleiaDictionary $dictionary

  public void update() {
    changeContentPaneFactory()
  }

  protected ContentPaneFactory createContentPaneFactory() {
    Setting setting = Setting.getInstance()
    ClickType linkClickType = setting.getLinkClickType()
    Boolean persisted = setting.getPersistsContentPanes()
    ShaleiaSuggestionContentPaneFactory contentPaneFactory = ShaleiaSuggestionContentPaneFactory.new(this, $dictionary)
    contentPaneFactory.setLinkClickType(linkClickType)
    contentPaneFactory.setPersisted(persisted)
    return contentPaneFactory
  }

  public ShaleiaDictionary getDictionary() {
    return $dictionary
  }

  public void setDictionary(ShaleiaDictionary dictionary) {
    $dictionary = dictionary
  }

}