package ziphil.dictionary.slime

import groovy.transform.CompileStatic
import ziphil.dictionary.SuggestionBase
import ziphil.module.Setting
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class SlimeSuggestion extends SuggestionBase<SlimePossibility> {

  private SlimeDictionary $dictionary

  public void update() {
    changeContentPaneFactory()
  }

  protected void makeContentPaneFactory() {
    $contentPaneFactory = SlimeSuggestionContentPaneFactory.new(this, $dictionary)
  }

  public SlimeDictionary getDictionary() {
    return $dictionary
  }

  public void setDictionary(SlimeDictionary dictionary) {
    $dictionary = dictionary
  }

}