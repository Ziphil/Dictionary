package ziphil.dictionary.shaleia

import groovy.transform.CompileStatic
import ziphil.dictionary.PaneFactory
import ziphil.dictionary.SuggestionBase
import ziphil.custom.ClickType
import ziphil.module.Setting
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class ShaleiaSuggestion extends SuggestionBase<ShaleiaPossibility> {

  private ShaleiaDictionary $dictionary

  public void update() {
    changePaneFactory()
  }

  protected PaneFactory createPaneFactory() {
    Setting setting = Setting.getInstance()
    ClickType linkClickType = setting.getLinkClickType()
    Boolean persisted = setting.getPersistsPanes()
    ShaleiaSuggestionPaneFactory paneFactory = ShaleiaSuggestionPaneFactory.new(this, $dictionary)
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

}