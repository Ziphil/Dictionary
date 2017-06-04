package ziphil.dictionary.shaleia

import groovy.transform.CompileStatic
import ziphil.dictionary.ContentPaneFactory
import ziphil.dictionary.SuggestionBase
import ziphil.module.Setting
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class ShaleiaSuggestion extends SuggestionBase<ShaleiaPossibility> {

  private ShaleiaDictionary $dictionary

  public void update() {
    changeContentPaneFactory()
  }

  protected ContentPaneFactory createContentPaneFactory() {
    return ShaleiaSuggestionContentPaneFactory.new(this, $dictionary)
  }

  public ShaleiaDictionary getDictionary() {
    return $dictionary
  }

  public void setDictionary(ShaleiaDictionary dictionary) {
    $dictionary = dictionary
  }

}