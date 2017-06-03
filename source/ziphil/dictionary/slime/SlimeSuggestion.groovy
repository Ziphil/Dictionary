package ziphil.dictionary.slime

import groovy.transform.CompileStatic
import ziphil.dictionary.ContentPaneFactoryBase
import ziphil.dictionary.SuggestionBase
import ziphil.module.Setting
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class SlimeSuggestion extends SuggestionBase<SlimePossibility> {

  private SlimeDictionary $dictionary

  public void update() {
    changeContentPaneFactory()
  }

  protected ContentPaneFactoryBase createContentPaneFactory() {
    return SlimeSuggestionContentPaneFactory.new(this, $dictionary)
  }

  public SlimeDictionary getDictionary() {
    return $dictionary
  }

  public void setDictionary(SlimeDictionary dictionary) {
    $dictionary = dictionary
  }

}