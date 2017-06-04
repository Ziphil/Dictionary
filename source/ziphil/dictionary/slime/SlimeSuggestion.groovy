package ziphil.dictionary.slime

import groovy.transform.CompileStatic
import ziphil.dictionary.ContentPaneFactory
import ziphil.dictionary.SuggestionBase
import ziphil.module.Setting
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class SlimeSuggestion extends SuggestionBase<SlimePossibility> {

  private SlimeDictionary $dictionary

  public void update() {
    changeContentPaneFactory()
  }

  protected ContentPaneFactory createContentPaneFactory() {
    Boolean persisted = Setting.getInstance().getPersistsContentPanes()
    return SlimeSuggestionContentPaneFactory.new(this, $dictionary, persisted)
  }

  public SlimeDictionary getDictionary() {
    return $dictionary
  }

  public void setDictionary(SlimeDictionary dictionary) {
    $dictionary = dictionary
  }

}